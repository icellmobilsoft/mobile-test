//
//  BkkApi.swift
//  icelltest
//
//  Created by Balázs on 2019. 12. 12..
//  Copyright © 2019. trial. All rights reserved.
//

import Foundation
import Moya

enum BkkApi {
    case queryRoutes(searchText: String)
    case queryStopsForRoute(routeId: String)
    case queryArrivalsAndDepartures(stops: [String], onlyDepartures: Bool)
}

extension BkkApi: TargetType {
    
    var baseURL: URL {
        return URL(string: "https://futar.bkk.hu/api/query/v1/ws/otp/api/where")!
    }
    
    var path: String {
        switch self {
        case .queryRoutes:
            return "/search.json"
        case .queryStopsForRoute:
            return "/route-details.json"
        case .queryArrivalsAndDepartures:
            return "/arrivals-and-departures-for-stop.json"
        }
    }
    
    var method: Moya.Method {
        return .get
    }
    
    var task: Task {
        var params: [String: Any] = [
            "key": "bkk-web",
            "version": 3,
            "appVersion": "apiary-1.0",
            "includedReferences": "stops"]
        
        switch self {
        case let .queryRoutes(searchText):
            params["query"] = searchText
        case let .queryStopsForRoute(routeId):
            params["routeId"] = routeId
            params["related"] = "stops"
        case let .queryArrivalsAndDepartures(stops, onlyDepartures):
            params["related"] = "false"
            params["includedReferences"] = "false"
            params["stopId"] = stops.joined(separator: ",")
            params["onlyDepartures"] = onlyDepartures
            params["limit"] = 4
            params["minutesBefore"] = 5
            params["minutesAfter"] = 90
        }
        
        return .requestParameters(parameters: params, encoding: URLEncoding.queryString)
    }
    
    var sampleData: Data {
        return Data()
    }
    
    var validationType: ValidationType {
        return .successCodes
    }
    
    var headers: [String: String]? {
        return nil
    }
}
