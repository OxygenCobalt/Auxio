package org.oxycblt.auxio.songs

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.models.Song

class SongsViewModel : ViewModel() {

    private val mSongs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> get() = mSongs

    init {
        val repo = MusicRepository.getInstance()

        mSongs.value = repo.songs

        Log.d(this::class.simpleName, "ViewModel created.")
    }
}
