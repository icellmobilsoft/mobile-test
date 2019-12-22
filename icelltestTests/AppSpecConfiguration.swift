//
//  AppSpecConfiguration.swift
//  icelltestTests
//
//  Created by Balázs on 2019. 12. 21..
//  Copyright © 2019. trial. All rights reserved.
//

@testable import Departures
import Nimble
import Quick

class EKretaSpecConfiguration: QuickConfiguration {
    override class func configure(_ configuration: Configuration) {
        Nimble.AsyncDefaults.Timeout = 5
        Nimble.AsyncDefaults.PollInterval = 0.1
    }
}
