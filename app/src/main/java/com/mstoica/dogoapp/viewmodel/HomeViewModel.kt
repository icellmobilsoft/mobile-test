package com.mstoica.dogoapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mstoica.dogoapp.model.domain.Dog
import com.mstoica.dogoapp.model.dto.ImageSearchDto
import com.mstoica.dogoapp.network.NetworkResource
import com.mstoica.dogoapp.network.SessionManager
import com.mstoica.dogoapp.repository.DogRepository
import com.mstoica.dogoapp.repository.FavouritesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    application: Application,
    private val sessionManager: SessionManager,
    private val dogRepo: DogRepository,
    private val favRepo: FavouritesRepository
) : AndroidViewModel(application) {

    private val _randomDogLiveData: MutableLiveData<NetworkResource<Dog>> =
        MutableLiveData()

    val randomDogLiveData: LiveData<NetworkResource<Dog>>
        get() = _randomDogLiveData

    private val _likeChangedLiveData: MutableLiveData<Boolean?> = MutableLiveData(null)

    val likeChangedLiveData: LiveData<Boolean?>
        get() = _likeChangedLiveData

    private val currentDog: Dog?
        get() = randomDogLiveData.value?.data

    init {
        getNewRandomDog()
    }

    fun getNewRandomDog() = viewModelScope.launch(Dispatchers.IO) {
        _likeChangedLiveData.postValue(null)
        _randomDogLiveData.postValue(NetworkResource.loading())

        val result = try {
            val dog = dogRepo.getRandomDog()
            NetworkResource.success(dog)
        } catch (ex: Exception) {
            Log.e(logTag, ex.message, ex)
            NetworkResource.error(ex.message)
        }
        _randomDogLiveData.postValue(result)
    }

    fun onLikePressed() = viewModelScope.launch {
        check(currentDog != null) {
            "Invalid data!"
        }

        if (currentDog!!.favId != null) {
            deleteFromFavourites(currentDog!!.favId!!)
        } else {
            makeFavourite(currentDog!!.imageId)
        }
    }

    private suspend fun makeFavourite(imageId: String) {
        try {
            val simpleResponse = favRepo.makeFavourite(imageId = imageId, userName = sessionManager.userName)
            currentDog!!.favId = simpleResponse.id
            _likeChangedLiveData.postValue(true)
        } catch(ex: Exception) {
            Log.e(logTag, ex.message, ex)
        }
    }

    private suspend fun deleteFromFavourites(id: String) {
        try {
            favRepo.deleteFavourite(id)
            currentDog!!.favId = null
            _likeChangedLiveData.postValue(false)
        } catch(ex: Exception) {
            Log.e(logTag, ex.message, ex)
        }
    }

    companion object {
        val logTag = HomeViewModel::class.java.simpleName
    }
}