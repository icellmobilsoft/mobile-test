//
//  Stop.swift
//  icelltest
//
//  Created by Balázs on 2019. 12. 20..
//  Copyright © 2019. trial. All rights reserved.
//

import Foundation

struct Stop: Decodable {
    var id: String
    var name: String
}

struct RouteVariant: Decodable {
    var name: String
    var stopIds: [String]
    var direction: String
    var headsign: String
}

struct StopsResponse: Decodable {
    var variants: [RouteVariant]
    var stops: [String: Stop]
    
    enum CodingKeys: String, CodingKey {
        case data
        case entry
        case variants
        case references
        case stops
    }
    
    init(from decoder: Decoder) throws {
        let responseContainer = try decoder.container(keyedBy: CodingKeys.self)
            let dataContainer = try responseContainer.nestedContainer(keyedBy: CodingKeys.self, forKey: .data)
            let entryContainer = try dataContainer.nestedContainer(keyedBy: CodingKeys.self, forKey: .entry)
            var variantsContainer = try entryContainer.nestedUnkeyedContainer(forKey: .variants)

            variants = []
            while !variantsContainer.isAtEnd {
                do {
                    variants.append(try variantsContainer.decode(RouteVariant.self))
                }
            }
            
            let referencesContainer = try dataContainer.nestedContainer(keyedBy: CodingKeys.self, forKey: .references)
            stops = try referencesContainer.decode([String: Stop].self, forKey: .stops)
     }
}
