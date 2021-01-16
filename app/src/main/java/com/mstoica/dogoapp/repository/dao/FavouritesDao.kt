package com.mstoica.dogoapp.repository.dao

import com.mstoica.dogoapp.model.dto.MakeFavouriteRequestDto
import com.mstoica.dogoapp.network.DogApi
import com.mstoica.dogoapp.network.NetworkOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavouritesDao @Inject constructor(private val dogApi: DogApi) {
    suspend fun makeFavourite(imageId: String, userName: String) = withContext(Dispatchers.IO) {
        dogApi.makeFavourite(
            apiKey = NetworkOptions.apiKey,
            body = MakeFavouriteRequestDto (
                imageId = imageId,
                userName = userName
            )
        )
    }

    suspend fun deleteFavourite(id: String) = withContext(Dispatchers.IO) {
        dogApi.deleteFavourite(
            apiKey = NetworkOptions.apiKey,
            favouriteId = id
        )
    }
}