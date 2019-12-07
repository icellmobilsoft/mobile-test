package com.korbacsk.movies.httpapi

import com.korbacsk.movies.config.Config
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApiInterface {
    @GET(Config.MOVIE_API_MOVIES)
    fun getMovies(
        @Query("api_key") apiKey: String = Config.MOVIE_API_KEY,
        @Query("page") page: Int
    ):  Call<MovieApiResponse>


}