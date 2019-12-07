package com.korbacsk.movies.config

import com.korbacsk.movies.httpapi.HttpApiClient
import com.korbacsk.movies.httpapi.MovieApiInterface

object ServiceLocator {
    fun getMovieApi(): MovieApiInterface {
        val httpApiInterface:MovieApiInterface = HttpApiClient.getMovieClient().create(MovieApiInterface::class.java);

        return httpApiInterface;
    }

}