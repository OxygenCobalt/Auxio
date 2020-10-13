package org.oxycblt.auxio.playback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toDuration

// TODO: Implement media controls
// TODO: Add the playback service itself
// TODO: Possibly add some swipe-to-next-track function, could require a ViewPager.
// A ViewModel that acts as an intermediary between PlaybackService and the Playback Fragments.
class PlaybackViewModel : ViewModel() {
    private val mCurrentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song> get() = mCurrentSong

    private val mCurrentDuration = MutableLiveData(0L)
    val currentDuration: LiveData<Long> get() = mCurrentDuration

    private val mIsPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> get() = mIsPlaying

    private val mIsSeeking = MutableLiveData(false)
    val isSeeking: LiveData<Boolean> get() = mIsSeeking

    // Formatted variants of the duration
    val formattedCurrentDuration = Transformations.map(currentDuration) {
        it.toDuration()
    }

    val formattedSeekBarProgress = Transformations.map(currentDuration) {
        if (mCurrentSong.value != null) it.toInt() else 0
    }

    fun updateSong(song: Song) {
        mCurrentSong.value = song
        mCurrentDuration.value = 0

        if (!mIsPlaying.value!!) {
            mIsPlaying.value = true
        }
    }

    // Invert, not directly set the playing status
    fun invertPlayingStatus() {
        mIsPlaying.value = !mIsPlaying.value!!
    }

    // Set the seeking status
    fun setSeekingStatus(status: Boolean) {
        mIsSeeking.value = status
    }

    // Update the current duration using a SeekBar progress
    fun updateCurrentDurationWithProgress(progress: Int) {
        mCurrentDuration.value = progress.toLong()
    }
}
