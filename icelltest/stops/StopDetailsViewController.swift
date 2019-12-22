//
//  StopDetailsViewController.swift
//  icelltest
//
//  Created by Balázs on 2019. 12. 19..
//  Copyright © 2019. trial. All rights reserved.
//

import Foundation
import UIKit
import RxSwift
import RxCocoa
import RxDataSources

struct StopsSection {
    var header: String
    var items: [Stop]
}

class StopDetailsViewController: UIViewController {
    @IBOutlet weak var activityPresenter: UIActivityIndicatorView!
    @IBOutlet weak var departureTimesLabel: UILabel!
    var route: ReplaySubject<Route> = ReplaySubject.create(bufferSize: 1)
    @IBOutlet weak var directionSwitch: UISegmentedControl!
    @IBOutlet weak var tableView: UITableView!
    var viewModel: StopDetailsViewModelProtocol!
    private var disposeBag = DisposeBag()
    private var dataSource: RxTableViewSectionedReloadDataSource<StopsSection>!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        dataSource = RxTableViewSectionedReloadDataSource<StopsSection>(
            configureCell: { _, tableView, indexPath, item in
                guard let cell = tableView.dequeueReusableCell(withIdentifier: "StopCellId", for: indexPath) as? StopCell else {
                    preconditionFailure("Cell should be of type StopCell")
                }
                cell.nameLabel.text = "\(indexPath.row + 1). " + item.name
                return cell
        })
        
        viewModel.sections$
            .bind(to: tableView.rx.items(dataSource: dataSource))
            .disposed(by: disposeBag)
        
        tableView.rx.itemSelected.asObservable()
            .subscribe(
                onNext: { [weak self] (indexPath: IndexPath) in
                    guard let self = self else {
                        return
                    }
                    let section: StopsSection = self.dataSource[indexPath.section]
                    let stop = section.items[indexPath.row]
                    self.viewModel.loadDepartures.accept(stop.id)
            })
            .disposed(by: disposeBag)
        
        bindToViewModel()
        driveUI()
    }
    
    // MARK: - Private
    private func setupUI() {
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 100
        self.directionSwitch.isHidden = true
    }
    
    private func bindToViewModel() {
        directionSwitch.rx.value.asObservable()
            .bind(to: viewModel.selectDirection)
            .disposed(by: disposeBag)
        
        route.asObservable()
            .map({ $0.id })
            .bind(to: viewModel.loadStops)
            .disposed(by: disposeBag)
    }
    
    private func driveUI() {
        viewModel.headings$
            .observeOn(MainScheduler.instance)
            .subscribe(onNext: { [weak self] headings in
                guard let self = self else { return }
                self.directionSwitch.removeAllSegments()
                for (idx, heading) in headings.enumerated() {
                    self.directionSwitch.insertSegment(
                        withTitle: heading,
                        at: idx,
                        animated: false)
                }
                self.directionSwitch.selectedSegmentIndex = 0
                self.directionSwitch.isHidden = false
            }).disposed(by: disposeBag)
        
        route
            .map({ "\($0.shortName) - \($0.type)" })
            .asDriver(onErrorJustReturn: "")
            .drive(rx.title)
            .disposed(by: disposeBag)
        
        viewModel.departures$.asDriver(onErrorJustReturn: "")
            .drive(departureTimesLabel.rx.text)
            .disposed(by: disposeBag)
        
        viewModel.showActivity$.asObservable()
            .observeOn(MainScheduler.instance)
            .subscribe(
                onNext: { [weak self] (show: Bool) in
                    if show {
                        self?.departureTimesLabel.text = nil
                        self?.activityPresenter.show()
                    } else {
                        self?.activityPresenter.dismiss()
                    }
            })
            .disposed(by: disposeBag)
    }
}
