package org.oxycblt.auxio.playback.state

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.database.PlaybackState
import org.oxycblt.auxio.database.PlaybackStateDatabase
import org.oxycblt.auxio.database.QueueItem
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import kotlin.random.Random

/**
 * Master class for the playback state. This should ***not*** be used outside of the playback module.
 * - If you want to use the playback state in the UI, use [org.oxycblt.auxio.playback.PlaybackViewModel].
 * - If you want to use the playback state with the ExoPlayer instance or system-side things,
 * use [org.oxycblt.auxio.playback.PlaybackService].
 *
 * All instantiation should be done with [PlaybackStateManager.from()].
 * @author OxygenCobalt
 */
class PlaybackStateManager private constructor() {
    // Playback
    private var mSong: Song? = null
        set(value) {
            field = value
            callbacks.forEach { it.onSongUpdate(value) }
        }
    private var mPosition: Long = 0
        set(value) {
            field = value
            callbacks.forEach { it.onPositionUpdate(value) }
        }
    private var mParent: BaseModel? = null
        set(value) {
            field = value
            callbacks.forEach { it.onParentUpdate(value) }
        }

    // Queue
    private var mQueue = mutableListOf<Song>()
        set(value) {
            field = value
            callbacks.forEach { it.onQueueUpdate(value) }
        }
    private var mUserQueue = mutableListOf<Song>()
        set(value) {
            field = value
            callbacks.forEach { it.onUserQueueUpdate(value) }
        }
    private var mIndex = 0
        set(value) {
            field = value
            callbacks.forEach { it.onIndexUpdate(value) }
        }
    private var mMode = PlaybackMode.ALL_SONGS
        set(value) {
            field = value
            callbacks.forEach { it.onModeUpdate(value) }
        }

    // Status
    private var mIsPlaying = false
        set(value) {
            field = value
            callbacks.forEach { it.onPlayingUpdate(value) }
        }

    private var mIsShuffling = false
        set(value) {
            field = value
            callbacks.forEach { it.onShuffleUpdate(value) }
        }
    private var mLoopMode = LoopMode.NONE
        set(value) {
            field = value
            callbacks.forEach { it.onLoopUpdate(value) }
        }
    private var mIsInUserQueue = false
    private var mIsRestored = false
    private var mShuffleSeed = -1L

    val song: Song? get() = mSong
    val parent: BaseModel? get() = mParent
    val position: Long get() = mPosition
    val queue: MutableList<Song> get() = mQueue
    val userQueue: MutableList<Song> get() = mUserQueue
    val index: Int get() = mIndex
    val mode: PlaybackMode get() = mMode
    val isPlaying: Boolean get() = mIsPlaying
    val isShuffling: Boolean get() = mIsShuffling
    val loopMode: LoopMode get() = mLoopMode
    val isRestored: Boolean get() = mIsRestored

    // --- CALLBACKS ---

    private val callbacks = mutableListOf<Callback>()

    fun addCallback(callback: Callback) {
        callbacks.add(callback)
    }

    fun removeCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    // --- PLAYING FUNCTIONS ---

    fun playSong(song: Song, mode: PlaybackMode) {
        // Auxio doesn't support playing songs while swapping the mode to GENRE, as its impossible
        // to determine what genre a song has.
        if (mode == PlaybackMode.IN_GENRE) {
            Log.e(this::class.simpleName, "Auxio cant play songs with the mode of IN_GENRE.")

            return
        }

        Log.d(this::class.simpleName, "Updating song to ${song.name} and mode to $mode")

        val musicStore = MusicStore.getInstance()

        when (mode) {
            PlaybackMode.ALL_SONGS -> {
                mParent = null
                mQueue = musicStore.songs.toMutableList()
            }

            PlaybackMode.IN_ARTIST -> {
                mParent = song.album.artist
                mQueue = song.album.artist.songs.toMutableList()
            }

            PlaybackMode.IN_ALBUM -> {
                mParent = song.album
                mQueue = song.album.songs
            }

            else -> {
            }
        }

        mMode = mode

        resetLoopMode()
        updatePlayback(song)

        if (mIsShuffling) {
            genShuffle(true)
        } else {
            resetShuffle()
        }

        mIndex = mQueue.indexOf(song)
    }

