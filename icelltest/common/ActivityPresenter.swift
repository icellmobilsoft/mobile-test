//
//  ActivityPresenter.swift
//  icelltest
//
//  Created by Balázs on 2019. 12. 18..
//  Copyright © 2019. trial. All rights reserved.
//

import Foundation
import UIKit

protocol ActivityPresenterProtocol {
    func show()
    func dismiss()
}

class SearchBarActivityPresenter: ActivityPresenterProtocol {
    private var activityIndicator: UIActivityIndicatorView
    private var searchBar: UISearchBar?
    private var searchIcon: UIImage?
    
    init() {
        activityIndicator = UIActivityIndicatorView(style: .medium)
        activityIndicator.hidesWhenStopped = true
        activityIndicator.stopAnimating()
        activityIndicator.backgroundColor = UIColor.clear
    }
    
    func addSearchBar(_ searchBar: UISearchBar) {
        self.searchBar = searchBar
        activityIndicator.translatesAutoresizingMaskIntoConstraints = false
        searchBar.addSubview(activityIndicator)
        NSLayoutConstraint.activate([
            activityIndicator.centerYAnchor.constraint(equalTo: searchBar.centerYAnchor),
            activityIndicator.trailingAnchor.constraint(equalTo: searchBar.trailingAnchor, constant: -38)
        ])
    }
    
    var isLoading: Bool {
        get {
            return activityIndicator.isAnimating
        } set {
            if newValue {
                activityIndicator.startAnimating()
            } else {
                activityIndicator.stopAnimating()
            }
        }
    }
    
    func show() {
        self.isLoading = true
    }
    
    func dismiss() {
        self.isLoading = false
    }
}

extension UIActivityIndicatorView: ActivityPresenterProtocol {
    func show() {
        startAnimating()
    }
    
    func dismiss() {
        stopAnimating()
    }
}
