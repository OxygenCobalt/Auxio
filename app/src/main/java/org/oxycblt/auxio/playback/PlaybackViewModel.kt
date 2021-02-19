package org.oxycblt.auxio.playback

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.oxycblt.auxio.R
import org.oxycblt.auxio.logD
import org.oxycblt.auxio.logE
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Parent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toDuration
import org.oxycblt.auxio.playback.queue.QueueAdapter
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.recycler.SortMode
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.createToast

/**
 * The ViewModel that provides a UI frontend for [PlaybackStateManager].
 *
 * **PLEASE Use this instead of [PlaybackStateManager], UI's are extremely volatile and this provides
 * an interface that properly sanitizes input and abstracts functions unlike the master class.**
 * @author OxygenCobalt
 */
class PlaybackViewModel : ViewModel(), PlaybackStateManager.Callback {
    // Playback
    private val mSong = MutableLiveData<Song?>()
    private val mParent = MutableLiveData<Parent?>()
    private val mPosition = MutableLiveData(0L)

    // Queue
    private val mQueue = MutableLiveData(mutableListOf<Song>())
    private val mUserQueue = MutableLiveData(mutableListOf<Song>())
    private val mIndex = MutableLiveData(0)
    private val mMode = MutableLiveData(PlaybackMode.ALL_SONGS)

    // States
    private val mIsPlaying = MutableLiveData(false)
    private val mIsShuffling = MutableLiveData(false)
    private val mLoopMode = MutableLiveData(LoopMode.NONE)
    private val mIsInUserQueue = MutableLiveData(false)

    // Other
    private val mIsSeeking = MutableLiveData(false)
    private var mCanAnimate = false

    /** The current song. */
    val song: LiveData<Song?> get() = mSong
    /** The current model that is being played from, such as an [Album] or [Artist] */
    val parent: LiveData<Parent?> get() = mParent
    /** The current playback position, in seconds */
    val position: LiveData<Long> get() = mPosition

    /** The current queue determined by [mode] and [parent] */
    val queue: LiveData<MutableList<Song>> get() = mQueue
    /** The queue created by the user. */
    val userQueue: LiveData<MutableList<Song>> get() = mUserQueue
    /** The current [PlaybackMode] that also determines the queue */
    val mode: LiveData<PlaybackMode> get() = mMode

    val isInUserQueue: LiveData<Boolean> = mIsInUserQueue

    val isPlaying: LiveData<Boolean> get() = mIsPlaying
    val isShuffling: LiveData<Boolean> get() = mIsShuffling
    /** The current repeat mode, see [LoopMode] for more information */
    val loopMode: LiveData<LoopMode> get() = mLoopMode
    val isSeeking: LiveData<Boolean> get() = mIsSeeking
    val canAnimate: Boolean get() = mCanAnimate

    /** The position as a duration string. */
    val formattedPosition = Transformations.map(mPosition) {
        it.toDuration()
    }

    /** The position as SeekBar progress. */
    val positionAsProgress = Transformations.map(mPosition) {
        if (mSong.value != null) it.toInt() else 0
    }

    /** The queue, without the previous items. */
    val nextItemsInQueue = Transformations.map(mQueue) {
        it.slice((mIndex.value!! + 1) until it.size)
    }

    private val playbackManager = PlaybackStateManager.getInstance()
    private val settingsManager = SettingsManager.getInstance()
    private val musicStore = MusicStore.getInstance()

    init {
        playbackManager.addCallback(this)

        // If the PlaybackViewModel was cleared [Signified by PlaybackStateManager still being
        // around & the fact that we are in the init function], then attempt to restore the
        // ViewModel state. If it isn't, then wait for MainFragment to give the command to restore
        // PlaybackStateManager.
        if (playbackManager.isRestored) {
            restorePlaybackState()
        }
    }

    // --- PLAYING FUNCTIONS ---

    /**
     * Play a song.
     * @param song The song to be played
     * @param mode The [PlaybackMode] for it to be played in. Defaults to the preferred song playback mode if not specified.
     */
    fun playSong(song: Song, mode: PlaybackMode = settingsManager.songPlaybackMode) {
        playbackManager.playSong(song, mode)
    }

    /** Play an album.*/
    fun playAlbum(album: Album, shuffled: Boolean) {
        if (album.songs.isEmpty()) {
            logE("Album is empty, Not playing.")

            return
        }

        playbackManager.playParent(album, shuffled)
    }

