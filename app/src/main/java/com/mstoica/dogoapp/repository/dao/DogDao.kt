package com.mstoica.dogoapp.repository.dao

import com.mstoica.dogoapp.model.DomainModelConverter
import com.mstoica.dogoapp.model.domain.Dog
import com.mstoica.dogoapp.model.dto.ImageOrder
import com.mstoica.dogoapp.model.dto.ImageSearchDto
import com.mstoica.dogoapp.model.dto.ImageSize
import com.mstoica.dogoapp.network.DogApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DogDao @Inject constructor(private val dogApi: DogApi) {

    suspend fun searchImages(
        limit: Int,
        page: Int,
        size: ImageSize,
        order: ImageOrder
    ): List<ImageSearchDto> = withContext(Dispatchers.IO) {
        dogApi.search(
            limit = limit,
            page = page,
            size = size.queryParamText,
            order = order.queryParamText
        )
    }

    suspend fun searchImage(
        limit: Int,
        page: Int,
        size: ImageSize,
        order: ImageOrder
    ): ImageSearchDto = searchImages(limit, page, size, order).first()

    suspend fun getRandomDog(): Dog = withContext(Dispatchers.IO) {
        DomainModelConverter.convertToDog(
            searchImage(
                limit = 1,
                page = 0,
                size = ImageSize.SMALL,
                order = ImageOrder.RANDOM
            )
        )
    }

    suspend fun getRandomDogs(
        limit: Int,
        page: Int,
        size: ImageSize,
        order: ImageOrder
    ): List<Dog> = withContext(Dispatchers.IO) {
        searchImages(limit, page, size, order).map {
            DomainModelConverter.convertToDog(it)
        }
    }
}