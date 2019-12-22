//
//  RouteService.swift
//  icelltest
//
//  Created by Balázs on 2019. 12. 18..
//  Copyright © 2019. trial. All rights reserved.
//

import Foundation
import Moya
import RxSwift

protocol RouteServiceProtocol {
    func queryRoutes(searchText: String) -> Observable<[Route]>
}

class RouteService: RouteServiceProtocol {
    var moya: MoyaProvider<MultiTarget>!
   
    init(moya: MoyaProvider<MultiTarget>) {
        self.moya = moya
    }
    
    func queryRoutes(searchText: String) -> Observable<[Route]> {
        let multiTarget = MultiTarget(BkkApi.queryRoutes(searchText: searchText))
        return moya.rx.request(multiTarget).asObservable()
        .map(RoutesResponse.self)
        .map({ $0.routes })
        .catchError({ (error: Error) -> Observable<[Route]> in
            let eKretaError = apiError(fromError: error)
            return Observable.error(eKretaError)
        })
    }
}
