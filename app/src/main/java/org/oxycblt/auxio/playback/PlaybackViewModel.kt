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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.playback.persist.PersistenceRepository
import org.oxycblt.auxio.playback.queue.Queue
import org.oxycblt.auxio.playback.state.*

/**
 * An [ViewModel] that provides a safe UI frontend for the current playback state.
 * @author Alexander Capehart (OxygenCobalt)
 */
@HiltViewModel
class PlaybackViewModel
@Inject
constructor(
    private val playbackManager: PlaybackStateManager,
    private val playbackSettings: PlaybackSettings,
    private val persistenceRepository: PersistenceRepository,
    private val musicRepository: MusicRepository,
    private val musicSettings: MusicSettings
) : ViewModel(), PlaybackStateManager.Listener {
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

    /** The current action to show on the playback bar. */
    val currentBarAction: ActionMode
        get() = playbackSettings.barAction

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

    override fun onIndexMoved(queue: Queue) {
        _song.value = queue.currentSong
    }

    override fun onQueueChanged(queue: Queue, change: Queue.ChangeResult) {
        // Other types of queue changes preserve the current song.
        if (change == Queue.ChangeResult.SONG) {
            _song.value = queue.currentSong
        }
    }

    override fun onQueueReordered(queue: Queue) {
        _isShuffled.value = queue.isShuffled
    }

    override fun onNewPlayback(queue: Queue, parent: MusicParent?) {
        _song.value = queue.currentSong
        _parent.value = parent
        _isShuffled.value = queue.isShuffled
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

    override fun onRepeatChanged(repeatMode: RepeatMode) {
        _repeatMode.value = repeatMode
    }

    // --- PLAYING FUNCTIONS ---

    /** Shuffle all songs in the music library. */
    fun shuffleAll() {
        playImpl(null, null, true)
    }

    /**
     * Play a [Song] from the [MusicParent] outlined by the given [MusicMode].
     * - If [MusicMode.SONGS], the [Song] is played from all songs.
     * - If [MusicMode.ALBUMS], the [Song] is played from it's [Album].
     * - If [MusicMode.ARTISTS], the [Song] is played from one of it's [Artist]s.
     * - If [MusicMode.GENRES], the [Song] is played from one of it's [Genre]s.
     * @param song The [Song] to play.
     * @param playbackMode The [MusicMode] to play from.
     */
    fun playFrom(song: Song, playbackMode: MusicMode) {
        when (playbackMode) {
            MusicMode.SONGS -> playImpl(song, null)
            MusicMode.ALBUMS -> playImpl(song, song.album)
            MusicMode.ARTISTS -> playFromArtist(song)
            MusicMode.GENRES -> playFromGenre(song)
        }
    }

    /**
     * Play a [Song] from one of it's [Artist]s.
     * @param song The [Song] to play.
     * @param artist The [Artist] to play from. Must be linked to the [Song]. If null, the user will
     * be prompted on what artist to play. Defaults to null.
     */
    fun playFromArtist(song: Song, artist: Artist? = null) {
        if (artist != null) {
            playImpl(song, artist)
        } else if (song.artists.size == 1) {
            playImpl(song, song.artists[0])
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
            playImpl(song, genre)
        } else if (song.genres.size == 1) {
            playImpl(song, song.genres[0])
        } else {
            _genrePlaybackPickerSong.value = song
        }
    }

    /**
     * Mark the [Genre] playback choice process as complete. This should occur when the [Genre]
     * choice dialog is opened after this flag is detected.
     * @see playFromGenre
     */
    fun finishPlaybackGenrePicker() {
        _genrePlaybackPickerSong.value = null
    }

    /**
     * Play an [Album].
     * @param album The [Album] to play.
     */
    fun play(album: Album) = playImpl(null, album, false)

    /**
     * Play an [Artist].
     * @param artist The [Artist] to play.
     */
    fun play(artist: Artist) = playImpl(null, artist, false)

    /**
     * Play a [Genre].
     * @param genre The [Genre] to play.
     */
    fun play(genre: Genre) = playImpl(null, genre, false)

    /**
     * Play a [Music] selection.
     * @param selection The selection to play.
     */
    fun play(selection: List<Music>) =
        playbackManager.play(null, null, selectionToSongs(selection), false)

    /**
     * Shuffle an [Album].
     * @param album The [Album] to shuffle.
     */
    fun shuffle(album: Album) = playImpl(null, album, true)

    /**
     * Shuffle an [Artist].
     * @param artist The [Artist] to shuffle.
     */
    fun shuffle(artist: Artist) = playImpl(null, artist, true)

    /**
     * Shuffle an [Genre].
     * @param genre The [Genre] to shuffle.
     */
    fun shuffle(genre: Genre) = playImpl(null, genre, true)

    /**
     * Shuffle a [Music] selection.
     * @param selection The selection to shuffle.
     */
    fun shuffle(selection: List<Music>) =
        playbackManager.play(null, null, selectionToSongs(selection), true)

    private fun playImpl(
        song: Song?,
        parent: MusicParent?,
        shuffled: Boolean = playbackManager.queue.isShuffled && playbackSettings.keepShuffle
    ) {
        check(song == null || parent == null || parent.songs.contains(song)) {
            "Song to play not in parent"
        }
        val library = musicRepository.library ?: return
        val sort =
            when (parent) {
                is Genre -> musicSettings.genreSongSort
                is Artist -> musicSettings.artistSongSort
                is Album -> musicSettings.albumSongSort
                null -> musicSettings.songSort
            }
        val queue = sort.songs(parent?.songs ?: library.songs)
        playbackManager.play(song, parent, queue, shuffled)
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
        playbackManager.playNext(song)
    }

    /**
     * Add a [Album] to the top of the queue.
     * @param album The [Album] to add.
     */
    fun playNext(album: Album) {
        playbackManager.playNext(musicSettings.albumSongSort.songs(album.songs))
    }

    /**
     * Add a [Artist] to the top of the queue.
     * @param artist The [Artist] to add.
     */
    fun playNext(artist: Artist) {
        playbackManager.playNext(musicSettings.artistSongSort.songs(artist.songs))
    }

    /**
     * Add a [Genre] to the top of the queue.
     * @param genre The [Genre] to add.
     */
    fun playNext(genre: Genre) {
        playbackManager.playNext(musicSettings.genreSongSort.songs(genre.songs))
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
        playbackManager.addToQueue(musicSettings.albumSongSort.songs(album.songs))
    }

    /**
     * Add a [Artist] to the end of the queue.
     * @param artist The [Artist] to add.
     */
    fun addToQueue(artist: Artist) {
        playbackManager.addToQueue(musicSettings.artistSongSort.songs(artist.songs))
    }

    /**
     * Add a [Genre] to the end of the queue.
     * @param genre The [Genre] to add.
     */
    fun addToQueue(genre: Genre) {
        playbackManager.addToQueue(musicSettings.genreSongSort.songs(genre.songs))
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
    fun togglePlaying() {
        playbackManager.setPlaying(!playbackManager.playerState.isPlaying)
    }

    /** Toggle [isShuffled] (ex. from on to off) */
    fun toggleShuffled() {
        playbackManager.reorder(!playbackManager.queue.isShuffled)
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
            onDone(persistenceRepository.saveState(playbackManager.toSavedState()))
        }
    }

    /**
     * Clear the current playback state.
     * @param onDone Called when the wipe is completed with true if successful, and false otherwise.
     */
    fun wipePlaybackState(onDone: (Boolean) -> Unit) {
        viewModelScope.launch { onDone(persistenceRepository.saveState(null)) }
    }

    /**
     * Force-restore the current playback state.
     * @param onDone Called when the restoration is completed with true if successful, and false
     * otherwise.
     */
    fun tryRestorePlaybackState(onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val library = musicRepository.library
            if (library != null) {
                val savedState = persistenceRepository.readState(library)
                if (savedState != null) {
                    playbackManager.applySavedState(savedState, true)
                    onDone(true)
                    return@launch
                }
            }
            onDone(false)
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
                is Album -> musicSettings.albumSongSort.songs(it.songs)
                is Artist -> musicSettings.artistSongSort.songs(it.songs)
                is Genre -> musicSettings.genreSongSort.songs(it.songs)
                is Song -> listOf(it)
            }
        }
    }
}
