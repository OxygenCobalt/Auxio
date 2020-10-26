package org.oxycblt.auxio.playback

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.exoplayer2.Player
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toDuration
import kotlin.random.Random
import kotlin.random.Random.Default.nextLong

// TODO: User managed queue
// TODO: Add the playback service itself
// TODO: Add loop control [From playback]
// TODO: Implement persistence through Bundles [I want to keep my shuffles, okay?]
// A ViewModel that acts as an intermediary between PlaybackService and the Playback Fragments.
class PlaybackViewModel(private val context: Context) : ViewModel(), Player.EventListener {
    private val mCurrentSong = MutableLiveData<Song>()
    val currentSong: LiveData<Song> get() = mCurrentSong

    private val mCurrentParent = MutableLiveData<BaseModel>()
    val currentParent: LiveData<BaseModel> get() = mCurrentParent

    private val mQueue = MutableLiveData(mutableListOf<Song>())
    val queue: LiveData<MutableList<Song>> get() = mQueue

    private val mCurrentIndex = MutableLiveData(0)
    val currentIndex: LiveData<Int> get() = mCurrentIndex

    private val mCurrentMode = MutableLiveData(PlaybackMode.ALL_SONGS)
    val currentMode: LiveData<PlaybackMode> get() = mCurrentMode

    private val mCurrentDuration = MutableLiveData(0L)

    private val mIsPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> get() = mIsPlaying

    private val mIsShuffling = MutableLiveData(false)
    val isShuffling: LiveData<Boolean> get() = mIsShuffling

    private val mShuffleSeed = MutableLiveData(-1L)
    val shuffleSeed: LiveData<Long> get() = mShuffleSeed

    private val mIsSeeking = MutableLiveData(false)
    val isSeeking: LiveData<Boolean> get() = mIsSeeking

    private var mCanAnimate = false
    val canAnimate: Boolean get() = mCanAnimate

