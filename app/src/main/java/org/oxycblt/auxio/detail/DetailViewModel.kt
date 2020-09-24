package org.oxycblt.auxio.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.music.models.Artist

class DetailViewModel : ViewModel() {
    var isAlreadyNavigating = false

    private val mNavToParentArtist = MutableLiveData<Boolean>()
    val navToParentArtist: LiveData<Boolean> get() = mNavToParentArtist

    var currentArtist: Artist? = null
    var currentAlbum: Album? = null

    fun navToParent() {
        mNavToParentArtist.value = true
    }

    fun doneWithNavToParent() {
        mNavToParentArtist.value = false
    }
}
