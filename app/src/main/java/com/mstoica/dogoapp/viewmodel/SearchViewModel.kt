package com.mstoica.dogoapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mstoica.dogoapp.model.domain.Dog
import com.mstoica.dogoapp.model.dto.ImageOrder
import com.mstoica.dogoapp.model.dto.ImageSize
import com.mstoica.dogoapp.network.NetworkOptions
import com.mstoica.dogoapp.network.NetworkResource
import com.mstoica.dogoapp.repository.DogRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class SearchViewModel @Inject constructor(
    application: Application,
    private val repository: DogRepository
): AndroidViewModel(application) {

    private val _searchResultLiveData: MutableLiveData<NetworkResource<List<Dog>>> =
        MutableLiveData()

    val searchResultLiveData: LiveData<NetworkResource<List<Dog>>>
        get() = _searchResultLiveData

    private var pageCount: Int = 0

    init {
        loadNewImages()
    }

    fun loadNewImages() = viewModelScope.launch (Dispatchers.IO) {
        val currentImages = _searchResultLiveData.value?.data ?: emptyList()

        _searchResultLiveData.postValue(NetworkResource.loading(currentImages))

        try {
            val searchResult = repository.getRandomDogs(
                limit = SEARCH_PER_PAGE,
                page = pageCount,
                size = ImageSize.MEDIUM,
                order = ImageOrder.DESC
            )
            _searchResultLiveData.postValue(
                NetworkResource.success(
                    data = currentImages + searchResult
                )
            )
            if (searchResult.isNotEmpty()) {
                ++pageCount
            }
        } catch (ex: Exception) {
            Log.e(logTag, ex.message, ex)
            _searchResultLiveData.postValue(NetworkResource.error(ex.message, currentImages))
        }
    }

    fun makeSearch(queryText: String) {
        //Todo: implement search functionality
    }

    companion object {
        const val SEARCH_PER_PAGE = 30
        val logTag = SearchViewModel::class.java.simpleName
    }
}