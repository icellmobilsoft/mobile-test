package com.mstoica.dogoapp.network

import com.mstoica.dogoapp.model.BreedDto
import com.mstoica.dogoapp.model.FavouriteDto
import retrofit2.http.GET

interface DogApi {

    @GET("breeds")
    suspend fun getBreeds(): List<BreedDto>

    @GET("favourites")
    suspend fun getFavourites(): List<FavouriteDto>
}