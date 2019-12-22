//
//  Route.swift
//  icelltest
//
//  Created by Balázs on 2019. 12. 20..
//  Copyright © 2019. trial. All rights reserved.
//

import Foundation

struct Route: Decodable {
    var id: String
    var shortName: String
    var longName: String?
    var description: String
    var type: String
}

struct RoutesResponse: Decodable {
    var routes: [Route]
    
    enum CodingKeys: String, CodingKey {
        case data
        case references
        case routes
    }
    
    init(from decoder: Decoder) throws {
        let responseContainer = try decoder.container(keyedBy: CodingKeys.self)
        let dataContainer = try responseContainer.nestedContainer(keyedBy: CodingKeys.self, forKey: .data)
        let referencesContainer = try dataContainer.nestedContainer(keyedBy: CodingKeys.self, forKey: .references)
        let routesDict = try referencesContainer.decode([String: Route].self, forKey: .routes)
        routes = routesDict.map { $1 }
     }
}
