//
//  AlertPresenter.swift
//  icelltest
//
//  Created by Balázs on 2019. 12. 17..
//  Copyright © 2019. trial. All rights reserved.
//

import UIKit
import RxSwift

struct AppAlertAction {
    var title: String? {
        return wrappedAction.title
    }
    var style: UIAlertAction.Style {
        return wrappedAction.style
    }
    var handler: ((UIAlertAction) -> Void)?
    var nativeAction: UIAlertAction {
        return wrappedAction
    }
    private var wrappedAction: UIAlertAction

    init(title: String?, style: UIAlertAction.Style, handler: ((UIAlertAction) -> Swift.Void)? = nil) {
        self.handler = handler
        self.wrappedAction = UIAlertAction(title: title, style: style, handler: handler)
    }
}

protocol AlertPresenterProtocol {
    func showAlert(
            alertStyle: UIAlertController.Style,
            withTitle title: String?,
            message: String?,
            actions: [AppAlertAction]) -> Single<AppAlertAction>
}

extension AlertPresenterProtocol {
    func showAlert(
            alertStyle: UIAlertController.Style = .alert,
            withTitle title: String?,
            message: String?,
            actions: [AppAlertAction]) -> Single<AppAlertAction> {
        return showAlert(alertStyle: alertStyle, withTitle: title, message: message, actions: actions)
    }
}

class AlertPresenter: AlertPresenterProtocol {
    func showAlert(
            alertStyle: UIAlertController.Style = .alert,
            withTitle title: String?,
            message: String?,
            actions: [AppAlertAction]) -> Single<AppAlertAction> {
        return Single<AppAlertAction>.create { single in
            guard let keyWindow = UIApplication.shared.keyWindow else {
                preconditionFailure("Expected keyWindow to exist")
            }

            guard let topViewController = keyWindow.topViewController() else {
                preconditionFailure("Expected topViewContoller to exist")
            }
            
            let alert = UIAlertController(
                    title: title,
                    message: message,
                    preferredStyle: alertStyle)
            for action in actions {
                alert.addAction(UIAlertAction(
                        title: action.title,
                        style: action.style,
                        handler: { (nativeAction: UIAlertAction) in
                            if let handler = action.handler {
                                handler(nativeAction)
                            }
                            single(.success(action))
                        }))
            }
            
            topViewController.present(alert, animated: true)
            return Disposables.create {
                alert.dismiss(animated: true, completion: nil)
            }
        }
    }
}
