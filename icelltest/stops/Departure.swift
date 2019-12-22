//
//  Departure.swift
//  icelltest
//
//  Created by Balázs on 2019. 12. 21..
//  Copyright © 2019. trial. All rights reserved.
//

import Foundation

struct Departure: Decodable {
    var stopHeadsign: String
    var departureTime: Date?
    var predictedDepartureTime: Date?
}

struct DeparturesResponse: Decodable {
    var departures: [Departure]
    
    enum CodingKeys: String, CodingKey {
        case data
        case entry
        case stopTimes
    }
    
    init(from decoder: Decoder) throws {
        let responseContainer = try decoder.container(keyedBy: CodingKeys.self)
            let dataContainer = try responseContainer.nestedContainer(keyedBy: CodingKeys.self, forKey: .data)
            let entryContainer = try dataContainer.nestedContainer(keyedBy: CodingKeys.self, forKey: .entry)
            var stopTimesContainer = try entryContainer.nestedUnkeyedContainer(forKey: .stopTimes)

            departures = []
            while !stopTimesContainer.isAtEnd {
                do {
                    departures.append(try stopTimesContainer.decode(Departure.self))
                }
            }
     }
}