    fun playParentModel(baseModel: BaseModel, shuffled: Boolean) {
        // This should never occur.
        if (baseModel is Song || baseModel is Header) {
            Log.e(
                this::class.simpleName,
                "playParentModel does not support ${baseModel::class.simpleName}."
            )

            return
        }

        Log.d(this::class.simpleName, "Playing ${baseModel.name}")

        mParent = baseModel
        mIndex = 0
        mIsShuffling = shuffled

        when (baseModel) {
            is Album -> {
                mQueue = orderSongsInAlbum(baseModel)
                mMode = PlaybackMode.IN_ALBUM
            }
            is Artist -> {
                mQueue = orderSongsInArtist(baseModel)
                mMode = PlaybackMode.IN_ARTIST
            }
            is Genre -> {
                mQueue = orderSongsInGenre(baseModel)
                mMode = PlaybackMode.IN_GENRE
            }

            else -> {
            }
        }

        resetLoopMode()

        updatePlayback(mQueue[0])

        if (mIsShuffling) {
            genShuffle(false)
        } else {
            resetShuffle()
        }
    }

    private fun updatePlayback(song: Song) {
        mSong = song
        mPosition = 0

        if (!mIsPlaying) {
            mIsPlaying = true
        }

        mIsInUserQueue = false
    }

    fun setPosition(position: Long) {
        // Don't accept any bugged positions that are over the duration of the song.
        mSong?.let {
            if (position <= it.duration) {
                mPosition = position
            }
        }
    }

    fun seekTo(position: Long) {
        mPosition = position

        callbacks.forEach { it.onSeekConfirm(position) }
    }

    // --- QUEUE FUNCTIONS ---

    fun next() {
        resetLoopMode()

        if (mUserQueue.isNotEmpty()) {
            updatePlayback(mUserQueue[0])
            mUserQueue.removeAt(0)

            mIsInUserQueue = true

            forceUserQueueUpdate()
        } else {
            if (mIndex < mQueue.lastIndex) {
                mIndex = mIndex.inc()
            } else {
                // TODO: Implement option to make the playlist loop instead of stop
                mQueue = mutableListOf()
                mSong = null

                return
            }

            updatePlayback(mQueue[mIndex])
            forceQueueUpdate()
        }
    }

    fun prev() {
        if (mIndex > 0 && !mIsInUserQueue) {
            mIndex = mIndex.dec()
        }

        resetLoopMode()

        updatePlayback(mQueue[mIndex])

        forceQueueUpdate()
    }

    fun removeQueueItem(index: Int) {
        Log.d(this::class.simpleName, "Removing item ${mQueue[index].name}.")

        if (index > mQueue.size || index < 0) {
            Log.e(this::class.simpleName, "Index is out of bounds, did not remove queue item.")

            return
        }

        mQueue.removeAt(index)

        forceQueueUpdate()
    }

    fun moveQueueItems(from: Int, to: Int) {
        try {
            val item = mQueue.removeAt(from)
            mQueue.add(to, item)
        } catch (exception: IndexOutOfBoundsException) {
            Log.e(this::class.simpleName, "Indices were out of bounds, did not move queue item")

            return
        }

        forceQueueUpdate()
    }

    fun addToUserQueue(song: Song) {
        mUserQueue.add(song)

        forceUserQueueUpdate()
    }

    fun addToUserQueue(songs: List<Song>) {
        mUserQueue.addAll(songs)

        forceUserQueueUpdate()
    }

    fun removeUserQueueItem(index: Int) {
        Log.d(this::class.simpleName, "Removing item ${mUserQueue[index].name}.")

        if (index > mUserQueue.size || index < 0) {
            Log.e(this::class.simpleName, "Index is out of bounds, did not remove queue item.")

            return
        }

        mUserQueue.removeAt(index)

        forceUserQueueUpdate()
    }

    fun moveUserQueueItems(from: Int, to: Int) {
        try {
            val item = mUserQueue.removeAt(from)
            mUserQueue.add(to, item)
        } catch (exception: IndexOutOfBoundsException) {
            Log.e(this::class.simpleName, "Indices were out of bounds, did not move queue item")

            return
        }

        forceUserQueueUpdate()
    }

