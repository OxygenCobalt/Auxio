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

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
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
import org.oxycblt.auxio.playback.state.PlaybackStateDatabase
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.application
import org.oxycblt.auxio.util.logE

/**
 * The ViewModel that provides a UI frontend for [PlaybackStateManager].
 *
 * **PLEASE Use this instead of [PlaybackStateManager], UIs are extremely volatile and this provides
 * an interface that properly sanitizes input and abstracts functions unlike the master class.**
 *
 * @author OxygenCobalt
 */
class PlaybackViewModel(application: Application) :
    AndroidViewModel(application), PlaybackStateManager.Callback, MusicStore.Callback {
    private val musicStore = MusicStore.getInstance()
    private val settings = Settings(application)
    private val playbackManager = PlaybackStateManager.getInstance()

    private var pendingDelayedAction: DelayedAction? = null

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

    init {
        musicStore.addCallback(this)
        playbackManager.addCallback(this)
    }

    // --- PLAYING FUNCTIONS ---

    /** Play a [song] with the [mode] specified, */
    fun play(song: Song, mode: PlaybackMode) {
        playbackManager.play(song, mode, settings)
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

        playbackManager.play(album, shuffled, settings)
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

        playbackManager.play(artist, shuffled, settings)
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

        playbackManager.play(genre, shuffled, settings)
    }

    /** Shuffle all songs */
    fun shuffleAll() {
        playbackManager.shuffleAll(settings)
    }

    /**
     * Perform the given [DelayedAction].
     *
     * A "delayed action" is a class of playback actions that must have music present to function,
     * usually alongside a context too. Examples include:
     * - Opening files
     * - Restoring the playback state
     * - App shortcuts
     *
     * We would normally want to put this kind of functionality into PlaybackService, but it's
     * lifecycle makes that more or less impossible.
     */
    fun startDelayedAction(action: DelayedAction) {
        val library = musicStore.library
        if (library != null) {
            performActionImpl(action, library)
        } else {
            pendingDelayedAction = action
        }
    }

    private fun performActionImpl(action: DelayedAction, library: MusicStore.Library) {
        when (action) {
            is DelayedAction.RestoreState -> {
                if (!playbackManager.isInitialized) {
                    viewModelScope.launch {
                        playbackManager.restoreState(PlaybackStateDatabase.getInstance(application))
                    }
                }
            }
            is DelayedAction.ShuffleAll -> shuffleAll()
            is DelayedAction.Open -> {
                library.findSongForUri(application, action.uri)?.let { song ->
                    play(song, settings.libPlaybackMode)
                }
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

    /** Add a [Song] to the top of the queue. */
    fun playNext(song: Song) {
        playbackManager.playNext(song)
    }

    /** Add an [Album] to the top of the queue. */
    fun playNext(album: Album) {
        playbackManager.playNext(settings.detailAlbumSort.songs(album.songs))
    }

    /** Add an [Artist] to the top of the queue. */
    fun playNext(artist: Artist) {
        playbackManager.playNext(settings.detailArtistSort.songs(artist.songs))
    }

    /** Add a [Genre] to the top of the queue. */
    fun playNext(genre: Genre) {
        playbackManager.playNext(settings.detailGenreSort.songs(genre.songs))
    }

    /** Add a [Song] to the end of the queue. */
    fun addToQueue(song: Song) {
        playbackManager.addToQueue(song)
    }

    /** Add an [Album] to the end of the queue. */
    fun addToQueue(album: Album) {
        playbackManager.addToQueue(settings.detailAlbumSort.songs(album.songs))
    }

    /** Add an [Artist] to the end of the queue. */
    fun addToQueue(artist: Artist) {
        playbackManager.addToQueue(settings.detailArtistSort.songs(artist.songs))
    }

    /** Add a [Genre] to the end of the queue. */
    fun addToQueue(genre: Genre) {
        playbackManager.addToQueue(settings.detailGenreSort.songs(genre.songs))
    }

    // --- STATUS FUNCTIONS ---

    /** Flip the playing status, e.g from playing to paused */
    fun invertPlaying() {
        playbackManager.isPlaying = !playbackManager.isPlaying
    }

    /** Flip the shuffle status, e.g from on to off. Will keep song by default. */
    fun invertShuffled() {
        playbackManager.reshuffle(!playbackManager.isShuffled, settings)
    }

    /** Increment the repeat mode, e.g from [RepeatMode.NONE] to [RepeatMode.ALL] */
    fun incrementRepeatMode() {
        playbackManager.repeatMode = playbackManager.repeatMode.increment()
    }

    // --- SAVE/RESTORE FUNCTIONS ---

    /** Force save the current [PlaybackStateManager] state to the database. */
    fun savePlaybackState(onDone: () -> Unit) {
        viewModelScope.launch {
            playbackManager.saveState(PlaybackStateDatabase.getInstance(application))
            onDone()
        }
    }

    /** Wipe the saved playback state (if any). */
    fun wipePlaybackState(onDone: () -> Unit) {
        viewModelScope.launch {
            playbackManager.wipeState(PlaybackStateDatabase.getInstance(application))
            onDone()
        }
    }

    /**
     * Force restore the last [PlaybackStateManager] saved state, regardless of if a library exists
     * or not. [onDone] will be called with true if it was successfully done, or false if there was
     * no state or if a library was not present.
     */
    fun tryRestorePlaybackState(onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val restored =
                playbackManager.restoreState(PlaybackStateDatabase.getInstance(application))
            onDone(restored)
        }
    }

    /** An action delayed until the complete load of the music library. */
    sealed class DelayedAction {
        object RestoreState : DelayedAction()
        object ShuffleAll : DelayedAction()
        data class Open(val uri: Uri) : DelayedAction()
    }

    // --- OVERRIDES ---

    override fun onCleared() {
        musicStore.removeCallback(this)
        playbackManager.removeCallback(this)
        pendingDelayedAction = null
    }

    override fun onIndexMoved(index: Int) {
        _song.value = playbackManager.song
    }

    override fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) {
        _song.value = playbackManager.song
        _parent.value = playbackManager.parent
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
