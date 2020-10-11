package org.oxycblt.auxio.playback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.music.Song

// TODO: Implement media controls
// TODO: Add the playback service itself
// TODO: Possibly add some swipe-to-next-track function, could require a ViewPager.
// A ViewModel that acts as an intermediary between PlaybackService and the Playback Fragments.
class PlaybackViewModel : ViewModel() {
    private val mCurrentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song> get() = mCurrentSong

    private val mShouldOpenPlayback = MutableLiveData<Boolean>()
    val shouldOpenPlayback: LiveData<Boolean> get() = mShouldOpenPlayback

    private val mIsPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> get() = mIsPlaying

    fun updateSong(song: Song) {
        mCurrentSong.value = song
        mIsPlaying.value = true
    }

    fun openPlayback() {
        mShouldOpenPlayback.value = true
    }

    fun doneWithOpenPlayback() {
        mShouldOpenPlayback.value = false
    }

    // Invert, not directly set the p
    fun invertPlayingStatus() {
        mIsPlaying.value = !mIsPlaying.value!!
    }
}