    /** Play an Artist */
    fun playArtist(artist: Artist, shuffled: Boolean) {
        if (artist.songs.isEmpty()) {
            logE("Artist is empty, Not playing.")

            return
        }

        playbackManager.playParent(artist, shuffled)
    }

    /** Play a genre. */
    fun playGenre(genre: Genre, shuffled: Boolean) {
        if (genre.songs.isEmpty()) {
            logE("Genre is empty, Not playing.")

            return
        }

        playbackManager.playParent(genre, shuffled)
    }

    /** Shuffle all songs */
    fun shuffleAll() {
        playbackManager.shuffleAll()
    }

    /** Play a song using an intent */
    fun playWithIntent(intent: Intent, context: Context) {
        val uri = intent.data ?: return

        viewModelScope.launch {
            musicStore.getSongForUri(uri, context.contentResolver)?.let { song ->
                playSong(song)
            }
        }
    }

    // --- POSITION FUNCTIONS ---

    /** Update the position and push it to [PlaybackStateManager] */
    fun setPosition(progress: Int) {
        playbackManager.seekTo((progress * 1000).toLong())
    }

    /**
     * Update the position without pushing the change to [PlaybackStateManager].
     * This is used during seek events to give the user an idea of where they're seeking to.
     * @param progress The SeekBar progress to seek to.
     */
    fun updatePositionDisplay(progress: Int) {
        mPosition.value = progress.toLong()
    }

    // --- QUEUE FUNCTIONS ---

    /** Skip to the next song. */
    fun skipNext() {
        playbackManager.next()
    }

    /** Skip to the previous song */
    fun skipPrev() {
        playbackManager.prev()
    }

    /**
     * Remove a queue OR user queue item, given a QueueAdapter index.
     * @param adapterIndex The [QueueAdapter] index to remove
     * @param queueAdapter The [QueueAdapter] itself to push changes to when successful.
     */
    fun removeQueueAdapterItem(adapterIndex: Int, queueAdapter: QueueAdapter) {
        var index = adapterIndex.dec()

        // If the item is in the user queue, then remove it from there after accounting for the header.
        if (index < mUserQueue.value!!.size) {
            queueAdapter.removeItem(adapterIndex)

            playbackManager.removeUserQueueItem(index)
        } else {
            // Translate the indices into proper queue indices if removing an item from there.
            index += (mQueue.value!!.size - nextItemsInQueue.value!!.size)

            if (userQueue.value!!.isNotEmpty()) {
                index -= mUserQueue.value!!.size.inc()
            }

            queueAdapter.removeItem(adapterIndex)

            playbackManager.removeQueueItem(index)
        }
    }

    /**
     * Move queue OR user queue items, given QueueAdapter indices.
     * @param adapterFrom The [QueueAdapter] index that needs to be moved
     * @param adapterTo The destination [QueueAdapter] index.
     * @param queueAdapter the [QueueAdapter] to push changes to when successful.
     */
    fun moveQueueAdapterItems(
        adapterFrom: Int,
        adapterTo: Int,
        queueAdapter: QueueAdapter
    ): Boolean {
        var from = adapterFrom.dec()
        var to = adapterTo.dec()

        if (from < mUserQueue.value!!.size) {
            // Ignore invalid movements to out of bounds, header, or queue positions
            if (to >= mUserQueue.value!!.size || to < 0) return false

            queueAdapter.moveItems(adapterFrom, adapterTo)

            playbackManager.moveUserQueueItems(from, to)
        } else {
            // Ignore invalid movements to out of bounds or header positions
            if (to < 0) return false

            // Get the real queue positions from the nextInQueue positions
            val delta = mQueue.value!!.size - nextItemsInQueue.value!!.size

            from += delta
            to += delta

            if (userQueue.value!!.isNotEmpty()) {
                // Ignore user queue positions
                if (to <= mUserQueue.value!!.size.inc()) return false

                from -= mUserQueue.value!!.size.inc()
                to -= mUserQueue.value!!.size.inc()

                // Ignore movements that are past the next songs
                if (to <= mIndex.value!!) return false
            }

            queueAdapter.moveItems(adapterFrom, adapterTo)

            playbackManager.moveQueueItems(from, to)
        }

        return true
    }

