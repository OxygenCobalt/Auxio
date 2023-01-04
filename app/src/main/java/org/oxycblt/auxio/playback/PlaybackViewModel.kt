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
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.playback.state.InternalPlayer
import org.oxycblt.auxio.playback.state.PlaybackStateDatabase
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.context

/**
 * An [AndroidViewModel] that provides a safe UI frontend for the current playback state.
 * @author Alexander Capehart (OxygenCobalt)
 */
class PlaybackViewModel(application: Application) :
    AndroidViewModel(application), PlaybackStateManager.Listener {
    private val settings = Settings(application)
    private val playbackManager = PlaybackStateManager.getInstance()
    private var lastPositionJob: Job? = null

    private val _song = MutableStateFlow<Song?>(null)
    /** The currently playing song. */
    val song: StateFlow<Song?>
        get() = _song
    private val _parent = MutableStateFlow<MusicParent?>(null)
    /** The [MusicParent] currently being played. Null if playback is occurring from all songs. */
    val parent: StateFlow<MusicParent?> = _parent
    private val _isPlaying = MutableStateFlow(false)
    /** Whether playback is ongoing or paused. */
    val isPlaying: StateFlow<Boolean>
        get() = _isPlaying
    private val _positionDs = MutableStateFlow(0L)
    /** The current position, in deci-seconds (1/10th of a second). */
    val positionDs: StateFlow<Long>
        get() = _positionDs

    private val _repeatMode = MutableStateFlow(RepeatMode.NONE)
    /** The current [RepeatMode]. */
    val repeatMode: StateFlow<RepeatMode>
        get() = _repeatMode
    private val _isShuffled = MutableStateFlow(false)
    /** Whether the queue is shuffled or not. */
    val isShuffled: StateFlow<Boolean>
        get() = _isShuffled

    private val _artistPlaybackPickerSong = MutableStateFlow<Song?>(null)
    /**
     * Flag signaling to open a picker dialog in order to resolve an ambiguous choice when playing a
     * [Song] from one of it's [Artist]s.
     * @see playFromArtist
     */
    val artistPickerSong: StateFlow<Song?>
        get() = _artistPlaybackPickerSong

    private val _genrePlaybackPickerSong = MutableStateFlow<Song?>(null)
    /**
     * Flag signaling to open a picker dialog in order to resolve an ambiguous choice when playing a
     * [Song] from one of it's [Genre]s.
     */
    val genrePickerSong: StateFlow<Song?>
        get() = _genrePlaybackPickerSong

    /**
     * The current audio session ID of the internal player. Null if no [InternalPlayer] is
     * available.
     */
    val currentAudioSessionId: Int?
        get() = playbackManager.currentAudioSessionId

    init {
        playbackManager.addListener(this)
    }

    override fun onCleared() {
        playbackManager.removeListener(this)
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
        // Still need to update the position now due to co-routine launch delays
        _positionDs.value = state.calculateElapsedPositionMs().msToDs()
        // Replace the previous position co-routine with a new one that uses the new
        // state information.
        lastPositionJob?.cancel()
        lastPositionJob =
            viewModelScope.launch {
                while (true) {
                    _positionDs.value = state.calculateElapsedPositionMs().msToDs()
                    // Wait a deci-second for the next position tick.
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

    // --- PLAYING FUNCTIONS ---

    /**
     * Play the given [Song] from all songs in the music library.
     * @param song The [Song] to play.
     */
    fun playFromAll(song: Song) {
        playbackManager.play(song, null, settings)
    }

    /** Shuffle all songs in the music library. */
    fun shuffleAll() {
        playbackManager.play(null, null, settings, true)
    }

    /**
     * Play a [Song] from it's [Album].
     * @param song The [Song] to play.
     */
    fun playFromAlbum(song: Song) {
        playbackManager.play(song, song.album, settings)
    }

    /**
     * Play a [Song] from one of it's [Artist]s.
     * @param song The [Song] to play.
     * @param artist The [Artist] to play from. Must be linked to the [Song]. If null, the user will
     * be prompted on what artist to play. Defaults to null.
     */
    fun playFromArtist(song: Song, artist: Artist? = null) {
        if (artist != null) {
            check(artist in song.artists) { "Artist not in song artists" }
            playbackManager.play(song, artist, settings)
        } else if (song.artists.size == 1) {
            playbackManager.play(song, song.artists[0], settings)
        } else {
            _artistPlaybackPickerSong.value = song
        }
    }

    /**
     * Mark the [Artist] playback choice process as complete. This should occur when the [Artist]
     * choice dialog is opened after this flag is detected.
     * @see playFromArtist
     */
    fun finishPlaybackArtistPicker() {
        _artistPlaybackPickerSong.value = null
    }

    /**
     * PLay a [Song] from one of it's [Genre]s.
     * @param song The [Song] to play.
     * @param genre The [Genre] to play from. Must be linked to the [Song]. If null, the user will
     * be prompted on what artist to play. Defaults to null.
     */
    fun playFromGenre(song: Song, genre: Genre? = null) {
        if (genre != null) {
            check(genre.songs.contains(song)) { "Invalid input: Genre is not linked to song" }
            playbackManager.play(song, genre, settings)
        } else if (song.genres.size == 1) {
            playbackManager.play(song, song.genres[0], settings)
        } else {
            _genrePlaybackPickerSong.value = song
        }
    }

    /**
     * Play an [Album].
     * @param album The [Album] to play.
     */
    fun play(album: Album) {
        playbackManager.play(null, album, settings, false)
    }

    /**
     * Play an [Artist].
     * @param artist The [Artist] to play.
     */
    fun play(artist: Artist) {
        playbackManager.play(null, artist, settings, false)
    }

    /**
     * Play a [Genre].
     * @param genre The [Genre] to play.
     */
    fun play(genre: Genre) {
        playbackManager.play(null, genre, settings, false)
    }

    /**
     * Shuffle an [Album].
     * @param album The [Album] to shuffle.
     */
    fun shuffle(album: Album) {
        playbackManager.play(null, album, settings, true)
    }

    /**
     * Shuffle an [Artist].
     * @param artist The [Artist] to shuffle.
     */
    fun shuffle(artist: Artist) {
        playbackManager.play(null, artist, settings, true)
    }

    /**
     * Shuffle an [Genre].
     * @param genre The [Genre] to shuffle.
     */
    fun shuffle(genre: Genre) {
        playbackManager.play(null, genre, settings, true)
    }

    /**
     * Start the given [InternalPlayer.Action] to be completed eventually. This can be used to
     * enqueue a playback action at startup to then occur when the music library is fully loaded.
     * @param action The [InternalPlayer.Action] to perform eventually.
     */
    fun startAction(action: InternalPlayer.Action) {
        playbackManager.startAction(action)
    }

    // --- PLAYER FUNCTIONS ---

    /**
     * Seek to the given position in the currently playing [Song].
     * @param positionDs The position to seek to, in deci-seconds (1/10th of a second).
     */
    fun seekTo(positionDs: Long) {
        playbackManager.seekTo(positionDs.dsToMs())
    }

    // --- QUEUE FUNCTIONS ---

    /** Skip to the next [Song]. */
    fun next() {
        playbackManager.next()
    }

    /** Skip to the previous [Song]. */
    fun prev() {
        playbackManager.prev()
    }

    /**
     * Add a [Song] to the top of the queue.
     * @param song The [Song] to add.
     */
    fun playNext(song: Song) {
        // TODO: Queue additions without a playing song should map to playing items
        //  (impossible until queue rework)
        playbackManager.playNext(song)
    }

    /**
     * Add a [Album] to the top of the queue.
     * @param album The [Album] to add.
     */
    fun playNext(album: Album) {
        playbackManager.playNext(settings.detailAlbumSort.songs(album.songs))
    }

    /**
     * Add a [Artist] to the top of the queue.
     * @param artist The [Artist] to add.
     */
    fun playNext(artist: Artist) {
        playbackManager.playNext(settings.detailArtistSort.songs(artist.songs))
    }

    /**
     * Add a [Genre] to the top of the queue.
     * @param genre The [Genre] to add.
     */
    fun playNext(genre: Genre) {
        playbackManager.playNext(settings.detailGenreSort.songs(genre.songs))
    }

    /**
     * Add a selection to the top of the queue.
     * @param selection The [Music] selection to add.
     */
    fun playNext(selection: List<Music>) {
        playbackManager.playNext(selectionToSongs(selection))
    }

    /**
     * Add a [Song] to the end of the queue.
     * @param song The [Song] to add.
     */
    fun addToQueue(song: Song) {
        playbackManager.addToQueue(song)
    }

    /**
     * Add a [Album] to the end of the queue.
     * @param album The [Album] to add.
     */
    fun addToQueue(album: Album) {
        playbackManager.addToQueue(settings.detailAlbumSort.songs(album.songs))
    }

    /**
     * Add a [Artist] to the end of the queue.
     * @param artist The [Artist] to add.
     */
    fun addToQueue(artist: Artist) {
        playbackManager.addToQueue(settings.detailArtistSort.songs(artist.songs))
    }

    /**
     * Add a [Genre] to the end of the queue.
     * @param genre The [Genre] to add.
     */
    fun addToQueue(genre: Genre) {
        playbackManager.addToQueue(settings.detailGenreSort.songs(genre.songs))
    }

    /**
     * Add a selection to the end of the queue.
     * @param selection The [Music] selection to add.
     */
    fun addToQueue(selection: List<Music>) {
        playbackManager.addToQueue(selectionToSongs(selection))
    }

    // --- STATUS FUNCTIONS ---

    /** Toggle [isPlaying] (i.e from playing to paused) */
    fun toggleIsPlaying() {
        playbackManager.setPlaying(!playbackManager.playerState.isPlaying)
    }

    /** Toggle [isShuffled] (ex. from on to off) */
    fun invertShuffled() {
        playbackManager.reshuffle(!playbackManager.isShuffled, settings)
    }

    /**
     * Toggle [repeatMode] (ex. from [RepeatMode.NONE] to [RepeatMode.TRACK])
     * @see RepeatMode.increment
     */
    fun toggleRepeatMode() {
        playbackManager.repeatMode = playbackManager.repeatMode.increment()
    }

    // --- SAVE/RESTORE FUNCTIONS ---

    /**
     * Force-save the current playback state.
     * @param onDone Called when the save is completed with true if successful, and false otherwise.
     */
    fun savePlaybackState(onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val saved = playbackManager.saveState(PlaybackStateDatabase.getInstance(context))
            onDone(saved)
        }
    }

    /**
     * Clear the current playback state.
     * @param onDone Called when the wipe is completed with true if successful, and false otherwise.
     */
    fun wipePlaybackState(onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val wiped = playbackManager.wipeState(PlaybackStateDatabase.getInstance(context))
            onDone(wiped)
        }
    }

    /**
     * Force-restore the current playback state.
     * @param onDone Called when the restoration is completed with true if successful, and false
     * otherwise.
     */
    fun tryRestorePlaybackState(onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val restored =
                playbackManager.restoreState(PlaybackStateDatabase.getInstance(context), true)
            onDone(restored)
        }
    }

    /**
     * Convert the given selection to a list of [Song]s.
     * @param selection The selection of [Music] to convert.
     * @return A [Song] list containing the child items of any [MusicParent] instances in the list
     * alongside the unchanged [Song]s or the original selection.
     */
    private fun selectionToSongs(selection: List<Music>): List<Song> {
        return selection.flatMap {
            when (it) {
                is Album -> settings.detailAlbumSort.songs(it.songs)
                is Artist -> settings.detailArtistSort.songs(it.songs)
                is Genre -> settings.detailGenreSort.songs(it.songs)
                is Song -> listOf(it)
            }
        }
    }
}