    // Force any callbacks to update when the queue is changed.
    private fun forceQueueUpdate() {
        mQueue = mQueue
    }

    private fun forceUserQueueUpdate() {
        mUserQueue = mUserQueue
    }

    // --- SHUFFLE FUNCTIONS ---

    fun shuffleAll() {
        val musicStore = MusicStore.getInstance()

        mIsShuffling = true
        mQueue = musicStore.songs.toMutableList()
        mMode = PlaybackMode.ALL_SONGS
        mIndex = 0

        genShuffle(false)
        updatePlayback(mQueue[0])
    }

    // Generate a new shuffled queue.
    private fun genShuffle(keepSong: Boolean) {
        val newSeed = Random.Default.nextLong()

        Log.d(this::class.simpleName, "Shuffling queue with seed $newSeed")

        mShuffleSeed = newSeed

        mQueue.shuffle(Random(newSeed))
        mIndex = 0

        // If specified, make the current song the first member of the queue.
        if (keepSong) {
            mSong?.let {
                moveQueueItems(mQueue.indexOf(it), 0)
            }
        } else {
            // Otherwise, just start from the zeroth position in the queue.
            mSong = mQueue[0]
        }

        forceQueueUpdate()
    }

    // Stop the queue and attempt to restore to the previous state
    private fun resetShuffle() {
        mShuffleSeed = -1L

        setupOrderedQueue()

        mIndex = mQueue.indexOf(mSong)

        forceQueueUpdate()
    }

    // --- STATE FUNCTIONS ---

    fun setPlayingStatus(value: Boolean) {
        if (mIsPlaying != value) {
            mIsPlaying = value
        }
    }

    fun setShuffleStatus(value: Boolean) {
        mIsShuffling = value

        if (mIsShuffling) {
            genShuffle(true)
        } else {
            resetShuffle()
        }
    }

    fun setLoopMode(mode: LoopMode) {
        mLoopMode = mode
    }

    private fun resetLoopMode() {
        // Reset the loop mode from ONCE if needed.
        if (mLoopMode == LoopMode.ONCE) {
            mLoopMode = LoopMode.NONE
        }
    }

    // --- PERSISTENCE FUNCTIONS ---
    // TODO: Implement a fast queue save function

    suspend fun saveStateToDatabase(context: Context) {
        Log.d(this::class.simpleName, "Saving state to DB.")

        val start = System.currentTimeMillis()

        withContext(Dispatchers.IO) {
            val playbackState = packToPlaybackState()
            val queueItems = packQueue()

            val database = PlaybackStateDatabase.getInstance(context)
            database.writeState(playbackState)
            database.writeQueue(queueItems)
        }

        val time = System.currentTimeMillis() - start

        Log.d(this::class.simpleName, "Save finished in ${time}ms")
    }

    suspend fun getStateFromDatabase(context: Context) {
        Log.d(this::class.simpleName, "Getting state from DB.")

        val start = System.currentTimeMillis()

        val state: PlaybackState?

        val queueItems = withContext(Dispatchers.IO) {
            val database = PlaybackStateDatabase.getInstance(context)

            state = database.readState()
            database.readQueue()
        }

        val loadTime = System.currentTimeMillis() - start

        Log.d(this::class.simpleName, "Load finished in ${loadTime}ms")

        state?.let {
            Log.d(this::class.simpleName, "Valid playback state $it")
            Log.d(this::class.simpleName, "Valid queue size ${queueItems.size}")

            unpackFromPlaybackState(it)
            unpackQueue(queueItems)
        }

        val time = System.currentTimeMillis() - start

        Log.d(this::class.simpleName, "Restore finished in ${time}ms")

        mIsRestored = true
    }

    private fun packToPlaybackState(): PlaybackState {
        val songId = mSong?.id ?: -1L
        val parentId = mParent?.id ?: -1L
        val intMode = mMode.toConstant()
        val intLoopMode = mLoopMode.toConstant()

        return PlaybackState(
            songId = songId,
            position = mPosition,
            parentId = parentId,
            index = mIndex,
            mode = intMode,
            isShuffling = mIsShuffling,
            shuffleSeed = mShuffleSeed,
            loopMode = intLoopMode,
            inUserQueue = mIsInUserQueue
        )
    }

