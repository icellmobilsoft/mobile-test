//
//  RoutesViewController.swift
//  icelltest
//
//  Created by Balázs on 2019. 12. 17..
//  Copyright © 2019. trial. All rights reserved.
//

import Foundation
import UIKit
import RxSwift
import RxCocoa
import RxDataSources

class RoutesViewController: UIViewController {
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var searchBar: UISearchBar!
    
    var viewModel: RoutesViewModelProtocol!
    var activityPresenter: ActivityPresenterProtocol!
    private var disposeBag = DisposeBag()
    private var dataSource: RxTableViewSectionedReloadDataSource<RoutesSection>!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
        dataSource = RxTableViewSectionedReloadDataSource<RoutesSection>(
            configureCell: { _, tableView, indexPath, item in
                guard let cell = tableView.dequeueReusableCell(withIdentifier: "RouteCellId", for: indexPath) as? RouteCell else {
                    preconditionFailure("Cell should be of type RouteCell")
                }
                cell.nameLabel.text = "\(item.shortName) - \(item.type)"
                cell.descriptionLabel.text = item.description
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
                    let section: RoutesSection = self.dataSource[indexPath.section]
                    let route = section.items[indexPath.row]
                    self.performSegue(withIdentifier: "showStopDetails",
                                      sender: route)
            })
            .disposed(by: disposeBag)
        
        bindToViewModel()
        driveUI()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        switch segue.identifier {
        case "showStopDetails":
            if let route = sender as? Route {
                if let stopDetailsVC = segue.destination as? StopDetailsViewController {
                    stopDetailsVC.route.onNext(route)
                }
            }
        default:
            break
        }
    }
    
    // MARK: - Private
    private func setupUI() {
        searchBar.becomeFirstResponder()
        searchBar.barTintColor = UIColor.white
        if let searchBarActivityPresenter = activityPresenter as? SearchBarActivityPresenter {
            searchBarActivityPresenter.addSearchBar(searchBar)
        }
        
        tableView.rowHeight = UITableView.automaticDimension
        tableView.estimatedRowHeight = 100
        tableView.tableFooterView = UIView()
    }
    
    private func bindToViewModel() {
        searchBar.rx.text.orEmpty.asObservable()
            .skip(1)
            .bind(to: viewModel.searchRoute)
            .disposed(by: disposeBag)
    }
    
    private func driveUI() {
        viewModel.showActivity$.asObservable()
            .observeOn(MainScheduler.instance)
            .subscribe(
                onNext: { [weak self] (show: Bool) in
                    if show {
                        self?.activityPresenter.show()
                    } else {
                        self?.activityPresenter.dismiss()
                    }
            })
            .disposed(by: disposeBag)
    }
}
