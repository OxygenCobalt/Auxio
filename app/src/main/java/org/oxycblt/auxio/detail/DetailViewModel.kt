package org.oxycblt.auxio.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.music.models.Artist
import org.oxycblt.auxio.reycler.SortMode

class DetailViewModel : ViewModel() {
    var isAlreadyNavigating = false

    private val mNavToParentArtist = MutableLiveData<Boolean>()
    val navToParentArtist: LiveData<Boolean> get() = mNavToParentArtist

    private val mArtistSortMode = MutableLiveData(SortMode.NUMERIC_DOWN)
    val artistSortMode: LiveData<SortMode> get() = mArtistSortMode

    var currentArtist: Artist? = null
    var currentAlbum: Album? = null

    fun navToParent() {
        mNavToParentArtist.value = true
    }

    fun doneWithNavToParent() {
        mNavToParentArtist.value = false
    }

    fun incrementArtistSortMode() {
        mArtistSortMode.value = when (mArtistSortMode.value) {
            SortMode.NUMERIC_DOWN -> SortMode.NUMERIC_UP
            SortMode.NUMERIC_UP -> SortMode.ALPHA_DOWN
            SortMode.ALPHA_DOWN -> SortMode.ALPHA_UP
            SortMode.ALPHA_UP -> SortMode.NUMERIC_DOWN

            else -> SortMode.NUMERIC_DOWN
        }
    }
}
