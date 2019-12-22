//
//  StopService.swift
//  icelltest
//
//  Created by Balázs on 2019. 12. 20..
//  Copyright © 2019. trial. All rights reserved.
//

import Foundation
import Moya
import RxSwift

protocol StopServiceProtocol {
    func queryRouteVariant(routeId: String) -> Observable<StopsResponse>
    func queryDepartures(stopId: String) -> Observable<DeparturesResponse>
}

class StopService: StopServiceProtocol {
    var moya: MoyaProvider<MultiTarget>!
    var decoder: JSONDecoder
   
    init(moya: MoyaProvider<MultiTarget>) {
        self.moya = moya
        decoder = JSONDecoder()
        decoder.dateDecodingStrategy = .secondsSince1970
    }
    
    func queryRouteVariant(routeId: String) -> Observable<StopsResponse> {
        let multiTarget = MultiTarget(BkkApi.queryStopsForRoute(routeId: routeId))
        return moya.rx.request(multiTarget).asObservable()
        .map(StopsResponse.self)
        .catchError({ (error: Error) -> Observable<StopsResponse> in
            let eKretaError = apiError(fromError: error)
            return Observable.error(eKretaError)
        })
    }
    
    func queryDepartures(stopId: String) -> Observable<DeparturesResponse> {
        let multiTarget = MultiTarget(BkkApi.queryArrivalsAndDepartures(stops: [stopId], onlyDepartures: true))
        return moya.rx.request(multiTarget).asObservable()
            .map(DeparturesResponse.self, using: decoder)
            .catchError({ (error: Error) -> Observable<DeparturesResponse> in
                let eKretaError = apiError(fromError: error)
                return Observable.error(eKretaError)
            })
    }
}
