package org.oxycblt.auxio.playback

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toDuration

// TODO: Implement media controls
// TODO: Implement persistence
// TODO: Add the playback service itself
// TODO: Possibly add some swipe-to-next-track function, could require a ViewPager.
// A ViewModel that acts as an intermediary between PlaybackService and the Playback Fragments.
class PlaybackViewModel : ViewModel() {
    private val mCurrentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song> get() = mCurrentSong

    private val mQueue = MutableLiveData(mutableListOf<Song>())
    val queue: LiveData<MutableList<Song>> get() = mQueue

    private val mCurrentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> get() = mCurrentIndex

    private val mCurrentMode = MutableLiveData(PlaybackMode.ALL_SONGS)

    private val mCurrentDuration = MutableLiveData(0L)

    private val mIsPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> get() = mIsPlaying

    private val mIsSeeking = MutableLiveData(false)
    val isSeeking: LiveData<Boolean> get() = mIsSeeking

    // Formatted variants of the duration
    val formattedCurrentDuration = Transformations.map(mCurrentDuration) {
        it.toDuration()
    }

    val formattedSeekBarProgress = Transformations.map(mCurrentDuration) {
        if (mCurrentSong.value != null) it.toInt() else 0
    }

    // Update the current song while changing the queue mode.
    fun update(song: Song, mode: PlaybackMode) {
        Log.d(this::class.simpleName, "Updating song to ${song.name} and mode to $mode")

        val musicStore = MusicStore.getInstance()

        updatePlayback(song)

        mQueue.value = when (mode) {
            PlaybackMode.ALL_SONGS -> musicStore.songs.toMutableList()
            PlaybackMode.IN_ARTIST -> song.album.artist.songs
            PlaybackMode.IN_ALBUM -> song.album.songs

            // Warning: Calling update() with a mode of IN_GENRE Will cause Auxio to play
            // from the artist's most prominent genre instead of the song's genre.
            // FIXME: This could be fixed by moving genre loading to songs
            PlaybackMode.IN_GENRE -> {
                Log.d(
                    this::class.simpleName,
                    "update() was called with IN_GENRES, using " +
                        "most prominent genre instead of the song's genre."
                )

                song.album.artist.genres[0].songs
            }
        }

        mCurrentMode.value = mode
        mCurrentIndex.value = mQueue.value!!.indexOf(song)
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
