/*
 * Copyright (c) 2021 Auxio Project
 * PlaybackViewModel.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.oxycblt.auxio.playback

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.LoopMode
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE

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
    private val mParent = MutableLiveData<MusicParent?>()

    // States
    private val mIsPlaying = MutableLiveData(false)
    private val mIsShuffling = MutableLiveData(false)
    private val mLoopMode = MutableLiveData(LoopMode.NONE)
    private val mPosition = MutableLiveData(0L)

    // Queue
    private val mNextUp = MutableLiveData(listOf<Song>())
    private val mMode = MutableLiveData(PlaybackMode.ALL_SONGS)

    // Other
    private var mIntentUri: Uri? = null

    /** The current song. */
    val song: LiveData<Song?> get() = mSong
    /** The current model that is being played from, such as an [Album] or [Artist] */
    val parent: LiveData<MusicParent?> get() = mParent

    val isPlaying: LiveData<Boolean> get() = mIsPlaying
    val isShuffling: LiveData<Boolean> get() = mIsShuffling
    /** The current repeat mode, see [LoopMode] for more information */
    val loopMode: LiveData<LoopMode> get() = mLoopMode
    /** The current playback position, in seconds */
    val position: LiveData<Long> get() = mPosition

    /** The queue, without the previous items. */
    val nextUp: LiveData<List<Song>> get() = mNextUp
    /** The current [PlaybackMode] that also determines the queue */
    val playbackMode: LiveData<PlaybackMode> get() = mMode

    private val playbackManager = PlaybackStateManager.getInstance()
    private val settingsManager = SettingsManager.getInstance()

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
     * Play a [song] with the [mode] specified. [mode] will default to the preferred song
     * playback mode of the user if not specified.
     */
    fun playSong(song: Song, mode: PlaybackMode = settingsManager.songPlaybackMode) {
        playbackManager.playSong(song, mode)
    }

    /**
     * Play an [album].
     * @param shuffled Whether to shuffle the new queue
     */
    fun playAlbum(album: Album, shuffled: Boolean) {
        if (album.songs.isEmpty()) {
            logE("Album is empty, Not playing")

            return
        }

        playbackManager.playParent(album, shuffled)
    }

    /**
     * Play an [artist].
     * @param shuffled Whether to shuffle the new queue
     */
    fun playArtist(artist: Artist, shuffled: Boolean) {
        if (artist.songs.isEmpty()) {
            logE("Artist is empty, Not playing")

            return
        }

        playbackManager.playParent(artist, shuffled)
    }

    /**
     * Play a [genre].
     * @param shuffled Whether to shuffle the new queue
     */
    fun playGenre(genre: Genre, shuffled: Boolean) {
        if (genre.songs.isEmpty()) {
            logE("Genre is empty, Not playing")

            return
        }

        playbackManager.playParent(genre, shuffled)
    }

    /**
     * Play using a file [uri].
     * This will not play instantly during the initial startup sequence.
     */
    fun playWithUri(uri: Uri, context: Context) {
        // Check if everything is already running to run the URI play
        if (playbackManager.isRestored && MusicStore.loaded()) {
            playWithUriInternal(uri, context)
        } else {
            logD("Cant play this URI right now, waiting")

            mIntentUri = uri
        }
    }

    /**
     * Play with a file URI.
     * This is called after [playWithUri] once its deemed safe to do so.
     */
    private fun playWithUriInternal(uri: Uri, context: Context) {
        logD("Playing with uri $uri")

        val musicStore = MusicStore.maybeGetInstance() ?: return

        musicStore.findSongForUri(uri, context.contentResolver)?.let { song ->
            playSong(song)
        }
    }

    /**
     * Shuffle all songs
     */
    fun shuffleAll() {
        playbackManager.shuffleAll()
    }

    // --- POSITION FUNCTIONS ---

    /**
     * Update the position and push it to [PlaybackStateManager]
     */
    fun setPosition(progress: Long) {
        playbackManager.seekTo((progress * 1000))
    }

    // --- QUEUE FUNCTIONS ---

    /**
     * Skip to the next song.
     */
    fun skipNext() {
        playbackManager.next()
    }

    /**
     * Skip to the previous song.
     */
    fun skipPrev() {
        playbackManager.prev()
    }

    /**
     * Remove a queue item using it's recyclerview adapter index. If the indices are valid,
     * [apply] is called just before the change is committed so that the adapter can be updated.
     */
    fun removeQueueDataItem(adapterIndex: Int, apply: () -> Unit) {
        val index = adapterIndex + (playbackManager.queue.size - mNextUp.value!!.size)
        if (index in playbackManager.queue.indices) {
            apply()
            playbackManager.removeQueueItem(index)
        }
    }
    /**
     * Move queue items using their recyclerview adapter indices. If the indices are valid,
     * [apply] is called just before the change is committed so that the adapter can be updated.
     */
    fun moveQueueDataItems(adapterFrom: Int, adapterTo: Int, apply: () -> Unit): Boolean {
        val delta = (playbackManager.queue.size - mNextUp.value!!.size)
        val from = adapterFrom + delta
        val to = adapterTo + delta
        if (from in playbackManager.queue.indices && to in playbackManager.queue.indices) {
            apply()
            playbackManager.moveQueueItems(from, to)
            return true
        }

        return false
    }

    /**
     * Add a [Song] to the top of the queue.
     */
    fun playNext(song: Song) {
        playbackManager.playNext(song)
    }

    /**
     * Add an [Album] to the top of the queue.
     */
    fun playNext(album: Album) {
        playbackManager.playNext(settingsManager.detailAlbumSort.sortAlbum(album))
    }

