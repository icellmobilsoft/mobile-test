//
//  UIWindow+App.swift
//  icelltest
//
//  Created by Balázs on 2019. 12. 17..
//  Copyright © 2019. trial. All rights reserved.
//

import Foundation
import UIKit

extension UIWindow {
    func topViewController() -> UIViewController? {
        var top = self.rootViewController
        while true {
            if let presented = top?.presentedViewController {
                top = presented
            } else if let nav = top as? UINavigationController {
                top = nav.visibleViewController
            } else {
                break
            }
        }
        return top
    }
}
