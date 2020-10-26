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
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toDuration
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.playback.state.PlaybackStateCallback
import org.oxycblt.auxio.playback.state.PlaybackStateManager

// The UI frontend for PlaybackStateManager.
class PlaybackViewModel(private val context: Context) : ViewModel(), PlaybackStateCallback {
    // Playback
    private val mSong = MutableLiveData<Song>()
    val song: LiveData<Song> get() = mSong

    private val mPosition = MutableLiveData(0L)
    val position: LiveData<Long> get() = mPosition

    // Queue
    private val mQueue = MutableLiveData(mutableListOf<Song>())
    val queue: LiveData<MutableList<Song>> get() = mQueue

    private val mIndex = MutableLiveData(0)
    val index: LiveData<Int> get() = mIndex

    // States
    private val mIsPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> get() = mIsPlaying

    private val mIsShuffling = MutableLiveData(false)
    val isShuffling: LiveData<Boolean> get() = mIsShuffling

    // Other
    private val mIsSeeking = MutableLiveData(false)
    val isSeeking: LiveData<Boolean> get() = mIsSeeking

    val formattedPosition = Transformations.map(mPosition) {
        it.toDuration()
    }

    val positionAsProgress = Transformations.map(mPosition) {
        if (mSong.value != null) it.toInt() else 0
    }

    val nextItemsInQueue = Transformations.map(mQueue) {
        it.slice((mIndex.value!! + 1) until it.size)
    }

    private var mCanAnimate = false
    val canAnimate: Boolean get() = mCanAnimate

    // Service setup
    private val playbackManager = PlaybackStateManager.getInstance()

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

    init {
        playbackManager.addCallback(this)
    }

    // --- PLAYING FUNCTIONS ---

    fun playSong(song: Song, mode: PlaybackMode) {
        playbackManager.playSong(song, mode)
    }

    fun shuffleAll() {
        playbackManager.shuffleAll()
    }

    fun playAlbum(album: Album, shuffled: Boolean) {
        if (album.songs.isEmpty()) {
            Log.e(this::class.simpleName, "Album is empty, Not playing.")

            return
        }

        playbackManager.playParentModel(album, shuffled)
    }

    fun playArtist(artist: Artist, shuffled: Boolean) {
        if (artist.songs.isEmpty()) {
            Log.e(this::class.simpleName, "Artist is empty, Not playing.")

            return
        }

        playbackManager.playParentModel(artist, shuffled)
    }

    fun playGenre(genre: Genre, shuffled: Boolean) {
        if (genre.songs.isEmpty()) {
            Log.e(this::class.simpleName, "Genre is empty, Not playing.")

            return
        }

        playbackManager.playParentModel(genre, shuffled)
    }

    // --- POSITION FUNCTIONS ---

    fun updatePositionWithProgress(progress: Int) {
        playbackManager.setPosition(progress.toLong())
    }

    // --- QUEUE FUNCTIONS ---

    fun skipNext() {
        playbackManager.skipNext()
    }

    fun skipPrev() {
        playbackManager.skipPrev()
    }

    fun removeQueueItem(adapterIndex: Int) {
        // Translate the adapter indices into the correct queue indices
        val delta = mQueue.value!!.size - nextItemsInQueue.value!!.size

        val index = adapterIndex + delta

        playbackManager.removeQueueItem(index)
    }

    fun moveQueueItems(adapterFrom: Int, adapterTo: Int) {
        // Translate the adapter indices into the correct queue indices
        val delta = mQueue.value!!.size - nextItemsInQueue.value!!.size

        val from = adapterFrom + delta
        val to = adapterTo + delta

        playbackManager.moveQueueItems(from, to)
    }

    // --- STATUS FUNCTIONS ---

    fun invertPlayingStatus() {
        mCanAnimate = true

        playbackManager.setPlayingStatus(!playbackManager.isPlaying)
    }

    fun invertShuffleStatus() {
        playbackManager.setShuffleStatus(!playbackManager.isShuffling)
    }

    // --- OTHER FUNCTIONS ---

    fun resetAnimStatus() {
        mCanAnimate = false
    }

    fun setSeekingStatus(value: Boolean) {
        mIsSeeking.value = value
    }

    // --- OVERRIDES ---

    override fun onCleared() {
        playbackManager.removeCallback(this)
    }

    override fun onSongUpdate(song: Song?) {
        song?.let {
            mSong.value = it
        }
    }

    override fun onPositionUpdate(position: Long) {
        mPosition.value = position
    }

    override fun onQueueUpdate(queue: MutableList<Song>) {
        mQueue.value = queue
    }

    override fun onIndexUpdate(index: Int) {
        mIndex.value = index
    }

    override fun onPlayingUpdate(isPlaying: Boolean) {
        mIsPlaying.value = isPlaying
    }

    override fun onShuffleUpdate(isShuffling: Boolean) {
        mIsShuffling.value = isShuffling
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
