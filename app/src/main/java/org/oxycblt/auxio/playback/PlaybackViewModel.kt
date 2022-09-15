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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.dsToMs
import org.oxycblt.auxio.music.msToDs
import org.oxycblt.auxio.playback.state.InternalPlayer
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
    AndroidViewModel(application), PlaybackStateManager.Callback {
    private val settings = Settings(application)
    private val playbackManager = PlaybackStateManager.getInstance()

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
    private val _positionDs = MutableStateFlow(0L)

    /** The current playback position, in *deci-seconds* */
    val positionDs: StateFlow<Long>
        get() = _positionDs

    private val _repeatMode = MutableStateFlow(RepeatMode.NONE)

    /** The current repeat mode, see [RepeatMode] for more information */
    val repeatMode: StateFlow<RepeatMode>
        get() = _repeatMode
    private val _isShuffled = MutableStateFlow(false)
    val isShuffled: StateFlow<Boolean>
        get() = _isShuffled

    val currentAudioSessionId: Int?
        get() = playbackManager.currentAudioSessionId

    private var lastPositionJob: Job? = null

    init {
        playbackManager.addCallback(this)
    }

    // --- PLAYING FUNCTIONS ---

    /** Play a [song] from all songs. */
    fun play(song: Song) {
        playbackManager.play(song, null, settings)
    }

    /** Play a song from it's album. */
    fun playFromAlbum(song: Song) {
        playbackManager.play(song, song.album, settings)
    }

    /** Play a song from it's artist. */
    fun playFromArtist(song: Song) {
        playbackManager.play(song, song.album.artist, settings)
    }

    /** Play a song from the specific genre that contains the song. */
    fun playFromGenre(song: Song, genre: Genre) {
        if (!genre.songs.contains(song)) {
            logE("Genre does not contain song, not playing")
            return
        }

        playbackManager.play(song, genre, settings)
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
     * Perform the given [InternalPlayer.Action].
     *
     * These are a class of playback actions that must have music present to function, usually
     * alongside a context too. Examples include:
     * - Opening files
     * - Restoring the playback state
     * - App shortcuts
     */
    fun startAction(action: InternalPlayer.Action) {
        playbackManager.startAction(action)
    }

    // --- PLAYER FUNCTIONS ---

    /** Update the position and push it to [PlaybackStateManager] */
    fun seekTo(positionDs: Long) {
        playbackManager.seekTo(positionDs.dsToMs())
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
        playbackManager.changePlaying(!playbackManager.playerState.isPlaying)
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
                playbackManager.restoreState(PlaybackStateDatabase.getInstance(application), true)
            onDone(restored)
        }
    }

    // --- OVERRIDES ---

    override fun onCleared() {
        playbackManager.removeCallback(this)
    }

    override fun onIndexMoved(index: Int) {
        _song.value = playbackManager.song
    }

    override fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) {
        _song.value = playbackManager.song
        _parent.value = playbackManager.parent
    }

    override fun onStateChanged(state: InternalPlayer.State) {
        _isPlaying.value = state.isPlaying
        _positionDs.value = state.calculateElapsedPosition().msToDs()

        // Start watching the position again
        lastPositionJob?.cancel()
        lastPositionJob =
            viewModelScope.launch {
                while (true) {
                    _positionDs.value = state.calculateElapsedPosition().msToDs()
                    delay(100)
                }
            }
    }

    override fun onShuffledChanged(isShuffled: Boolean) {
        _isShuffled.value = isShuffled
    }

    override fun onRepeatChanged(repeatMode: RepeatMode) {
        _repeatMode.value = repeatMode
    }
}
