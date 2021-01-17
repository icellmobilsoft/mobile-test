package com.mstoica.dogoapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mstoica.dogoapp.model.domain.Dog
import com.mstoica.dogoapp.model.structure.SimpleResponseDto
import com.mstoica.dogoapp.network.NetworkOptions
import com.mstoica.dogoapp.network.NetworkResource
import com.mstoica.dogoapp.network.SessionManager
import com.mstoica.dogoapp.repository.FavouritesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class FavouritesViewModel @Inject constructor(
    application: Application,
    private val sessionManager: SessionManager,
    private val repository: FavouritesRepository
): AndroidViewModel(application) {

    enum class LikeState { LIKED, UNLIKED, ERROR; }

    private val _favouritesLiveData: MutableLiveData<NetworkResource<List<Dog>>> =
        MutableLiveData()

    val favouritesLiveData: LiveData<NetworkResource<List<Dog>>>
        get() = _favouritesLiveData

    private val _likeModificationLiveData: MutableLiveData<Pair<Dog, LikeState>?> = MutableLiveData()

    val likeModificationLiveData: LiveData<Pair<Dog, LikeState>?>
        get() = _likeModificationLiveData

    init {
        loadFavourites()
    }

    //Todo: implement paging
    fun loadFavourites(page: Int = 0) = viewModelScope.launch (Dispatchers.IO) {
        val currentFavourites = _favouritesLiveData.value?.data ?: emptyList()
        _favouritesLiveData.postValue(NetworkResource.loading(currentFavourites))

        try {
            val queryResult = repository.getFavouriteDogs(
                userName = sessionManager.userName,
                limit = NetworkOptions.PAGE_LIMIT,
                page = page
            )
            _favouritesLiveData.postValue(
                NetworkResource.success(queryResult)
            )
        } catch (ex: Exception) {
            Log.e(logTag, ex.message, ex)
            _favouritesLiveData.postValue(NetworkResource.error(ex.message, currentFavourites))
        }
    }

    fun modifyLike(dogItem: Dog, like: Boolean) = viewModelScope.launch {
        var operationSuccess = false
        if (like) {
            val newFavId = makeFavourite(dogItem.imageId)
            if (newFavId != null) {
                dogItem.favId = newFavId
                operationSuccess = true
                _likeModificationLiveData.postValue(dogItem to LikeState.LIKED)
            } else {
                _likeModificationLiveData.postValue(dogItem to LikeState.ERROR)
            }
        } else {
            val success = deleteFromFavourites(dogItem.favId!!)
            if (success) {
                dogItem.favId = null
                operationSuccess = true
                _likeModificationLiveData.postValue(dogItem to LikeState.UNLIKED)
            } else {
                _likeModificationLiveData.postValue(dogItem to LikeState.ERROR)
            }
        }

        if (operationSuccess) {
            loadFavourites()
        }
    }

    private suspend fun makeFavourite(imageId: String): String? {
        val simpleResponse: SimpleResponseDto? = try {
            repository.makeFavourite(
                imageId = imageId,
                userName = sessionManager.userName
            )
        } catch(ex: Exception) {
            Log.e(HomeViewModel.logTag, ex.message, ex)
            null
        }
        return simpleResponse?.id
    }

    private suspend fun deleteFromFavourites(id: String): Boolean {
        return try {
            repository.deleteFavourite(id)
            true
        } catch(ex: Exception) {
            Log.e(HomeViewModel.logTag, ex.message, ex)
            false
        }
    }

    companion object {
        val logTag = FavouritesViewModel::class.java.simpleName
    }
}