    private lateinit var playbackService: PlaybackService
    private var playbackIntent: Intent

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            playbackService = (binder as PlaybackService.LocalBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(this::class.simpleName, "Service disconnected.")
        }
    }

    init {
        playbackIntent = Intent(context, PlaybackService::class.java).also {
            context.bindService(it, connection, Context.BIND_AUTO_CREATE)
        }
    }

    // Formatted variants of the duration
    val formattedCurrentDuration = Transformations.map(mCurrentDuration) {
        it.toDuration()
    }

    val formattedSeekBarProgress = Transformations.map(mCurrentDuration) {
        if (mCurrentSong.value != null) it.toInt() else 0
    }

    // Formatted queue that shows all the songs after the current playing song.
    val formattedQueue = Transformations.map(mQueue) {
        it.slice((mCurrentIndex.value!! + 1) until it.size)
    }

    // Update the current song while changing the queue mode.
    fun update(song: Song, mode: PlaybackMode) {
        // Auxio doesn't support playing songs while swapping the mode to GENRE, as its impossible
        // to determine what genre a song has.
        if (mode == PlaybackMode.IN_GENRE) {
            Log.e(this::class.simpleName, "Auxio cant play songs with the mode of IN_GENRE.")

            return
        }

        Log.d(this::class.simpleName, "Updating song to ${song.name} and mode to $mode")

        val musicStore = MusicStore.getInstance()

        mCurrentMode.value = mode

        updatePlayback(song)

        mQueue.value = when (mode) {
            PlaybackMode.ALL_SONGS -> musicStore.songs.toMutableList()
            PlaybackMode.IN_ARTIST -> song.album.artist.songs
            PlaybackMode.IN_ALBUM -> song.album.songs
            PlaybackMode.IN_GENRE -> error("what")
        }

        mCurrentParent.value = when (mode) {
            PlaybackMode.ALL_SONGS -> null
            PlaybackMode.IN_ARTIST -> song.album.artist
            PlaybackMode.IN_ALBUM -> song.album
            PlaybackMode.IN_GENRE -> error("what")
        }

        if (mIsShuffling.value!!) {
            genShuffle(true)
        } else {
            resetShuffle()
        }

        mCurrentIndex.value = mQueue.value!!.indexOf(song)
    }

    // Play some parent music model, whether that being albums/artists/genres.
    fun play(album: Album, isShuffled: Boolean) {
        Log.d(this::class.simpleName, "Playing album ${album.name}")

        if (album.songs.isEmpty()) {
            Log.e(this::class.simpleName, "Album is empty, not playing.")

            return
        }

        val songs = orderSongsInAlbum(album)

        updatePlayback(songs[0])

        mQueue.value = songs
        mCurrentIndex.value = 0
        mCurrentParent.value = album
        mIsShuffling.value = isShuffled
        mCurrentMode.value = PlaybackMode.IN_ALBUM

        if (mIsShuffling.value!!) {
            genShuffle(false)
        } else {
            resetShuffle()
        }
    }

    fun play(artist: Artist, isShuffled: Boolean) {
        Log.d(this::class.simpleName, "Playing artist ${artist.name}")

        if (artist.songs.isEmpty()) {
            Log.e(this::class.simpleName, "Artist is empty, not playing.")

            return
        }

        val songs = orderSongsInArtist(artist)

        updatePlayback(songs[0])

        mQueue.value = songs
        mCurrentIndex.value = 0
        mCurrentParent.value = artist
        mIsShuffling.value = isShuffled
        mCurrentMode.value = PlaybackMode.IN_ARTIST

        if (mIsShuffling.value!!) {
            genShuffle(false)
        } else {
            resetShuffle()
        }
    }

    fun play(genre: Genre, isShuffled: Boolean) {
        Log.d(this::class.simpleName, "Playing genre ${genre.name}")

        if (genre.songs.isEmpty()) {
            Log.e(this::class.simpleName, "Genre is empty, not playing.")

            return
        }

        val songs = orderSongsInGenre(genre)

        updatePlayback(songs[0])

        mQueue.value = songs
        mCurrentIndex.value = 0
        mCurrentParent.value = genre
        mIsShuffling.value = isShuffled
        mCurrentMode.value = PlaybackMode.IN_GENRE

        if (mIsShuffling.value!!) {
            genShuffle(false)
        } else {
            resetShuffle()
        }
    }

    // Update the current duration using a SeekBar progress
    fun updateCurrentDurationWithProgress(progress: Int) {
        mCurrentDuration.value = progress.toLong()
    }

    // Invert, not directly set the playing/shuffling status
    // Used by the toggle buttons in playback fragment.
    fun invertPlayingStatus() {
        mCanAnimate = true

        mIsPlaying.value = !mIsPlaying.value!!
    }

    fun invertShuffleStatus() {
        mIsShuffling.value = !mIsShuffling.value!!

        if (mIsShuffling.value!!) {
            genShuffle(true)
        } else {
            resetShuffle()
        }
    }

    // Shuffle all the songs.
    fun shuffleAll() {
        val musicStore = MusicStore.getInstance()

        mIsShuffling.value = true
        mQueue.value = musicStore.songs.toMutableList()
        mCurrentMode.value = PlaybackMode.ALL_SONGS
        mCurrentIndex.value = 0

        genShuffle(false)
        updatePlayback(mQueue.value!![0])
    }

    // Set the seeking status
    fun setSeekingStatus(status: Boolean) {
        mIsSeeking.value = status
    }

    // Skip to next song
    fun skipNext() {
        if (mCurrentIndex.value!! < mQueue.value!!.size) {
            mCurrentIndex.value = mCurrentIndex.value!!.inc()
        }

        updatePlayback(mQueue.value!![mCurrentIndex.value!!])

        forceQueueUpdate()
    }

    // Skip to last song
    fun skipPrev() {
        if (mCurrentIndex.value!! > 0) {
            mCurrentIndex.value = mCurrentIndex.value!!.dec()
        }

        updatePlayback(mQueue.value!![mCurrentIndex.value!!])

        forceQueueUpdate()
    }

    fun resetAnimStatus() {
        mCanAnimate = false
    }

    // Move two queue items. Note that this function does not force-update the queue,
    // as calling updateData with a drag would cause bugs.
    fun moveQueueItems(adapterFrom: Int, adapterTo: Int) {
        // Translate the adapter indices into the correct queue indices
        val delta = mQueue.value!!.size - formattedQueue.value!!.size

        val from = adapterFrom + delta
        val to = adapterTo + delta

        try {
            val currentItem = mQueue.value!![from]

            mQueue.value!!.removeAt(from)
            mQueue.value!!.add(to, currentItem)
        } catch (exception: IndexOutOfBoundsException) {
            Log.e(this::class.simpleName, "Indices were out of bounds, did not move queue item")

            return
        }

        forceQueueUpdate()
    }

    // Remove a queue item. Note that this function does not force-update the queue,
    // as calling updateData with a drag would cause bugs.
    fun removeQueueItem(adapterIndex: Int) {
        // Translate the adapter index into the correct queue index
        val delta = mQueue.value!!.size - formattedQueue.value!!.size
        val properIndex = adapterIndex + delta

        Log.d(this::class.simpleName, "Removing item ${mQueue.value!![properIndex].name}.")

        if (properIndex > mQueue.value!!.size || properIndex < 0) {
            Log.e(this::class.simpleName, "Index is out of bounds, did not remove queue item.")

            return
        }

        mQueue.value!!.removeAt(properIndex)

        forceQueueUpdate()
    }

    // Force the observers of the queue to actually update after making changes.
    private fun forceQueueUpdate() {
        mQueue.value = mQueue.value
    }

    // Generic function for updating the playback with a new song.
    // Use this instead of manually updating the values each time.
    private fun updatePlayback(song: Song) {
        mCurrentSong.value = song
        mCurrentDuration.value = 0

        if (!mIsPlaying.value!!) {
            mIsPlaying.value = true
        }

        playbackService.playSong(song)
    }

    // Generate a new shuffled queue.
    private fun genShuffle(keepSong: Boolean) {
        // Take a random seed and then shuffle the current queue based off of that.
        // This seed will be saved in a bundle if the app closes, so that the shuffle mode
        // can be restored when its started again.
        val newSeed = Random.Default.nextLong()

        Log.d(this::class.simpleName, "Shuffling queue with a seed of $newSeed.")

        mShuffleSeed.value = newSeed

        mQueue.value!!.shuffle(Random(newSeed))
        mCurrentIndex.value = 0

        // If specified, make the current song the first member of the queue.
        if (keepSong) {
            mQueue.value!!.remove(mCurrentSong.value)
            mQueue.value!!.add(0, mCurrentSong.value!!)
        } else {
            // Otherwise, just start from the zeroth position in the queue.
            mCurrentSong.value = mQueue.value!![0]
        }

        // Force the observers to actually update.
        mQueue.value = mQueue.value
    }

    // Stop the queue and attempt to restore to the previous state
    private fun resetShuffle() {
        mShuffleSeed.value = -1

        mQueue.value = when (mCurrentMode.value!!) {
            PlaybackMode.IN_ARTIST -> orderSongsInArtist(mCurrentParent.value as Artist)
            PlaybackMode.IN_ALBUM -> orderSongsInAlbum(mCurrentParent.value as Album)
            PlaybackMode.IN_GENRE -> orderSongsInGenre(mCurrentParent.value as Genre)
            PlaybackMode.ALL_SONGS -> MusicStore.getInstance().songs.toMutableList()
        }

        mCurrentIndex.value = mQueue.value!!.indexOf(mCurrentSong.value)
    }

    // Basic sorting functions when things are played in order
    private fun orderSongsInAlbum(album: Album): MutableList<Song> {
        return album.songs.sortedBy { it.track }.toMutableList()
    }

    private fun orderSongsInArtist(artist: Artist): MutableList<Song> {
        val final = mutableListOf<Song>()

        artist.albums.sortedByDescending { it.year }.forEach { album ->
            final.addAll(album.songs.sortedBy { it.track })
        }

        return final
    }

    private fun orderSongsInGenre(genre: Genre): MutableList<Song> {
        val final = mutableListOf<Song>()

        genre.artists.sortedWith(
            compareBy(String.CASE_INSENSITIVE_ORDER) { it.name }
        ).forEach { artist ->
            artist.albums.sortedByDescending { it.year }.forEach { album ->
                final.addAll(album.songs.sortedBy { it.track })
            }
        }

        return final
    }

    override fun onCleared() {
        super.onCleared()

        context.unbindService(connection)
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("unchecked_cast")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlaybackViewModel::class.java)) {
                return PlaybackViewModel(context) as T
            }

            throw IllegalArgumentException("Unknown ViewModel class.")
        }
    }
}
