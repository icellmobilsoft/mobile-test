//
//  StopDetailsViewModel.swift
//  icelltest
//
//  Created by Balázs on 2019. 12. 19..
//  Copyright © 2019. trial. All rights reserved.
//

import Foundation
import RxSwift
import RxCocoa
import RxDataSources
import RxSwiftExt

extension StopsSection: SectionModelType {
    typealias Item = Stop
    
    init(original: StopsSection, items: [Item]) {
        self = original
        self.items = items
    }
}

protocol StopDetailsViewModelProtocol {
    // MARK: - Input
    var loadStops: PublishRelay<String> { get }
    var selectDirection: PublishRelay<Int> { get }
    var loadDepartures: PublishRelay<String> { get }
    // MARK: - Output
    var sections$: Observable<[StopsSection]> { get }
    var showActivity$: Observable<Bool> { get }
    var headings$: Observable<[String]> { get }
    var departures$: Observable<String> { get }
}

class StopDetailsViewModel: StopDetailsViewModelProtocol {
    // MARK: - Input
    var loadStops: PublishRelay<String> = PublishRelay()
    var selectDirection: PublishRelay<Int> = PublishRelay()
    var loadDepartures: PublishRelay<String> = PublishRelay()
    // MARK: - Output
    var sections$ = Observable<[StopsSection]>.never()
    var showActivity$ = Observable<Bool>.never()
    var headings$ = Observable<[String]>.never()
    var departures$ = Observable<String>.never()
    
    // MARK: - Internal
    private var disposeBag = DisposeBag()
    
    init(stopService: StopServiceProtocol,
         alertPresenter: AlertPresenterProtocol) {
        let showActivitySubject = BehaviorSubject<Bool>(value: false)
        showActivity$ = showActivitySubject.asObservable()
        
        let stopResponse$ = loadStops.asObservable()
            .flatMapLatest { (routeId: String) -> Observable<StopsResponse> in
                return queryRouteVariant(
                    routeId: routeId,
                    stopService: stopService,
                    activitySubject: showActivitySubject)
        }
        .share(replay: 1)
        
        let stopResponseSubject = ReplaySubject<StopsResponse>.create(bufferSize: 1)
        let selectedHeadingSubject = ReplaySubject<String>.create(bufferSize: 1)
        sections$ = Observable.combineLatest(
            stopResponse$,
            selectDirection.asObservable().startWith(0))
            .map({ (params: (response: StopsResponse, index: Int)) -> [Stop] in
                stopResponseSubject.onNext(params.response)
                let selectedRouteVariant = params.response.variants[params.index]
                selectedHeadingSubject.onNext(selectedRouteVariant.headsign)
                return selectedRouteVariant.stopIds
                    .compactMap { params.response.stops[$0] }
                
            })
            .map({ (stops: [Stop]) -> [StopsSection] in
                return stops.count > 0 ? [StopsSection(header: "", items: stops)] : []
            })
        
        headings$ =
            stopResponseSubject
                .map({ $0.variants.map { $0.headsign } })
        .take(1)
        
        departures$ = Observable.combineLatest(
            selectedHeadingSubject.asObservable(),
            loadDepartures.asObservable())
        .flatMapLatest({ (params: (selectedHeading: String, stopId: String)) -> Observable<[Departure]> in
            return queryDepatures(
                stopId: params.stopId,
                stopService: stopService,
                activitySubject: showActivitySubject)
                .map { (departures: [Departure]) -> [Departure] in
                    return departures.filter { $0.stopHeadsign == params.selectedHeading }
                }
            })
            .map({ (departures: [Departure]) -> String in
                guard departures.count > 0 else {
                    return "no departures in the next 90 minutes "
                }
                let departureTimes = departures
                    .map({ (departure: Departure) -> String in
                        if let departureTime = departure.departureTime {
                            return timeFormatter.string(from: departure.predictedDepartureTime ?? departureTime)
                        }
                        return "-"
                })
                return departureTimes.joined(separator: ", ")
            })
            
        timeFormatter = setupTimeFormatter()
    }
}

private var timeFormatter = DateFormatter()
func setupTimeFormatter() -> DateFormatter {
    let formatter = DateFormatter()
    formatter.dateStyle = .none
    formatter.timeStyle = .short
    return formatter
}

private func queryRouteVariant(
    routeId: String,
    stopService: StopServiceProtocol,
    activitySubject: BehaviorSubject<Bool>) -> Observable<StopsResponse> {
    
    return Observable.just(())
        .do(onNext: { _ in
            activitySubject.onNext(true)
        })
        .flatMapLatest({ _ -> Observable<StopsResponse> in
            return stopService.queryRouteVariant(routeId: routeId)
        })
        .do(onNext: { _ in
            activitySubject.onNext(false)
        })
}

private func queryDepatures(
    stopId: String,
    stopService: StopServiceProtocol,
    activitySubject: BehaviorSubject<Bool>) -> Observable<[Departure]> {
    
    return Observable.just(())
        .do(onNext: { _ in
            activitySubject.onNext(true)
        })
        .flatMapLatest({ _ -> Observable<[Departure]> in
            return stopService.queryDepartures(stopId: stopId)
                .map { $0.departures }
        })
        .do(onNext: { _ in
            activitySubject.onNext(false)
        })
}
