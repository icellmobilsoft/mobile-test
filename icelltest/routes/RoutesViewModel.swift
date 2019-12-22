//
//  RoutesViewModel.swift
//  icelltest
//
//  Created by Balázs on 2019. 12. 17..
//  Copyright © 2019. trial. All rights reserved.
//

import Foundation
import RxSwift
import RxCocoa
import RxDataSources

struct RoutesSection {
    var header: String
    var items: [Item]
}
    
extension RoutesSection: SectionModelType {
    typealias Item = Route
    
    init(original: RoutesSection, items: [Item]) {
        self = original
        self.items = items
    }
}

protocol RoutesViewModelProtocol {
    // MARK: - Input
    var searchRoute: PublishRelay<String> { get }
    // MARK: - Output
    var sections$: Observable<[RoutesSection]> { get }
    var showActivity$: Observable<Bool> { get }
}

class RoutesViewModel: RoutesViewModelProtocol {
    // MARK: - Input
    var searchRoute: PublishRelay<String> = PublishRelay()
    // MARK: - Output
    var sections$ = Observable<[RoutesSection]>.never()
    var showActivity$ = Observable<Bool>.never()
    
    // MARK: - Internal
    private var disposeBag = DisposeBag()
    
    init(routeService: RouteServiceProtocol,
         alertPresenter: AlertPresenterProtocol) {
        let showActivitySubject = BehaviorSubject<Bool>(value: false)
        sections$ = searchRoute.asObservable()
            .debounce(0.6, scheduler: MainScheduler.instance)
            .distinctUntilChanged()
            .flatMapLatest({ (searchText: String) -> Observable<[RoutesSection]> in
                return queryRoutes(
                    query: searchText,
                    routeService: routeService,
                    activitySubject: showActivitySubject)
                    .map(routesToSections)
            })
            .observeOn(MainScheduler.instance)
            .catchError({ (error: Error) -> Observable<[RoutesSection]> in
                showActivitySubject.onNext(false)
                if let alertDetails = alertDetails(from: error) {
                    return alertPresenter.showAlert(
                        withTitle: alertDetails.title,
                        message: alertDetails.message,
                        actions: [AppAlertAction(title: "Ok", style: .cancel, handler: { _ in })]).asObservable()
                        .flatMap({ _ -> Observable<[RoutesSection]> in
                            return Observable.empty()
                        })
                }
                return Observable.empty()
            })
            .retry()
            .share(replay: 1)
        
        showActivity$ = showActivitySubject.asObservable()
    }
}

private func queryRoutes(
    query: String,
    routeService: RouteServiceProtocol,
    activitySubject: BehaviorSubject<Bool>) -> Observable<[Route]> {
    
    return Observable.just(())
        .do(onNext: { _ in
            activitySubject.onNext(true)
        })
        .flatMapLatest({ _ -> Observable<[Route]> in
            return routeService.queryRoutes(searchText: query)
        })
        .do(onNext: { _ in
            activitySubject.onNext(false)
        })
}

private func routesToSections(_ routes: [Route]) -> [RoutesSection] {
    return routes.count > 0 ? [RoutesSection(header: "", items: routes)] : []
}

private func alertDetails(from error: Error) -> (title: String, message: String)? {
    switch apiError(fromError: error) {
    case .disconnected:
        return disconnectedErrorMessage()
    case .unexpected:
        return unexpectedErrorMessage()
    }
}

func apiError(fromError error: Error) -> ApiError {
    return .unexpected
}

func disconnectedErrorMessage() -> (title: String, message: String) {
    return (title: "Alert",
            message: "There is no network connection.")
}

func unexpectedErrorMessage() -> (title: String, message: String) {
    return (title: "Alert",
            message: "Unexpected error happened")
}
