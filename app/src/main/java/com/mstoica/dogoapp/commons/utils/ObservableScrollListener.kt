package com.mstoica.dogoapp.commons.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ObservableScrollListener(
    private val layoutManager: GridLayoutManager
): RecyclerView.OnScrollListener() {

    private val _reachedBottomLiveData = MutableLiveData(false)

    val reachedBottomLiveData: LiveData<Boolean>
        get() = _reachedBottomLiveData

    var pastVisibleItems = 0
    var visibleItemCount:Int = 0
    var totalItemCount:Int = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        visibleItemCount = layoutManager.childCount
        totalItemCount = layoutManager.itemCount
        pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
        if (visibleItemCount + pastVisibleItems >= totalItemCount) {
            if (_reachedBottomLiveData.value != true) {
                _reachedBottomLiveData.value = true
            }
        } else {
            if (_reachedBottomLiveData.value != false) {
                _reachedBottomLiveData.value = false
            }
        }
    }

}