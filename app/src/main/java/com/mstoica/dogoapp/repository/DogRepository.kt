package com.mstoica.dogoapp.repository

import com.mstoica.dogoapp.model.domain.Dog
import com.mstoica.dogoapp.repository.dao.DogDao
import javax.inject.Inject

class DogRepository @Inject constructor(private val dao: DogDao)  {

    suspend fun getRandomDog(): Dog {
        val searchResult = dao.getRandomDog()
        val breedInfo = searchResult.breeds.firstOrNull()

        return Dog(
            imageId = searchResult.id,
            imageUrl = searchResult.url,
            breedInfo = breedInfo?.name,
            temperament = breedInfo?.temperament,
        )
    }
}