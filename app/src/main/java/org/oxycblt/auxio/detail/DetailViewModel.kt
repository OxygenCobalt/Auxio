package org.oxycblt.auxio.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DetailViewModel : ViewModel() {
    var isAlreadyNavigating = false

    private val mNavToParentArtist = MutableLiveData<Boolean>()
    val navToParentArtist: LiveData<Boolean> get() = mNavToParentArtist

    fun navToParent() {
        mNavToParentArtist.value = true
    }

    fun doneWithNavToParent() {
        mNavToParentArtist.value = false
    }
}