/**
     * Add a [Song] to the end of the queue.
     */
    fun addToQueue(song: Song) {
        playbackManager.addToQueue(song)
    }

    /**
     * Add an [Album] to the end of the queue.
     */
    fun addToQueue(album: Album) {
        playbackManager.addToQueue(settingsManager.detailAlbumSort.sortAlbum(album))
    }

// --- STATUS FUNCTIONS ---

    /**
     * Flip the playing status, e.g from playing to paused
     */
    fun invertPlayingStatus() {
        playbackManager.setPlaying(!playbackManager.isPlaying)
    }

    /**
     * Flip the shuffle status, e.g from on to off. Will keep song by default.
     */
    fun invertShuffleStatus() {
        playbackManager.setShuffling(!playbackManager.isShuffling, true)
    }

    /**
     * Increment the loop status, e.g from off to loop once
     */
    fun incrementLoopStatus() {
        playbackManager.setLoopMode(playbackManager.loopMode.increment())
    }

    // --- SAVE/RESTORE FUNCTIONS ---

    /**
     * Force save the current [PlaybackStateManager] state to the database.
     * Called by SettingsListFragment.
     */
    fun savePlaybackState(context: Context, onDone: () -> Unit) {
        viewModelScope.launch {
            playbackManager.saveStateToDatabase(context)
            onDone()
        }
    }

    /**
     * Restore playback on startup. This can do one of two things:
     * - Play a file intent that was given by MainActivity in [playWithUri]
     * - Restore the last playback state if there is no active file intent.
     */
    fun setupPlayback(context: Context) {
        val intentUri = mIntentUri

        if (intentUri != null) {
            playWithUriInternal(intentUri, context)
            // Remove the uri after finishing the calls so that this does not fire again.
            mIntentUri = null

            // Were not going to be restoring playbackManager after this, so mark it as such.
            playbackManager.markRestored()
        } else if (!playbackManager.isRestored) {
            // Otherwise just restore
            viewModelScope.launch {
                playbackManager.restoreFromDatabase(context)
            }
        }
    }

    /**
     * Attempt to restore the current playback state from an existing
     * [PlaybackStateManager] instance.
     */
    private fun restorePlaybackState() {
        logD("Attempting to restore playback state")

        onSongUpdate(playbackManager.song)
        onPositionUpdate(playbackManager.position)
        onParentUpdate(playbackManager.parent)
        onQueueUpdate(playbackManager.queue, playbackManager.index)
        onModeUpdate(playbackManager.playbackMode)
        onPlayingUpdate(playbackManager.isPlaying)
        onShuffleUpdate(playbackManager.isShuffling)
        onLoopUpdate(playbackManager.loopMode)
    }

    // --- OVERRIDES ---

    override fun onCleared() {
        playbackManager.removeCallback(this)
    }

    override fun onSongUpdate(song: Song?) {
        mSong.value = song
    }

    override fun onParentUpdate(parent: MusicParent?) {
        mParent.value = parent
    }

    override fun onPositionUpdate(position: Long) {
        mPosition.value = position / 1000
    }

    override fun onQueueUpdate(queue: List<Song>, index: Int) {
        mNextUp.value = queue.slice(index.inc() until queue.size)
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

    override fun onLoopUpdate(loopMode: LoopMode) {
        mLoopMode.value = loopMode
    }
}