    /** Add a [Song] to the user queue.*/
    fun addToUserQueue(song: Song) {
        playbackManager.addToUserQueue(song)
    }

    /** Add an [Album] to the user queue */
    fun addToUserQueue(album: Album) {
        val songs = SortMode.NUMERIC_DOWN.getSortedSongList(album.songs)

        playbackManager.addToUserQueue(songs)
    }

    /** Clear the user queue entirely */
    fun clearUserQueue() {
        playbackManager.clearUserQueue()
    }

    // --- STATUS FUNCTIONS ---

    /** Flip the playing status, e.g from playing to paused */
    fun invertPlayingStatus() {
        enableAnimation()

        playbackManager.setPlaying(!playbackManager.isPlaying)
    }

    /** Flip the shuffle status, e.g from on to off. Will keep song by default. */
    fun invertShuffleStatus() {
        playbackManager.setShuffling(!playbackManager.isShuffling, keepSong = true)
    }

    /** Increment the loop status, e.g from off to loop once */
    fun incrementLoopStatus() {
        playbackManager.setLoopMode(playbackManager.loopMode.increment())
    }

    // --- SAVE/RESTORE FUNCTIONS ---

    /**
     * Force save the current [PlaybackStateManager] state to the database. Called by SettingsListFragment.
     * @param context [Context] required.
     */
    fun savePlaybackState(context: Context) {
        viewModelScope.launch {
            playbackManager.saveStateToDatabase(context)

            context.getString(R.string.debug_state_saved).createToast(context)
        }
    }

    /**
     * Get [PlaybackStateManager] to restore its state from the database, if needed. Called by MainFragment.
     * @param context [Context] required.
     */
    fun restorePlaybackIfNeeded(context: Context) {
        if (!playbackManager.isRestored) {
            viewModelScope.launch {
                playbackManager.getStateFromDatabase(context)
            }
        }
    }

    /** Attempt to restore the current playback state from an existing [PlaybackStateManager] instance */
    private fun restorePlaybackState() {
        logD("Attempting to restore playback state.")

        mSong.value = playbackManager.song
        mPosition.value = playbackManager.position / 1000
        mParent.value = playbackManager.parent
        mQueue.value = playbackManager.queue
        mMode.value = playbackManager.mode
        mUserQueue.value = playbackManager.userQueue
        mIndex.value = playbackManager.index
        mIsPlaying.value = playbackManager.isPlaying
        mIsShuffling.value = playbackManager.isShuffling
        mLoopMode.value = playbackManager.loopMode
    }

    // --- OTHER FUNCTIONS ---

    /** Set whether the seeking indicator should be highlighted */
    fun setSeekingStatus(value: Boolean) {
        mIsSeeking.value = value
    }

    /** Enable animation on CompactPlaybackFragment */
    fun enableAnimation() {
        mCanAnimate = true
    }

    /** Disable animation on CompactPlaybackFragment */
    fun disableAnimation() {
        mCanAnimate = false
    }

    // --- OVERRIDES ---

    override fun onCleared() {
        playbackManager.removeCallback(this)
    }

    override fun onSongUpdate(song: Song?) {
        mSong.value = song
    }

    override fun onParentUpdate(parent: Parent?) {
        mParent.value = parent
    }

    override fun onPositionUpdate(position: Long) {
        if (!mIsSeeking.value!!) {
            mPosition.value = position / 1000
        }
    }

    override fun onQueueUpdate(queue: MutableList<Song>) {
        mQueue.value = queue
    }

    override fun onUserQueueUpdate(userQueue: MutableList<Song>) {
        mUserQueue.value = userQueue
    }

    override fun onIndexUpdate(index: Int) {
        mIndex.value = index
    }

    override fun onModeUpdate(mode: PlaybackMode) {
        mMode.value = mode
    }

    override fun onPlayingUpdate(isPlaying: Boolean) {
        mIsPlaying.value = isPlaying
    }

    override fun onShuffleUpdate(isShuffling: Boolean) {
        mIsShuffling.value = isShuffling
    }

    override fun onLoopUpdate(mode: LoopMode) {
        mLoopMode.value = mode
    }

    override fun onInUserQueueUpdate(isInUserQueue: Boolean) {
        mIsInUserQueue.value = isInUserQueue
    }
}
