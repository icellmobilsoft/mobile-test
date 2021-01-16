package com.mstoica.dogoapp.repository

import com.mstoica.dogoapp.repository.dao.FavouritesDao
import javax.inject.Inject

class FavouritesRepository @Inject constructor(private val dao: FavouritesDao) {
    suspend fun makeFavourite(imageId: String, userName: String) = dao.makeFavourite(
        imageId = imageId,
        userName = userName
    )

    suspend fun deleteFavourite(id: String) = dao.deleteFavourite(id)
}