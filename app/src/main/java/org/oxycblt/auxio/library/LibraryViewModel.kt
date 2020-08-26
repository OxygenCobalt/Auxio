package org.oxycblt.auxio.library

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.music.models.Artist

class LibraryViewModel : ViewModel() {

    private val mArtists = MutableLiveData<List<Artist>>()
    private var mAlbums = MutableLiveData<List<Album>>()

    val artists: LiveData<List<Artist>> get() = mArtists
    val albums: LiveData<List<Album>> get() = mAlbums

    init {
        val repo = MusicRepository.getInstance()

        mArtists.value = repo.artists
        mAlbums.value = repo.albums

        Log.d(this::class.simpleName, "ViewModel created.")
    }
}
