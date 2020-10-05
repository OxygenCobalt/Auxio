package org.oxycblt.auxio.playback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.music.Song

class PlaybackViewModel : ViewModel() {
    private val mCurrentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song> get() = mCurrentSong

    fun updateSong(song: Song) {
        mCurrentSong.value = song
    }
}
