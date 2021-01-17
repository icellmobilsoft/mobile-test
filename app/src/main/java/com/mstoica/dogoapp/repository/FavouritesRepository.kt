package com.mstoica.dogoapp.repository

import com.mstoica.dogoapp.repository.dao.FavouritesDao
import javax.inject.Inject

class FavouritesRepository @Inject constructor(private val dao: FavouritesDao) {

    suspend fun getFavouriteDogs(userName: String, limit: Int, page: Int) = dao.getFavouriteDogs(
        userName = userName,
        limit = limit,
        page = page
    )

    suspend fun getFavourites(userName: String, limit: Int, page: Int) = dao.getFavourites(
        userName = userName,
        limit = limit,
        page = page
    )

    suspend fun makeFavourite(imageId: String, userName: String) = dao.makeFavourite(
        imageId = imageId,
        userName = userName
    )

    suspend fun deleteFavourite(id: String) = dao.deleteFavourite(id)
}