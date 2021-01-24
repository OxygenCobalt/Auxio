package org.oxycblt.auxio.playback.state

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.database.PlaybackState
import org.oxycblt.auxio.database.PlaybackStateDatabase
import org.oxycblt.auxio.database.QueueItem
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.logE
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.SortMode
import org.oxycblt.auxio.settings.SettingsManager

/**
 * Master class (and possible god object) for the playback state.
 *
 * This should ***NOT*** be used outside of the playback module.
 * - If you want to use the playback state in the UI, use [org.oxycblt.auxio.playback.PlaybackViewModel] as it can withstand volatile UIs.
 * - If you want to use the playback state with the ExoPlayer instance or system-side things, use [org.oxycblt.auxio.playback.PlaybackService].
 *
 * All access should be done with [PlaybackStateManager.getInstance].
 *
 * TODO: Queues should reflect sort mode
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
        set(value) {
            field = value
            callbacks.forEach { it.onInUserQueueUpdate(value) }
        }
    private var mIsRestored = false
    private var mHasPlayed = false

    /** The currently playing song. Null if there isn't one */
    val song: Song? get() = mSong
    /** The parent the queue is based on, null if all_songs */
    val parent: BaseModel? get() = mParent
    /** The current playback progress */
    val position: Long get() = mPosition
    /** The current queue determined by [parent] and [mode] */
    val queue: MutableList<Song> get() = mQueue
    /** The queue created by the user. */
    val userQueue: MutableList<Song> get() = mUserQueue
    /** The current index of the queue */
    val index: Int get() = mIndex
    /** The current [PlaybackMode] */
    val mode: PlaybackMode get() = mMode
    /** Whether playback is paused or not */
    val isPlaying: Boolean get() = mIsPlaying
    /** Whether the queue is shuffled */
    val isShuffling: Boolean get() = mIsShuffling
    /** The current [LoopMode] */
    val loopMode: LoopMode get() = mLoopMode
    /** Whether this instance has already been restored */
    val isRestored: Boolean get() = mIsRestored
    /** Whether this instance has started playing or not */
    val hasPlayed: Boolean get() = mHasPlayed

    private val settingsManager = SettingsManager.getInstance()
    private val musicStore = MusicStore.getInstance()

    // --- CALLBACKS ---

    private val callbacks = mutableListOf<Callback>()

    /**
     * Add a [PlaybackStateManager.Callback] to this instance.
     * Make sure to remove the callback with [removeCallback] when done.
     */
    fun addCallback(callback: Callback) {
        callbacks.add(callback)
    }

    /**
     * Remove a [PlaybackStateManager.Callback] bound to this instance.
     */
    fun removeCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    // --- PLAYING FUNCTIONS ---

    /**
     * Play a song.
     * @param song The song to be played
     * @param mode The [PlaybackMode] to construct the queue off of.
     */
    fun playSong(song: Song, mode: PlaybackMode) {
        logD("Updating song to ${song.name} and mode to $mode")

        when (mode) {
            PlaybackMode.ALL_SONGS -> {
                mParent = null
                mQueue = musicStore.songs.toMutableList()
            }

            PlaybackMode.IN_GENRE -> {
                val genre = song.genre

                // Dont do this if the genre is null
                if (genre != null) {
                    mParent = genre
                    mQueue = genre.songs.toMutableList()
                } else {
                    playSong(song, PlaybackMode.ALL_SONGS)

                    return
                }
            }

            PlaybackMode.IN_ARTIST -> {
                mParent = song.album.artist
                mQueue = song.album.artist.songs.toMutableList()
            }

            PlaybackMode.IN_ALBUM -> {
                mParent = song.album
                mQueue = song.album.songs.toMutableList()
            }
        }

        mMode = mode

        resetLoopMode()
        updatePlayback(song)
        setShuffling(settingsManager.keepShuffle && mIsShuffling, keepSong = true)
    }

    /**
     * Play a parent model, e.g an artist or an album.
     * @param baseModel The model to use
     * @param shuffled Whether to shuffle the queue or not
     */
    fun playParentModel(baseModel: BaseModel, shuffled: Boolean) {
        if (baseModel is Song || baseModel is Header) {
            // This should never occur.
            logE("playParentModel is not meant to play ${baseModel::class.simpleName}.")

            return
        }

        logD("Playing ${baseModel.name}")

        mParent = baseModel
        mIndex = 0

        when (baseModel) {
            is Album -> {
                mQueue = baseModel.songs.toMutableList()
                mMode = PlaybackMode.IN_ALBUM
            }
            is Artist -> {
                mQueue = baseModel.songs.toMutableList()
                mMode = PlaybackMode.IN_ARTIST
            }
            is Genre -> {
                mQueue = baseModel.songs.toMutableList()
                mMode = PlaybackMode.IN_GENRE
            }

            else -> {
            }
        }

        resetLoopMode()
        setShuffling(shuffled, keepSong = false)
        updatePlayback(mQueue[0])
    }

    /**
     * Shortcut function for updating what song is being played. ***USE THIS INSTEAD OF WRITING OUT ALL THE CODE YOURSELF!!!***
     * @param song The song to play
     */
    private fun updatePlayback(song: Song) {
        mIsInUserQueue = false

        mSong = song
        mPosition = 0

        if (!mIsPlaying) {
            setPlaying(true)
        }
    }

    /**
     * Update the current position. Will not notify any listeners of a seek event, that's what [seekTo] is for.
     * @param position The new position in millis.
     * @see seekTo
     */
    fun setPosition(position: Long) {
        mSong?.let {
            // Don't accept any bugged positions that are over the duration of the song.
            if (position <= it.duration) {
                mPosition = position
            }
        }
    }

    /**
     * **Seek** to a position, this calls [PlaybackStateManager.Callback.onSeek] to notify
     * elements that rely on that.
     * @param position The position to seek to in millis.
     * @see setPosition
     */
    fun seekTo(position: Long) {
        mPosition = position

        callbacks.forEach { it.onSeek(position) }
    }

    // --- QUEUE FUNCTIONS ---

    /**
     * Go to the next song, along with doing all the checks that entails.
     */
    fun next() {
        resetLoopMode()

        // If there's anything in the user queue, go to the first song in there instead
        // of incrementing the index.
        if (mUserQueue.isNotEmpty()) {
            updatePlayback(mUserQueue[0])
            mUserQueue.removeAt(0)

            // Mark that the playback state is currently in the user queue, for later.
            mIsInUserQueue = true

            forceUserQueueUpdate()
        } else {
            // Increment the index.
            // If it cant be incremented anymore, end playback or loop depending on the setting.
            if (mIndex < mQueue.lastIndex) {
                mIndex = mIndex.inc()
            } else {
                handlePlaylistEnd()

                return
            }

            updatePlayback(mQueue[mIndex])
            forceQueueUpdate()
        }
    }

    /**
     * Go to the previous song, doing any checks that are needed.
     */
    fun prev() {
        // If enabled, rewind before skipping back if the position is past 3 seconds [3000ms]
        if (settingsManager.rewindWithPrev && mPosition >= 3000) {
            rewind()
        } else {
            // Only decrement the index if there's a song to move back to AND if we are not exiting
            // the user queue.
            if (mIndex > 0 && !mIsInUserQueue) {
                mIndex = mIndex.dec()
            }

            resetLoopMode()

            updatePlayback(mQueue[mIndex])

            forceQueueUpdate()
        }
    }

    /**
     * Handle what to do at then end of a playlist.
     */
    private fun handlePlaylistEnd() {
        when (settingsManager.doAtEnd) {
            SettingsManager.EntryValues.AT_END_LOOP_PAUSE -> {
                mIndex = 0
                forceQueueUpdate()

                mSong = mQueue[0]
                mPosition = 0
                setPlaying(false)
                mIsInUserQueue = false
            }

            SettingsManager.EntryValues.AT_END_LOOP -> {
                mIndex = 0
                forceQueueUpdate()

                updatePlayback(mQueue[0])
            }

            SettingsManager.EntryValues.AT_END_STOP -> {
                mQueue.clear()
                forceQueueUpdate()

                mSong = null
                mParent = null
            }
        }
    }

    // --- QUEUE EDITING FUNCTIONS ---

    /**
     * Remove a queue item at a QUEUE index. Will log an error if the index is out of bounds
     * @param index The index at which the item should be removed.
     */
    fun removeQueueItem(index: Int): Boolean {
        logD("Removing item ${mQueue[index].name}.")

        if (index > mQueue.size || index < 0) {
            logE("Index is out of bounds, did not remove queue item.")

            return false
        }

        mQueue.removeAt(index)

        forceQueueUpdate()

        return true
    }

    /**
     * Move a queue item from a QUEUE INDEX to a QUEUE INDEX. Will log an error if one of the indices
     * is out of bounds.
     * @param from The starting item's index
     * @param to The destination index.
     */
    fun moveQueueItems(from: Int, to: Int): Boolean {
        try {
            val item = mQueue.removeAt(from)
            mQueue.add(to, item)
        } catch (exception: IndexOutOfBoundsException) {
            logE("Indices were out of bounds, did not move queue item")

            return false
        }

        forceQueueUpdate()

        return true
    }

    /**
     * Add a song to the user queue.
     * @param song The song to add
     */
    fun addToUserQueue(song: Song) {
        mUserQueue.add(song)

        forceUserQueueUpdate()
    }

    /**
     * Add a list of songs to the user queue.
     * @param songs The songs to add.
     */
    fun addToUserQueue(songs: List<Song>) {
        mUserQueue.addAll(songs)

        forceUserQueueUpdate()
    }

    /**
     * Remove a USER QUEUE item at a USER QUEUE index. Will log an error if the index is out of bounds.
     * @param index The index at which the item should be removed.
     */
    fun removeUserQueueItem(index: Int) {
        logD("Removing item ${mUserQueue[index].name}.")

        if (index > mUserQueue.size || index < 0) {
            logE("Index is out of bounds, did not remove queue item.")

            return
        }

        mUserQueue.removeAt(index)

        forceUserQueueUpdate()
    }

    /**
     * Move a USER QUEUE item from a USER QUEUE index to another USER QUEUE index. Will log an error if one of the indices
     * is out of bounds.
     * @param from The starting item's index
     * @param to The destination index.
     */
    fun moveUserQueueItems(from: Int, to: Int) {
        try {
            val item = mUserQueue.removeAt(from)
            mUserQueue.add(to, item)
        } catch (exception: IndexOutOfBoundsException) {
            logE("Indices were out of bounds, did not move queue item")

            return
        }

        forceUserQueueUpdate()
    }

    /**
     * Clear the user queue. Forces a user queue update.
     */
    fun clearUserQueue() {
        mUserQueue.clear()

        forceUserQueueUpdate()
    }

    /**
     * Force any callbacks to receive a queue update.
     */
    private fun forceQueueUpdate() {
        mQueue = mQueue
    }

    /**
     * Force any callbacks to recieve a user queue update.
     */
    private fun forceUserQueueUpdate() {
        mUserQueue = mUserQueue
    }

    // --- SHUFFLE FUNCTIONS ---

    /**
     * Shuffle all songs.
     */
    fun shuffleAll() {
        mMode = PlaybackMode.ALL_SONGS
        mQueue = musicStore.songs.toMutableList()

        setShuffling(true, keepSong = false)
        updatePlayback(mQueue[0])
    }

    /**
     * Set the shuffle status. Updates the queue accordingly
     * @param shuffling Whether the queue should be shuffled or not.
     * @param keepSong Whether the current song should be kept as the queue is shuffled/unshuffled
     */
    fun setShuffling(shuffling: Boolean, keepSong: Boolean) {
        mIsShuffling = shuffling

        if (mIsShuffling) {
            genShuffle(keepSong, mIsInUserQueue)
        } else {
            resetShuffle(keepSong, mIsInUserQueue)
        }
    }

    /**
     * Generate a new shuffled queue.
     * @param keepSong Whether the current song should be kept as the queue is shuffled
     * @param useLastSong Whether to use the last song in the queue instead of the current one
     * @return A new shuffled queue
     */
    private fun genShuffle(keepSong: Boolean, useLastSong: Boolean) {
        val lastSong = if (useLastSong) mQueue[0] else mSong

        logD("Shuffling queue")

        mQueue.shuffle()
        mIndex = 0

        // If specified, make the current song the first member of the queue.
        if (keepSong) {
            moveQueueItems(mQueue.indexOf(lastSong), 0)
        } else {
            // Otherwise, just start from the zeroth position in the queue.
            mSong = mQueue[0]
        }

        forceQueueUpdate()
    }

    /**
     * Reset the queue to its normal, ordered state.
     * @param keepSong Whether the current song should be kept as the queue is unshuffled
     * @param useLastSong Whether to use the previous song for the index calculations.
     */
    private fun resetShuffle(keepSong: Boolean, useLastSong: Boolean) {
        val lastSong = if (useLastSong) mQueue[mIndex] else mSong

        mQueue = when (mMode) {
            PlaybackMode.IN_ARTIST -> orderSongsInArtist(mParent as Artist)
            PlaybackMode.IN_ALBUM -> orderSongsInAlbum(mParent as Album)
            PlaybackMode.IN_GENRE -> orderSongsInGenre(mParent as Genre)
            PlaybackMode.ALL_SONGS -> musicStore.songs.toMutableList()
        }

        if (keepSong) {
            mIndex = mQueue.indexOf(lastSong)
        }

        forceQueueUpdate()
    }

    // --- STATE FUNCTIONS ---

    /**
     * Set the current playing status
     * @param playing Whether the playback should be playing or paused.
     */
    fun setPlaying(playing: Boolean) {
        if (mIsPlaying != playing) {
            if (playing) {
                mHasPlayed = true
            }

            mIsPlaying = playing
        }
    }

    /**
     * Rewind to the beginning of a song.
     */
    fun rewind() {
        seekTo(0)
        setPlaying(true)
    }

    /**
     * Set the [LoopMode]
     * @param mode The [LoopMode] to be used
     */
    fun setLoopMode(mode: LoopMode) {
        mLoopMode = mode
    }

    /**
     * Reset the current [LoopMode], if needed.
     * Use this instead of duplicating the code manually.
     */
    private fun resetLoopMode() {
        // Reset the loop mode from ONCE if needed.
        if (mLoopMode == LoopMode.ONCE) {
            mLoopMode = LoopMode.NONE
        }
    }

    /**
     * Reset the has played status as if this instance is fresh.
     */
    fun resetHasPlayedStatus() {
        mHasPlayed = false
    }

    // --- PERSISTENCE FUNCTIONS ---

    /**
     * Save the current state to the database.
     * @param context [Context] required
     */
    suspend fun saveStateToDatabase(context: Context) {
        logD("Saving state to DB.")

        val start = System.currentTimeMillis()

        withContext(Dispatchers.IO) {
            val playbackState = packToPlaybackState()
            val queueItems = packQueue()

            val database = PlaybackStateDatabase.getInstance(context)
            database.writeState(playbackState)
            database.writeQueue(queueItems)
        }

        val time = System.currentTimeMillis() - start

        logD("Save finished in ${time}ms")
    }

    /**
     * Restore the state from the database
     * @param context [Context] required.
     */
    suspend fun getStateFromDatabase(context: Context) {
        logD("Getting state from DB.")

        val start: Long

        val state: PlaybackState?

        val queueItems = withContext(Dispatchers.IO) {
            start = System.currentTimeMillis()

            val database = PlaybackStateDatabase.getInstance(context)

            state = database.readState()
            database.readQueue()
        }

        val loadTime = System.currentTimeMillis() - start

        logD("Load finished in ${loadTime}ms")

        state?.let {
            logD("Valid playback state $it")
            logD("Valid queue size ${queueItems.size}")

            unpackFromPlaybackState(it)
            unpackQueue(queueItems)
            doParentSanityCheck()
        }

        val time = System.currentTimeMillis() - start

        logD("Restore finished in ${time}ms")

        mIsRestored = true
    }

    /**
     * Pack the current state into a [PlaybackState] to be saved.
     * @return A [PlaybackState] reflecting the current state.
     */
    private fun packToPlaybackState(): PlaybackState {
        val songName = mSong?.name ?: ""
        val parentName = mParent?.name ?: ""
        val intMode = mMode.toInt()
        val intLoopMode = mLoopMode.toInt()

        return PlaybackState(
            songName = songName,
            position = mPosition,
            parentName = parentName,
            index = mIndex,
            mode = intMode,
            isShuffling = mIsShuffling,
            loopMode = intLoopMode,
            inUserQueue = mIsInUserQueue
        )
    }

    /**
     * Unpack the state from a [PlaybackState]
     * @param playbackState The state to unpack.
     */
    private fun unpackFromPlaybackState(playbackState: PlaybackState) {
        // Turn the simplified information from PlaybackState into values that can be used
        mSong = musicStore.songs.find { it.name == playbackState.songName }
        mPosition = playbackState.position
        mParent = musicStore.parents.find { it.name == playbackState.parentName }
        mMode = PlaybackMode.fromInt(playbackState.mode) ?: PlaybackMode.ALL_SONGS
        mLoopMode = LoopMode.fromInt(playbackState.loopMode) ?: LoopMode.NONE
        mIsShuffling = playbackState.isShuffling
        mIsInUserQueue = playbackState.inUserQueue
        mIndex = playbackState.index

        callbacks.forEach {
            it.onSeek(mPosition)
            it.onModeUpdate(mMode)
            it.onRestoreFinish()
        }
    }

    /**
     * Pack the queue into a list of [QueueItem]s to be saved.
     * @return A list of packed queue items.
     */
    private fun packQueue(): List<QueueItem> {
        val unified = mutableListOf<QueueItem>()

        var queueItemId = 0L

        mUserQueue.forEach {
            unified.add(QueueItem(queueItemId, it.name, it.album.name, true))
            queueItemId++
        }

        mQueue.forEach {
            unified.add(QueueItem(queueItemId, it.name, it.album.name, false))
            queueItemId++
        }

        return unified
    }

    /**
     * Unpack a list of queue items into a queue & user queue.
     * @param queueItems The list of [QueueItem]s to unpack.
     */
    private fun unpackQueue(queueItems: List<QueueItem>) {
        queueItems.forEach { item ->
            // Traverse albums and then album songs instead of just the songs, as its faster.
            musicStore.albums.find { it.name == item.albumName }
                ?.songs?.find { it.name == item.songName }?.let {
                    if (item.isUserQueue) {
                        mUserQueue.add(it)
                    } else {
                        mQueue.add(it)
                    }
                }
        }

        // When done, get a more accurate index to prevent issues with queue songs that were saved
        // to the db but are now deleted when the restore occurred.
        // Not done if in user queue because that could result in a bad index being created.
        if (!mIsInUserQueue) {
            mSong?.let {
                val index = mQueue.indexOf(it)
                mIndex = if (index != -1) index else mIndex
            }
        }

        forceQueueUpdate()
        forceUserQueueUpdate()
    }

    /**
     * Do the sanity check to make sure the parent was not lost in the restore process.
     */
    private fun doParentSanityCheck() {
        // Check if the parent was lost while in the DB.
        if (mSong != null && mParent == null && mMode != PlaybackMode.ALL_SONGS) {
            logD("Parent lost, attempting restore.")

            mParent = when (mMode) {
                PlaybackMode.IN_ALBUM -> mQueue.firstOrNull()?.album
                PlaybackMode.IN_ARTIST -> mQueue.firstOrNull()?.album?.artist
                PlaybackMode.IN_GENRE -> mQueue.firstOrNull()?.genre
                PlaybackMode.ALL_SONGS -> null
            }
        }
    }

    // --- ORDERING FUNCTIONS ---

    /**
     * Create an ordered queue based on an [Album].
     */
    private fun orderSongsInAlbum(album: Album): MutableList<Song> {
        return SortMode.NUMERIC_DOWN.getSortedSongList(album.songs).toMutableList()
    }

    /**
     * Create an ordered queue based on an [Artist].
     */
    private fun orderSongsInArtist(artist: Artist): MutableList<Song> {
        return SortMode.NUMERIC_DOWN.getSortedArtistSongList(artist.songs).toMutableList()
    }

    /**
     * Create an ordered queue based on a [Genre].
     */
    private fun orderSongsInGenre(genre: Genre): MutableList<Song> {
        return SortMode.ALPHA_DOWN.getSortedSongList(genre.songs).toMutableList()
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
        fun onSeek(position: Long) {}
        fun onInUserQueueUpdate(isInUserQueue: Boolean) {}
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
