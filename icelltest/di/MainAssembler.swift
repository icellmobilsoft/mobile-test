//
//  MainAssembler.swift
//  icelltest
//
//  Created by Balázs on 2019. 12. 12..
//  Copyright © 2019. trial. All rights reserved.
//

import Foundation
import RxSwift
import Alamofire
import Moya
import Swinject
import SwinjectStoryboard

enum DependencyInjectionError: Error {
    case cannotResolveDependencies(String)
}

class MainAssembler {
    public static var instance: MainAssembler! = nil

    var resolver: Resolver {
        return assembler.resolver
    }
    private let assembler = Assembler(container: SwinjectStoryboard.defaultContainer)

    // swiftlint:disable force_cast
    private init(withAssembly assembly: Assembly) {
        assembler.apply(assembly: assembly)
    }

    static func create(withAssembly assembly: Assembly) -> MainAssembler {
        instance = MainAssembler(withAssembly: assembly)
        return instance
    }

    func dispose() {
        SwinjectStoryboard.defaultContainer.removeAll()
    }
}
// swiftlint:disable function_body_length file_length
class MainAssembly: Assembly {

    func assemble(container: Container) {
        assembleCommon(container: container)
        assembleRoute(container: container)
        assembleStop(container: container)
    }
    
    func assembleCommon(container: Container) {
        container.register(AlertPresenterProtocol.self) { _ in
            return AlertPresenter()
        }.inObjectScope(.container)
        
        container.register(ActivityPresenterProtocol.self) { _ in
            return SearchBarActivityPresenter()
        }.inObjectScope(.transient)
        
        container.register(MoyaProvider<MultiTarget>.self) { resolver in
            guard
                let manager = resolver.resolve(Manager.self) else {
                preconditionFailure("Failed to instantiate dependencies")
            }
            return MoyaProvider<MultiTarget>(
                manager: manager,
                plugins: [NetworkLoggerPlugin(verbose: true)])
        }.inObjectScope(.container)
        
        container.register(Manager.self) { _ in
            return Manager(configuration: URLSessionConfiguration.default)
        }.inObjectScope(.container)
    }
    
    func assembleRoute(container: Container) {
        container.storyboardInitCompleted(RoutesViewController.self) { resolver, viewController in
            viewController.viewModel = resolver.resolve(RoutesViewModelProtocol.self)!
            viewController.activityPresenter = resolver.resolve(ActivityPresenterProtocol.self)!
        }

        container.register(RoutesViewModelProtocol.self) { resolver in
            guard
                let routeService = resolver.resolve(RouteServiceProtocol.self),
                let alertPresenter = resolver.resolve(AlertPresenterProtocol.self) else {
                    preconditionFailure("Failed to instantiate dependencies")
            }
            return RoutesViewModel(
                routeService: routeService,
                alertPresenter: alertPresenter)
        }.inObjectScope(.transient)

        container.register(RouteServiceProtocol.self) { resolver in
            guard let moya = resolver.resolve(MoyaProvider<MultiTarget>.self) else {
                preconditionFailure("Failed to instantiate dependencies")
            }
            return RouteService(moya: moya)
        }.inObjectScope(.container)
    }
    
    func assembleStop(container: Container) {
        container.storyboardInitCompleted(StopDetailsViewController.self) { resolver, viewController in
            viewController.viewModel = resolver.resolve(StopDetailsViewModelProtocol.self)!
        }

        container.register(StopDetailsViewModelProtocol.self) { resolver in
            guard
                let stopService = resolver.resolve(StopServiceProtocol.self),
                let alertPresenter = resolver.resolve(AlertPresenterProtocol.self) else {
                    preconditionFailure("Failed to instantiate dependencies")
            }
            return StopDetailsViewModel(
                stopService: stopService,
                alertPresenter: alertPresenter)
        }.inObjectScope(.transient)

        container.register(StopServiceProtocol.self) { resolver in
            guard let moya = resolver.resolve(MoyaProvider<MultiTarget>.self) else {
                preconditionFailure("Failed to instantiate dependencies")
            }
            return StopService(moya: moya)
        }.inObjectScope(.container)
    }
}
