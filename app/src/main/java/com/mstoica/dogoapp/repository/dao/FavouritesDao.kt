package com.mstoica.dogoapp.repository.dao

import com.mstoica.dogoapp.model.DomainModelConverter
import com.mstoica.dogoapp.model.domain.Dog
import com.mstoica.dogoapp.model.dto.MakeFavouriteRequestDto
import com.mstoica.dogoapp.network.DogApi
import com.mstoica.dogoapp.network.NetworkOptions
import kotlinx.coroutines.*
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject

class FavouritesDao @Inject constructor(private val dogApi: DogApi) {

    suspend fun getFavouriteDogs(userName: String, limit: Int, page: Int): List<Dog> = withContext(Dispatchers.IO) {
        val favourites = dogApi.getFavourites(
            userName = userName,
            limit = limit,
            page = page
        )

        val result = ConcurrentLinkedQueue<Dog>()
        val deferredList = mutableListOf<Deferred<Any>>()

        favourites.forEachIndexed { index, favourite ->
            val deferred = async {
                val searchResult = dogApi.getImage(favourite.imageId)
                result.add(DomainModelConverter.convertToDog(searchResult).also {
                    it.favId = favourite.id
                    it.tag = index.toString()
                })
            }
            deferredList.add(deferred)
        }

        deferredList.map { it.await() }
        result.toList().sortedBy { it.tag!!.toInt() }
    }

    suspend fun getFavourites(userName: String, limit: Int, page: Int) = withContext(Dispatchers.IO) {
        dogApi.getFavourites(
            userName = userName,
            limit = limit,
            page = page
        )
    }

    suspend fun makeFavourite(imageId: String, userName: String) = withContext(Dispatchers.IO) {
        dogApi.makeFavourite(
            body = MakeFavouriteRequestDto (
                imageId = imageId,
                userName = userName
            )
        )
    }

    suspend fun deleteFavourite(id: String) = withContext(Dispatchers.IO) {
        dogApi.deleteFavourite(id)
    }
}