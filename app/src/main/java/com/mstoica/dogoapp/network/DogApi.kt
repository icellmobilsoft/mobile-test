package com.mstoica.dogoapp.network

import com.mstoica.dogoapp.model.dto.*
import com.mstoica.dogoapp.model.structure.SimpleResponseDto
import okhttp3.ResponseBody
import retrofit2.http.*

interface DogApi {

    @GET("images/search")
    suspend fun search(
        @Query("limit")
        limit: Int,

        @Query("page")
        page: Int,

        @Query("size")
        size: String = ImageSize.MEDIUM.queryParamText,

        @Query("order")
        order: String = ImageOrder.RANDOM.queryParamText,
    ): List<ImageSearchDto>

    @GET("favourites")
    suspend fun getFavourites(
        @Query("sub_id")
        userName: String,

        @Query("limit")
        limit: Int,

        @Query("page")
        page: Int,
    ): List<FavouriteDto>

    @GET("images/{image_id}")
    suspend fun getImage(
        @Path("image_id")
        imageId: String,
    ): ImageSearchDto

    @DELETE("favourites/{id}")
    suspend fun deleteFavourite(
        @Path("id")
        favouriteId: String
    ): ResponseBody

    @POST("favourites")
    suspend fun makeFavourite(
        @Body
        body: MakeFavouriteRequestDto
    ): SimpleResponseDto
}