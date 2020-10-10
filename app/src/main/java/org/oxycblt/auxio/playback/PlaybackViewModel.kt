package org.oxycblt.auxio.playback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.music.Song

// TODO: Implement media controls
// TODO: Add the playback service itself
class PlaybackViewModel : ViewModel() {
    private val mCurrentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song> get() = mCurrentSong

    private val mShouldOpenPlayback = MutableLiveData<Boolean>()
    val shouldOpenPlayback: LiveData<Boolean> get() = mShouldOpenPlayback

    fun updateSong(song: Song) {
        mCurrentSong.value = song
    }

    fun openPlayback() {
        mShouldOpenPlayback.value = true
    }

    fun doneWithOpenPlayback() {
        mShouldOpenPlayback.value = false
    }
}
