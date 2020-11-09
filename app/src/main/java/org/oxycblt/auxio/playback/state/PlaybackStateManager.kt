package org.oxycblt.auxio.playback.state

import android.util.Log
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import kotlin.random.Random

// The manager of the current playback state [Current Song, Queue, Shuffling]
// This class is for sole use by the code in /playback/.
// If you want to add system-side things, add to PlaybackService.
// If you want to add ui-side things, add to PlaybackViewModel.
// [Yes, I know MediaSessionCompat exists, but I like having full control over the
// playback state instead of dealing with android's likely buggy code.]
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
    private var mShuffleSeed = -1L
    private var mLoopMode = LoopMode.NONE
        set(value) {
            field = value
            callbacks.forEach { it.onLoopUpdate(value) }
        }
    private var mIsInUserQueue = false

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

        mParent = when (mode) {
            PlaybackMode.ALL_SONGS -> null
            PlaybackMode.IN_ARTIST -> song.album.artist
            PlaybackMode.IN_ALBUM -> song.album
            PlaybackMode.IN_GENRE -> error("what")
        }

        mMode = mode

        mQueue = when (mode) {
            PlaybackMode.ALL_SONGS -> musicStore.songs.toMutableList()
            PlaybackMode.IN_ARTIST -> song.album.artist.songs
            PlaybackMode.IN_ALBUM -> song.album.songs
            PlaybackMode.IN_GENRE -> error("what")
        }

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
        // Take a random seed and then shuffle the current queue based off of that.
        // This seed will be saved in a database, so that the shuffle mode
        // can be restored when its started again.
        val newSeed = Random.Default.nextLong()

        Log.d(this::class.simpleName, "Shuffling queue with a seed of $newSeed.")

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
        mShuffleSeed = -1

        mQueue = when (mMode) {
            PlaybackMode.IN_ARTIST -> orderSongsInArtist(mParent as Artist)
            PlaybackMode.IN_ALBUM -> orderSongsInAlbum(mParent as Album)
            PlaybackMode.IN_GENRE -> orderSongsInGenre(mParent as Genre)
            PlaybackMode.ALL_SONGS -> MusicStore.getInstance().songs.toMutableList()
        }

        mIndex = mQueue.indexOf(mSong)
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
        // Reset the loop mode froM ONCE if needed.
        if (mLoopMode == LoopMode.ONCE) {
            mLoopMode = LoopMode.NONE
        }
    }

    // --- ORDERING FUNCTIONS ---

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

    companion object {
        @Volatile
        private var INSTANCE: PlaybackStateManager? = null

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
    }
}
