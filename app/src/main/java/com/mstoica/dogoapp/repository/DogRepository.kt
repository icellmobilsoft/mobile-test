package com.mstoica.dogoapp.repository

import com.mstoica.dogoapp.model.domain.Dog
import com.mstoica.dogoapp.model.dto.ImageOrder
import com.mstoica.dogoapp.model.dto.ImageSearchDto
import com.mstoica.dogoapp.model.dto.ImageSize
import com.mstoica.dogoapp.repository.dao.DogDao
import javax.inject.Inject

class DogRepository @Inject constructor(private val dao: DogDao)  {

    suspend fun searchImages(limit: Int, page: Int, size: ImageSize, order: ImageOrder): List<ImageSearchDto> =
        dao.searchImages(limit, page, size, order)

    suspend fun searchImage(limit: Int, page: Int, size: ImageSize, order: ImageOrder): ImageSearchDto =
        dao.searchImage(limit, page, size, order)

    suspend fun getRandomDog(): Dog = dao.getRandomDog()

    suspend fun getRandomDogs(limit: Int, page: Int, size: ImageSize, order: ImageOrder): List<Dog> =
        dao.getRandomDogs(limit, page, size, order)
}