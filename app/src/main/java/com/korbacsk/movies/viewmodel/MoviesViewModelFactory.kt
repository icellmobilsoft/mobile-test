package com.korbacsk.movies.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.korbacsk.movies.httpapi.MovieApiInterface

class MoviesViewModelFactory (private val finalMovieApiInterface: MovieApiInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoviesViewModel::class.java)) {
            return MoviesViewModel(finalMovieApiInterface) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}