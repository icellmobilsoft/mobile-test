package com.korbacsk.movies.httpapi

import com.google.gson.annotations.SerializedName
import com.korbacsk.movies.config.Config
import com.korbacsk.movies.model.MovieModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class MovieApiResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val movies: List<MovieModel>,
    @SerializedName("total_pages") val pages: Int
    )