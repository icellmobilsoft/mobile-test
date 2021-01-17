package com.mstoica.dogoapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mstoica.dogoapp.model.domain.Dog
import com.mstoica.dogoapp.model.dto.ImageSearchDto
import com.mstoica.dogoapp.network.NetworkOptions
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

    val currentDog: Dog?
        get() = randomDogLiveData.value?.data

    init {
        getNewRandomDog()
    }

    fun getNewRandomDog() = viewModelScope.launch(Dispatchers.IO) {
        _randomDogLiveData.postValue(NetworkResource.loading())

        val result = try {
            val dog = dogRepo.getRandomDog()
            dog.favId = getFavourites().find { dog.imageId == it.imageId }?.id
            NetworkResource.success(dog)
        } catch (ex: Exception) {
            Log.e(logTag, ex.message, ex)
            NetworkResource.error(ex.message)
        }
        _randomDogLiveData.postValue(result)
    }

    fun update() = viewModelScope.launch(Dispatchers.IO) {
        if (currentDog != null) {
            currentDog!!.favId = getFavourites().find { currentDog!!.imageId == it.imageId }?.id
            _randomDogLiveData.postValue(_randomDogLiveData.value)
        }
    }

    private suspend fun getFavourites() = favRepo.getFavourites(
        userName = sessionManager.userName,
        limit = NetworkOptions.PAGE_LIMIT,
        page = 0
    )

    companion object {
        val logTag = HomeViewModel::class.java.simpleName
    }
}