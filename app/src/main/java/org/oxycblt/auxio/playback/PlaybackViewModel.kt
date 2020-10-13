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

    private val mCurrentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> get() = mCurrentIndex

    private val mQueue = MutableLiveData(mutableListOf<Song>())
    val queue: LiveData<MutableList<Song>> get() = mQueue

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

    // Update the current song while changing the queue to All Songs.
    fun update(song: Song, allSongs: List<Song>) {
        updatePlayback(song)

        mQueue.value = allSongs.toMutableList()
        mCurrentIndex.value = allSongs.indexOf(song)
    }

    private fun updatePlayback(song: Song) {
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

    fun skipNext() {
        if (mCurrentIndex.value!! < mQueue.value!!.size) {
            mCurrentIndex.value = mCurrentIndex.value!!.inc()
        }

        updatePlayback(mQueue.value!![mCurrentIndex.value!!])
    }

    fun skipPrev() {
        if (mCurrentIndex.value!! > 0) {
            mCurrentIndex.value = mCurrentIndex.value!!.dec()
        }

        updatePlayback(mQueue.value!![mCurrentIndex.value!!])
    }
}
