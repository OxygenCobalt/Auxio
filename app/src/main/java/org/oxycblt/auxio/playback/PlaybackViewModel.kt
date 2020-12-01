package org.oxycblt.auxio.playback

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toDuration
import org.oxycblt.auxio.playback.queue.QueueAdapter
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.recycler.SortMode

/**
 * The ViewModel that provides a UI frontend for [PlaybackStateManager].
 * @author OxygenCobalt
 */
class PlaybackViewModel : ViewModel(), PlaybackStateManager.Callback {
    // Playback
    private val mSong = MutableLiveData<Song?>()
    val song: LiveData<Song?> get() = mSong

    private val mParent = MutableLiveData<BaseModel?>()
    val parent: LiveData<BaseModel?> get() = mParent

    private val mPosition = MutableLiveData(0L)
    val position: LiveData<Long> get() = mPosition

    // Queue
    private val mQueue = MutableLiveData(mutableListOf<Song>())
    val queue: LiveData<MutableList<Song>> get() = mQueue

    private val mUserQueue = MutableLiveData(mutableListOf<Song>())
    val userQueue: LiveData<MutableList<Song>> get() = mUserQueue

    private val mIndex = MutableLiveData(0)
    val index: LiveData<Int> get() = mIndex

    private val mMode = MutableLiveData(PlaybackMode.ALL_SONGS)
    val mode: LiveData<PlaybackMode> get() = mMode

    // States
    private val mIsPlaying = MutableLiveData(false)
    val isPlaying: LiveData<Boolean> get() = mIsPlaying

    private val mIsShuffling = MutableLiveData(false)
    val isShuffling: LiveData<Boolean> get() = mIsShuffling

    private val mLoopMode = MutableLiveData(LoopMode.NONE)
    val loopMode: LiveData<LoopMode> get() = mLoopMode

    // Other
    private val mIsSeeking = MutableLiveData(false)
    val isSeeking: LiveData<Boolean> get() = mIsSeeking

    private val mNavToItem = MutableLiveData<BaseModel?>()
    val navToItem: LiveData<BaseModel?> get() = mNavToItem

    private var mCanAnimate = false
    val canAnimate: Boolean get() = mCanAnimate

    val formattedPosition = Transformations.map(mPosition) {
        it.toDuration()
    }

    val positionAsProgress = Transformations.map(mPosition) {
        if (mSong.value != null) it.toInt() else 0
    }

    val nextItemsInQueue = Transformations.map(mQueue) {
        it.slice((mIndex.value!! + 1) until it.size)
    }

    private val playbackManager = PlaybackStateManager.getInstance()

    init {
        playbackManager.addCallback(this)

        // If the PlaybackViewModel was cleared [Signified by PlaybackStateManager still being
        // around & the fact that we are in the init function], then attempt to restore the
        // viewmodel state. If it isn't, then wait for MainFragment to give the command to restore
        // PlaybackStateManager.
        if (playbackManager.isRestored) {
            restorePlaybackState()
        }
    }

    // --- PLAYING FUNCTIONS ---

    // Play a song
    fun playSong(song: Song, mode: PlaybackMode) {
        playbackManager.playSong(song, mode)
    }

    // Play all songs
    fun shuffleAll() {
        playbackManager.shuffleAll()
    }

    // Play an album
    fun playAlbum(album: Album, shuffled: Boolean) {
        if (album.songs.isEmpty()) {
            Log.e(this::class.simpleName, "Album is empty, Not playing.")

            return
        }

        playbackManager.playParentModel(album, shuffled)
    }

    // Play an artist
    fun playArtist(artist: Artist, shuffled: Boolean) {
        if (artist.songs.isEmpty()) {
            Log.e(this::class.simpleName, "Artist is empty, Not playing.")

            return
        }

        playbackManager.playParentModel(artist, shuffled)
    }

    // Play a genre
    fun playGenre(genre: Genre, shuffled: Boolean) {
        if (genre.songs.isEmpty()) {
            Log.e(this::class.simpleName, "Genre is empty, Not playing.")

            return
        }

        playbackManager.playParentModel(genre, shuffled)
    }

    // --- POSITION FUNCTIONS ---

    // Update the position without pushing the change to playbackManager.
    // This is used during seek events to give the user an idea of where they're seeking to.
    fun updatePositionDisplay(progress: Int) {
        mPosition.value = progress.toLong()
    }

    // Update the position and push the change the playbackManager.
    fun updatePosition(progress: Int) {
        playbackManager.seekTo((progress * 1000).toLong())
    }

    // --- QUEUE FUNCTIONS ---

    // Skip to next song.
    fun skipNext() {
        playbackManager.next()
    }

    // Skip to last song.
    fun skipPrev() {
        playbackManager.prev()
    }

    // Remove a queue OR user queue item, given a QueueAdapter index.
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

    // Move queue OR user queue items, given QueueAdapter indices.
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

    fun addToUserQueue(song: Song) {
        playbackManager.addToUserQueue(song)
    }

    fun addToUserQueue(album: Album) {
        val songs = SortMode.NUMERIC_DOWN.getSortedSongList(album.songs)

        playbackManager.addToUserQueue(songs)
    }

    fun clearUserQueue() {
        playbackManager.clearUserQueue()
    }

    // --- STATUS FUNCTIONS ---

    // Flip the playing status.
    fun invertPlayingStatus() {
        enableAnimation()

        playbackManager.setPlayingStatus(!playbackManager.isPlaying)
    }

    // Flip the shuffle status.
    fun invertShuffleStatus() {
        playbackManager.setShuffleStatus(!playbackManager.isShuffling)
    }

    fun incrementLoopStatus() {
        playbackManager.setLoopMode(playbackManager.loopMode.increment())
    }

    // --- OTHER FUNCTIONS ---

    fun setSeekingStatus(value: Boolean) {
        mIsSeeking.value = value
    }

    fun restorePlaybackIfNeeded(context: Context) {
        if (!playbackManager.isRestored) {
            viewModelScope.launch {
                playbackManager.getStateFromDatabase(context)
            }
        }
    }

    fun save(context: Context) {
        viewModelScope.launch {
            playbackManager.saveStateToDatabase(context)
        }
    }

    fun navToItem(item: BaseModel) {
        mNavToItem.value = item
    }

    fun doneWithNavToItem() {
        mNavToItem.value = null
    }

    fun enableAnimation() {
        mCanAnimate = true
    }

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

    override fun onParentUpdate(parent: BaseModel?) {
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

    private fun restorePlaybackState() {
        Log.d(this::class.simpleName, "Attempting to restore playback state.")

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
}
