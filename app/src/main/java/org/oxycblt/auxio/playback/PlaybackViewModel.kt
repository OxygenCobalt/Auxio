/*
 * Copyright (c) 2021 Auxio Project
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackMode
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * The ViewModel that provides a UI frontend for [PlaybackStateManager].
 *
 * **PLEASE Use this instead of [PlaybackStateManager], UIs are extremely volatile and this provides
 * an interface that properly sanitizes input and abstracts functions unlike the master class.**
 *
 * @author OxygenCobalt
 */
class PlaybackViewModel : ViewModel(), PlaybackStateManager.Callback, MusicStore.Callback {
    private val musicStore = MusicStore.getInstance()
    private val settingsManager = SettingsManager.getInstance()
    private val playbackManager = PlaybackStateManager.getInstance()

    private var pendingDelayedAction: DelayedActionImpl? = null

    private val _song = MutableStateFlow<Song?>(null)
    /** The current song. */
    val song: StateFlow<Song?>
        get() = _song
    private val _parent = MutableStateFlow<MusicParent?>(null)
    /** The current model that is being played from, such as an [Album] or [Artist] */
    val parent: StateFlow<MusicParent?> = _parent
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean>
        get() = _isPlaying
    private val _positionSecs = MutableStateFlow(0L)
    /** The current playback position, in seconds */
    val positionSecs: StateFlow<Long>
        get() = _positionSecs
    private val _repeatMode = MutableStateFlow(RepeatMode.NONE)
    /** The current repeat mode, see [RepeatMode] for more information */
    val repeatMode: StateFlow<RepeatMode>
        get() = _repeatMode
    private val _isShuffled = MutableStateFlow(false)
    val isShuffled: StateFlow<Boolean>
        get() = _isShuffled

    private val _nextUp = MutableStateFlow(listOf<Song>())
    /** The queue, without the previous items. */
    val nextUp: StateFlow<List<Song>>
        get() = _nextUp

    init {
        musicStore.addCallback(this)
        playbackManager.addCallback(this)
    }

    // --- PLAYING FUNCTIONS ---

    /**
     * Play a [song] with the [mode] specified. [mode] will default to the preferred song playback
     * mode of the user if not specified.
     */
    fun play(song: Song, mode: PlaybackMode = settingsManager.songPlaybackMode) {
        playbackManager.play(song, mode)
    }

    /**
     * Play an [album].
     * @param shuffled Whether to shuffle the new queue
     */
    fun play(album: Album, shuffled: Boolean) {
        if (album.songs.isEmpty()) {
            logE("Album is empty, Not playing")
            return
        }

        playbackManager.play(album, shuffled)
    }

    /**
     * Play an [artist].
     * @param shuffled Whether to shuffle the new queue
     */
    fun play(artist: Artist, shuffled: Boolean) {
        if (artist.songs.isEmpty()) {
            logE("Artist is empty, Not playing")
            return
        }

        playbackManager.play(artist, shuffled)
    }

    /**
     * Play a [genre].
     * @param shuffled Whether to shuffle the new queue
     */
    fun play(genre: Genre, shuffled: Boolean) {
        if (genre.songs.isEmpty()) {
            logE("Genre is empty, Not playing")
            return
        }

        playbackManager.play(genre, shuffled)
    }

    /** Shuffle all songs */
    fun shuffleAll() {
        playbackManager.shuffleAll()
    }

    /**
     * Perform the given [DelayedAction].
     *
     * A "delayed action" is a class of playback actions that must have music present to function,
     * usually alongside a context too. Examples include:
     * - Opening files
     * - Restoring the playback state
     * - Future app shortcuts
     *
     * We would normally want to put this kind of functionality into PlaybackService, but it's
     * lifecycle makes that more or less impossible.
     */
    fun performAction(context: Context, action: DelayedAction) {
        val library = musicStore.library
        val actionImpl = DelayedActionImpl(context.applicationContext, action)
        if (library != null) {
            performActionImpl(actionImpl, library)
        } else {
            pendingDelayedAction = actionImpl
        }
    }

    private fun performActionImpl(action: DelayedActionImpl, library: MusicStore.Library) {
        when (action.inner) {
            is DelayedAction.RestoreState -> {
                if (!playbackManager.isInitialized) {
                    viewModelScope.launch { playbackManager.restoreState(action.context) }
                }
            }
            is DelayedAction.Open -> {
                library.findSongForUri(action.context, action.inner.uri)?.let(::play)
            }
        }
    }

    // --- POSITION FUNCTIONS ---

