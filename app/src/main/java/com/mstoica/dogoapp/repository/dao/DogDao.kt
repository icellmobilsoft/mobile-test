package com.mstoica.dogoapp.repository.dao

import com.mstoica.dogoapp.model.dto.ImageSearchDto
import com.mstoica.dogoapp.model.dto.ImageSize
import com.mstoica.dogoapp.network.DogApi
import com.mstoica.dogoapp.network.NetworkOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DogDao @Inject constructor(private val dogApi: DogApi) {
    suspend fun getRandomDog(): ImageSearchDto = withContext(Dispatchers.IO) {
        dogApi.searchImage(
            apiKey = NetworkOptions.apiKey,
            limit = 1,
            size = ImageSize.SMALL.queryParamText
        ).first()
    }
}