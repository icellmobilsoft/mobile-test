//
//  RoutesViewControllerSpec.swift
//  icelltestTests
//
//  Created by Balázs on 2019. 12. 21..
//  Copyright © 2019. trial. All rights reserved.
//

@testable import Departures
import Nimble
import Quick
import Swinject
import SwinjectStoryboard
import UIKit
import RxSwift
import RxSwiftUtilities
import RxCocoa

// swiftlint:disable file_length
// swiftlint:disable force_cast
class RoutesViewControllerSpec: QuickSpec {
    
    // swiftlint:disable function_body_length
    override func spec() {
        describe("RoutesViewControllerSpec") {
            var sut: RoutesViewController!
            var mockViewModel: MockRoutesViewModel!
            var mockActivityPresenter: MockActivityPresenter!
            var disposeBag: DisposeBag!
            var assembler: MainAssembler!
            
            beforeEach {
                assembler = MainAssembler.create(withAssembly: TestAssembly())
                let storyboard = SwinjectStoryboard.create(name: "Main", bundle: nil)
                sut = storyboard.instantiateViewController(withIdentifier: "RoutesViewController") as? RoutesViewController
                mockViewModel = sut.viewModel as? MockRoutesViewModel
                sut.activityPresenter = assembler.resolver.resolve(MockActivityPresenter.self)
                mockActivityPresenter = sut.activityPresenter as? MockActivityPresenter
                disposeBag = DisposeBag()
            }
            
            afterEach {
                disposeBag = nil
                assembler.dispose()
            }
            
            it("can be instantiated form storyboard") {
                expect(sut).toNot(beNil())
            }
            
            context("when the view is loaded") {
                beforeEach {
                    sut.loadViewIfNeeded()
                }
                
                it("makes the search bar active with the keyboard on") {
                    // when
                    let window = UIWindow()
                    window.rootViewController = sut
                    window.makeKeyAndVisible()
                    //then
                    expect(sut.searchBar.isFirstResponder).to(beTrue())
                }
                
                it("forwards the search term to the viewModel when the user enters a search term") {
                    let theSearchTerm = "aSearchTerm"
                    sut.searchBar.text = theSearchTerm
                    sut.searchBar.delegate?.searchBar!(sut.searchBar, textDidChange: theSearchTerm)
                    // then
                    mockViewModel.verifySearchRouteTriggered(searchText: theSearchTerm)
                }
                
                context("when the viewModel signals to show activity") {
                    it("shows an activity indication") {
                        // given
                        mockViewModel.expectShowActivityToReturn(true)
                        // then
                        mockActivityPresenter.verifyShowCalled()
                    }
                }
                
                context("when the viewModel signals to hide activity") {
                    it("hides the activity indication") {
                        // given
                        mockViewModel.expectShowActivityToReturn(false)
                        // then
                        mockActivityPresenter.verifyDismissCalled(times: 2) // +1 for the initial dismiss
                    }
                }
            }
        }
    }
}

extension RoutesViewControllerSpec {
    class TestAssembly: Assembly {
        func assemble(container: Container) {
            container.storyboardInitCompleted(RoutesViewController.self) { resolver, viewController in
                viewController.viewModel = resolver.resolve(MockRoutesViewModel.self)
                viewController.activityPresenter = resolver.resolve(MockActivityPresenter.self)
            }
            
            container.register(MockRoutesViewModel.self) { _ in
                return MockRoutesViewModel()
            }.inObjectScope(.transient)
            
            container.register(MockActivityPresenter.self) { _ in
                return MockActivityPresenter()
            }.inObjectScope(.transient)
        }
        
    }
    
    class MockRoutesViewModel: RoutesViewModelProtocol {
        // MARK: - Input
        var searchRoute = PublishRelay<String>()
        
        // MARK: - Output
        var sections$: Observable<[RoutesSection]> {
            return sectionsSubject.asObservable()
        }
        var showActivity$: Observable<Bool> {
            return showActivitySubject.asObservable()
        }
        
        private var disposeBag = DisposeBag()
        init() {
            searchRoute.asObservable().subscribe(
                onNext: { [weak self] (query) in
                    self?.searchRouteRequestCount += 1
                    self?.searchRouteLastQueryParam = query
            }).disposed(by: disposeBag)
        }
        
        // MARK: - sections
        func expectSectionsToReturn(_ sections: [RoutesSection]) {
            sectionsSubject.onNext(sections)
        }
        private var sectionsSubject = ReplaySubject<[RoutesSection]>.create(bufferSize: 0)

        // MARK: - searchRoute
        var searchRouteRequestCount = 0
        var searchRouteLastQueryParam: String?
        func verifySearchRouteTriggered(
            times callCount: Int = 1,
            searchText: String?,
            file: FileString = #file,
            line: UInt = #line) {
            expect(self.searchRouteRequestCount, file: file, line: line).to(equal(callCount))
            expect(self.searchRouteLastQueryParam, file: file, line: line).to(equal(searchText))
        }
        
        // MARK: - showActivity
        func expectShowActivityToReturn(_ showActivity: Bool) {
            showActivitySubject.onNext(showActivity)
        }
        private var showActivitySubject = BehaviorSubject<Bool>(value: false)
    }
    
    class MockActivityPresenter: ActivityPresenterProtocol {
        private var showCallCount = 0
        private var dismissCallCount = 0
        
        func show() {
            showCallCount += 1
        }
        
        func dismiss() {
            dismissCallCount += 1
        }
        
        func verifyShowCalled(
            times callCount: Int = 1,
            file: FileString = #file,
            line: UInt = #line) {
            expect(self.showCallCount, file: file, line: line).to(equal(callCount))
        }
        
        func verifyDismissCalled(
            times callCount: Int = 1,
            file: FileString = #file,
            line: UInt = #line) {
            expect(self.dismissCallCount, file: file, line: line).to(equal(callCount))
        }
    }
    
    class MockStopDetailsViewModel: StopDetailsViewModelProtocol {
        var loadStops = PublishRelay<String>()
        var selectDirection = PublishRelay<Int>()
        var loadDepartures = PublishRelay<String>()
        var sections$ = Observable<[StopsSection]>.never()
        var showActivity$ = Observable<Bool>.never()
        var headings$ = Observable<[String]>.never()
        var departures$ = Observable<String>.never()
    }
}