    private fun packQueue(): List<QueueItem> {
        val unified = mutableListOf<QueueItem>()

        var queueItemId = 0L

        mUserQueue.forEach {
            unified.add(QueueItem(queueItemId, it.id, it.albumId, true))
            queueItemId++
        }

        mQueue.forEach {
            unified.add(QueueItem(queueItemId, it.id, it.albumId, false))
            queueItemId++
        }

        return unified
    }

    private fun unpackFromPlaybackState(playbackState: PlaybackState) {
        val musicStore = MusicStore.getInstance()

        // Turn the simplified information from PlaybackState into values that can be used
        mSong = musicStore.songs.find { it.id == playbackState.songId }
        mPosition = playbackState.position
        mParent = musicStore.parents.find { it.id == playbackState.parentId }
        mMode = PlaybackMode.fromConstant(playbackState.mode) ?: PlaybackMode.ALL_SONGS
        mLoopMode = LoopMode.fromConstant(playbackState.loopMode) ?: LoopMode.NONE
        mIsShuffling = playbackState.isShuffling
        mShuffleSeed = playbackState.shuffleSeed
        mIsInUserQueue = playbackState.inUserQueue
        mIndex = playbackState.index

        callbacks.forEach {
            it.onSeekConfirm(mPosition)
            it.onModeUpdate(mMode)
            it.onRestoreFinish()
        }
    }

    private fun unpackQueue(queueItems: List<QueueItem>) {
        val musicStore = MusicStore.getInstance()

        queueItems.forEach { item ->
            // Traverse albums and then album songs instead of just the songs, as its faster.
            musicStore.albums.find { it.id == item.albumId }
                ?.songs?.find { it.id == item.songId }?.let {
                if (item.isUserQueue) {
                    mUserQueue.add(it)
                } else {
                    mQueue.add(it)
                }
            }
        }

        // Get a more accurate index [At least if were not in the user queue]
        if (!mIsInUserQueue) {
            mSong?.let {
                val index = mQueue.indexOf(it)
                mIndex = if (index != -1) index else mIndex
            }
        }

        forceQueueUpdate()
        forceUserQueueUpdate()
    }

    // --- ORDERING FUNCTIONS ---

    private fun setupOrderedQueue() {
        mQueue = when (mMode) {
            PlaybackMode.IN_ARTIST -> orderSongsInArtist(mParent as Artist)
            PlaybackMode.IN_ALBUM -> orderSongsInAlbum(mParent as Album)
            PlaybackMode.IN_GENRE -> orderSongsInGenre(mParent as Genre)
            PlaybackMode.ALL_SONGS -> MusicStore.getInstance().songs.toMutableList()
        }
    }

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

    /**
     * The interface for receiving updates from [PlaybackStateManager].
     * Add the callback to [PlaybackStateManager] using [addCallback],
     * remove them on destruction with [removeCallback].
     */
    interface Callback {
        fun onSongUpdate(song: Song?) {}
        fun onParentUpdate(parent: BaseModel?) {}
        fun onPositionUpdate(position: Long) {}
        fun onQueueUpdate(queue: MutableList<Song>) {}
        fun onUserQueueUpdate(userQueue: MutableList<Song>) {}
        fun onModeUpdate(mode: PlaybackMode) {}
        fun onIndexUpdate(index: Int) {}
        fun onPlayingUpdate(isPlaying: Boolean) {}
        fun onShuffleUpdate(isShuffling: Boolean) {}
        fun onLoopUpdate(mode: LoopMode) {}
        fun onSeekConfirm(position: Long) {}
        fun onRestoreFinish() {}
    }

    companion object {
        @Volatile
        private var INSTANCE: PlaybackStateManager? = null

        /**
         * Get/Instantiate the single instance of [PlaybackStateManager].
         */
        fun getInstance(): PlaybackStateManager {
            val currentInstance = INSTANCE

            if (currentInstance != null) {
                return currentInstance
            }

            synchronized(this) {
                val newInstance = PlaybackStateManager()
                INSTANCE = newInstance
                return newInstance
            }
        }
    }
}
