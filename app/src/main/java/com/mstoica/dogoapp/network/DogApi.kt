package com.mstoica.dogoapp.network

import com.mstoica.dogoapp.model.dto.*
import com.mstoica.dogoapp.model.structure.SimpleResponseDto
import okhttp3.ResponseBody
import retrofit2.http.*

interface DogApi {

    @GET("breeds")
    suspend fun getBreeds(): List<BreedDto>

    @GET("favourites")
    suspend fun getFavourites(
        @Header("x-api-key") apiKey: String
    ): List<FavouriteDto>

    @GET("images/search")
    suspend fun searchImage(
        @Header("x-api-key")
        apiKey: String,

        @Query("limit")
        limit: Int,

        @Query("size")
        size: String = ImageSize.MEDIUM.queryParamText,
    ): List<ImageSearchDto>

    @DELETE("favourites/{id}")
    suspend fun deleteFavourite(
        @Header("x-api-key")
        apiKey: String,

        @Path("id")
        favouriteId: String
    ): ResponseBody

    @POST("favourites")
    suspend fun makeFavourite(
        @Header("x-api-key")
        apiKey: String,
        @Body
        body: MakeFavouriteRequestDto
    ): SimpleResponseDto
}