    /** Update the position and push it to [PlaybackStateManager] */
    fun seekTo(positionSecs: Long) {
        playbackManager.seekTo(positionSecs * 1000)
    }

    // --- QUEUE FUNCTIONS ---

    /** Skip to the next song. */
    fun next() {
        playbackManager.next()
    }

    /** Skip to the previous song. */
    fun prev() {
        playbackManager.prev()
    }

    /**
     * Remove a queue item using it's recyclerview adapter index. If the indices are valid, [apply]
     * is called just before the change is committed so that the adapter can be updated.
     */
    fun removeQueueDataItem(adapterIndex: Int, apply: () -> Unit) {
        val index =
            adapterIndex + (playbackManager.queue.size - unlikelyToBeNull(_nextUp.value).size)
        if (index in playbackManager.queue.indices) {
            apply()
            playbackManager.removeQueueItem(index)
        }
    }
    /**
     * Move queue items using their recyclerview adapter indices. If the indices are valid, [apply]
     * is called just before the change is committed so that the adapter can be updated.
     */
    fun moveQueueDataItems(adapterFrom: Int, adapterTo: Int, apply: () -> Unit): Boolean {
        val delta = (playbackManager.queue.size - unlikelyToBeNull(_nextUp.value).size)
        val from = adapterFrom + delta
        val to = adapterTo + delta
        if (from in playbackManager.queue.indices && to in playbackManager.queue.indices) {
            apply()
            playbackManager.moveQueueItem(from, to)
            return true
        }

        return false
    }

    /** Add a [Song] to the top of the queue. */
    fun playNext(song: Song) {
        playbackManager.playNext(song)
    }

    /** Add an [Album] to the top of the queue. */
    fun playNext(album: Album) {
        playbackManager.playNext(settingsManager.detailAlbumSort.songs(album.songs))
    }

    /** Add a [Song] to the end of the queue. */
    fun addToQueue(song: Song) {
        playbackManager.addToQueue(song)
    }

    /** Add an [Album] to the end of the queue. */
    fun addToQueue(album: Album) {
        playbackManager.addToQueue(settingsManager.detailAlbumSort.songs(album.songs))
    }

    // --- STATUS FUNCTIONS ---

    /** Flip the playing status, e.g from playing to paused */
    fun invertPlaying() {
        playbackManager.isPlaying = !playbackManager.isPlaying
    }

    /** Flip the shuffle status, e.g from on to off. Will keep song by default. */
    fun invertShuffled() {
        playbackManager.reshuffle(!playbackManager.isShuffled)
    }

    /** Increment the repeat mode, e.g from [RepeatMode.NONE] to [RepeatMode.ALL] */
    fun incrementRepeatMode() {
        playbackManager.repeatMode = playbackManager.repeatMode.increment()
    }

    // --- SAVE/RESTORE FUNCTIONS ---

    /**
     * Force save the current [PlaybackStateManager] state to the database. Called by
     * SettingsListFragment.
     */
    fun savePlaybackState(context: Context, onDone: () -> Unit) {
        viewModelScope.launch {
            playbackManager.saveState(context)
            onDone()
        }
    }

    /** An action delayed until the complete load of the music library. */
    sealed class DelayedAction {
        object RestoreState : DelayedAction()
        data class Open(val uri: Uri) : DelayedAction()
    }

    private data class DelayedActionImpl(val context: Context, val inner: DelayedAction)

    // --- OVERRIDES ---

    override fun onCleared() {
        musicStore.removeCallback(this)
        playbackManager.removeCallback(this)
        pendingDelayedAction = null
    }

    override fun onIndexMoved(index: Int) {
        _song.value = playbackManager.song
        _nextUp.value = playbackManager.queue.slice(index + 1 until playbackManager.queue.size)
    }

    override fun onQueueChanged(index: Int, queue: List<Song>) {
        _nextUp.value = queue.slice(index + 1 until queue.size)
    }

    override fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) {
        _song.value = playbackManager.song
        _parent.value = playbackManager.parent
        _nextUp.value = queue.slice(index + 1 until queue.size)
    }

    override fun onPositionChanged(positionMs: Long) {
        _positionSecs.value = positionMs / 1000
    }

    override fun onPlayingChanged(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
    }

    override fun onShuffledChanged(isShuffled: Boolean) {
        _isShuffled.value = isShuffled
    }

    override fun onRepeatChanged(repeatMode: RepeatMode) {
        _repeatMode.value = repeatMode
    }

    override fun onLibraryChanged(library: MusicStore.Library?) {
        if (library != null) {
            val action = pendingDelayedAction
            if (action != null) {
                performActionImpl(action, library)
                pendingDelayedAction = null
            }
        }
    }
}
