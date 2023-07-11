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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.persist.PersistenceRepository
import org.oxycblt.auxio.playback.queue.Queue
import org.oxycblt.auxio.playback.state.InternalPlayer
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.util.Event
import org.oxycblt.auxio.util.MutableEvent
import org.oxycblt.auxio.util.logD

/**
 * An [ViewModel] that provides a safe UI frontend for the current playback state.
 *
 * @author Alexander Capehart (OxygenCobalt)
 *
 * TODO: Debug subtle backwards movement of position on pause
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
) : ViewModel(), PlaybackStateManager.Listener, PlaybackSettings.Listener {
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

    private val _currentBarAction = MutableStateFlow(playbackSettings.barAction)
    /** The current secondary action to show alongside the play button in the playback bar. */
    val currentBarAction: StateFlow<ActionMode>
        get() = _currentBarAction

    private val _openPanel = MutableEvent<OpenPanel>()
    /**
     * A [OpenPanel] command that is awaiting a view capable of responding to it. Null if none
     * currently.
     */
    val openPanel: Event<OpenPanel>
        get() = _openPanel

    private val _playbackDecision = MutableEvent<PlaybackDecision>()
    /**
     * A [PlaybackDecision] command that is awaiting a view capable of responding to it. Null if
     * none currently.
     */
    val playbackDecision: Event<PlaybackDecision>
        get() = _playbackDecision

    /**
     * The current audio session ID of the internal player. Null if no [InternalPlayer] is
     * available.
     */
    val currentAudioSessionId: Int?
        get() = playbackManager.currentAudioSessionId

    init {
        playbackManager.addListener(this)
        playbackSettings.registerListener(this)
    }

    override fun onCleared() {
        playbackManager.removeListener(this)
        playbackSettings.unregisterListener(this)
    }

    override fun onIndexMoved(queue: Queue) {
        logD("Index moved, updating current song")
        _song.value = queue.currentSong
    }

    override fun onQueueChanged(queue: Queue, change: Queue.Change) {
        // Other types of queue changes preserve the current song.
        if (change.type == Queue.Change.Type.SONG) {
            logD("Queue changed, updating current song")
            _song.value = queue.currentSong
        }
    }

    override fun onQueueReordered(queue: Queue) {
        logD("Queue completely changed, updating current song")
        _isShuffled.value = queue.isShuffled
    }

    override fun onNewPlayback(queue: Queue, parent: MusicParent?) {
        logD("New playback started, updating playback information")
        _song.value = queue.currentSong
        _parent.value = parent
        _isShuffled.value = queue.isShuffled
    }

    override fun onStateChanged(state: InternalPlayer.State) {
        logD("Player state changed, starting new position polling")
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

    override fun onBarActionChanged() {
        _currentBarAction.value = playbackSettings.barAction
    }

    // --- PLAYING FUNCTIONS ---

    fun play(song: Song, with: PlaySong) {
        logD("Playing $song with $with")
        playWithImpl(song, with, isImplicitlyShuffled())
    }

    fun playExplicit(song: Song, with: PlaySong) {
        playWithImpl(song, with, false)
    }

    fun shuffleExplicit(song: Song, with: PlaySong) {
        playWithImpl(song, with, true)
    }

    /** Shuffle all songs in the music library. */
    fun shuffleAll() {
        logD("Shuffling all songs")
        playFromAllImpl(null, true)
    }

    /**
     * Play a [Song] from one of it's [Artist]s.
     *
     * @param song The [Song] to play.
     * @param artist The [Artist] to play from. Must be linked to the [Song]. If null, the user will
     *   be prompted on what artist to play. Defaults to null.
     */
    fun playFromArtist(song: Song, artist: Artist? = null) {
        playFromArtistImpl(song, artist, isImplicitlyShuffled())
    }

    /**
     * Play a [Song] from one of it's [Genre]s.
     *
     * @param song The [Song] to play.
     * @param genre The [Genre] to play from. Must be linked to the [Song]. If null, the user will
     *   be prompted on what artist to play. Defaults to null.
     */
    fun playFromGenre(song: Song, genre: Genre? = null) {
        playFromGenreImpl(song, genre, isImplicitlyShuffled())
    }

    private fun isImplicitlyShuffled() =
        playbackManager.queue.isShuffled && playbackSettings.keepShuffle

    private fun playWithImpl(song: Song, with: PlaySong, shuffled: Boolean) {
        when (with) {
            is PlaySong.FromAll -> playFromAllImpl(song, shuffled)
            is PlaySong.FromAlbum -> playFromAlbumImpl(song, shuffled)
            is PlaySong.FromArtist -> playFromArtistImpl(song, with.which, shuffled)
            is PlaySong.FromGenre -> playFromGenreImpl(song, with.which, shuffled)
            is PlaySong.FromPlaylist -> playFromPlaylistImpl(song, with.which, shuffled)
            is PlaySong.ByItself -> playItselfImpl(song, shuffled)
        }
    }

    private fun playFromAllImpl(song: Song?, shuffled: Boolean) {
        playImpl(song, null, shuffled)
    }

    private fun playFromAlbumImpl(song: Song, shuffled: Boolean) {
        playImpl(song, song.album, shuffled)
    }

    private fun playFromArtistImpl(song: Song, artist: Artist?, shuffled: Boolean) {
        if (artist != null) {
            logD("Playing $song from $artist")
            playImpl(song, artist, shuffled)
        } else if (song.artists.size == 1) {
            logD("$song has one artist, playing from it")
            playImpl(song, song.artists[0], shuffled)
        } else {
            logD("$song has multiple artists, showing choice dialog")
            startPlaybackDecision(PlaybackDecision.PlayFromArtist(song))
        }
    }

    private fun playFromGenreImpl(song: Song, genre: Genre?, shuffled: Boolean) {
        if (genre != null) {
            logD("Playing $song from $genre")
            playImpl(song, genre, shuffled)
        } else if (song.genres.size == 1) {
            logD("$song has one genre, playing from it")
            playImpl(song, song.genres[0], shuffled)
        } else {
            logD("$song has multiple genres, showing choice dialog")
            startPlaybackDecision(PlaybackDecision.PlayFromGenre(song))
        }
    }

    private fun playFromPlaylistImpl(song: Song, playlist: Playlist, shuffled: Boolean) {
        logD("Playing $song from $playlist")
        playImpl(song, playlist, shuffled)
    }

    private fun playItselfImpl(song: Song, shuffled: Boolean) {
        playImpl(song, listOf(song), shuffled)
    }

    private fun startPlaybackDecision(decision: PlaybackDecision) {
        val existing = _playbackDecision.flow.value
        if (existing != null) {
            logD("Already handling decision $existing, ignoring $decision")
            return
        }
        _playbackDecision.put(decision)
    }

    /**
     * Play an [Album].
     *
     * @param album The [Album] to play.
     */
    fun play(album: Album) {
        logD("Playing $album")
        playImpl(null, album, false)
    }

    /**
     * Shuffle an [Album].
     *
     * @param album The [Album] to shuffle.
     */
    fun shuffle(album: Album) {
        logD("Shuffling $album")
        playImpl(null, album, true)
    }

    /**
     * Play an [Artist].
     *
     * @param artist The [Artist] to play.
     */
    fun play(artist: Artist) {
        logD("Playing $artist")
        playImpl(null, artist, false)
    }

    /**
     * Shuffle an [Artist].
     *
     * @param artist The [Artist] to shuffle.
     */
    fun shuffle(artist: Artist) {
        logD("Shuffling $artist")
        playImpl(null, artist, true)
    }

    /**
     * Play a [Genre].
     *
     * @param genre The [Genre] to play.
     */
    fun play(genre: Genre) {
        logD("Playing $genre")
        playImpl(null, genre, false)
    }

    /**
     * Shuffle a [Genre].
     *
     * @param genre The [Genre] to shuffle.
     */
    fun shuffle(genre: Genre) {
        logD("Shuffling $genre")
        playImpl(null, genre, true)
    }

    /**
     * Play a [Playlist].
     *
     * @param playlist The [Playlist] to play.
     */
    fun play(playlist: Playlist) {
        logD("Playing $playlist")
        playImpl(null, playlist, false)
    }

    /**
     * Shuffle a [Playlist].
     *
     * @param playlist The [Playlist] to shuffle.
     */
    fun shuffle(playlist: Playlist) {
        logD("Shuffling $playlist")
        playImpl(null, playlist, true)
    }

    /**
     * Play a list of [Song]s.
     *
     * @param songs The [Song]s to play.
     */
    fun play(songs: List<Song>) {
        logD("Playing ${songs.size} songs")
        playbackManager.play(null, null, songs, false)
    }

    /**
     * Shuffle a list of [Song]s.
     *
     * @param songs The [Song]s to shuffle.
     */
    fun shuffle(songs: List<Song>) {
        logD("Shuffling ${songs.size} songs")
        playbackManager.play(null, null, songs, true)
    }

    private fun playImpl(song: Song?, queue: List<Song>, shuffled: Boolean) {
        check(song == null || queue.contains(song)) { "Song to play not in queue" }
        playbackManager.play(song, null, queue, shuffled)
    }

    private fun playImpl(song: Song?, parent: MusicParent?, shuffled: Boolean) {
        check(song == null || parent == null || parent.songs.contains(song)) {
            "Song to play not in parent"
        }
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        val queue =
            when (parent) {
                is Genre -> musicSettings.genreSongSort.songs(parent.songs)
                is Artist -> musicSettings.artistSongSort.songs(parent.songs)
                is Album -> musicSettings.albumSongSort.songs(parent.songs)
                is Playlist -> parent.songs
                null -> musicSettings.songSort.songs(deviceLibrary.songs)
            }
        playbackManager.play(song, parent, queue, shuffled)
    }

    /**
     * Start the given [InternalPlayer.Action] to be completed eventually. This can be used to
     * enqueue a playback action at startup to then occur when the music library is fully loaded.
     *
     * @param action The [InternalPlayer.Action] to perform eventually.
     */
    fun startAction(action: InternalPlayer.Action) {
        logD("Starting action $action")
        playbackManager.startAction(action)
    }

    // --- PLAYER FUNCTIONS ---

    /**
     * Seek to the given position in the currently playing [Song].
     *
     * @param positionDs The position to seek to, in deci-seconds (1/10th of a second).
     */
    fun seekTo(positionDs: Long) {
        logD("Seeking to ${positionDs}ds")
        playbackManager.seekTo(positionDs.dsToMs())
    }

    // --- QUEUE FUNCTIONS ---

    /** Skip to the next [Song]. */
    fun next() {
        logD("Skipping to next song")
        playbackManager.next()
    }

    /** Skip to the previous [Song]. */
    fun prev() {
        logD("Skipping to previous song")
        playbackManager.prev()
    }

    /**
     * Add a [Song] to the top of the queue.
     *
     * @param song The [Song] to add.
     */
    fun playNext(song: Song) {
        logD("Playing $song next")
        playbackManager.playNext(song)
    }

    /**
     * Add a [Album] to the top of the queue.
     *
     * @param album The [Album] to add.
     */
    fun playNext(album: Album) {
        logD("Playing $album next")
        playbackManager.playNext(musicSettings.albumSongSort.songs(album.songs))
    }

    /**
     * Add a [Artist] to the top of the queue.
     *
     * @param artist The [Artist] to add.
     */
    fun playNext(artist: Artist) {
        logD("Playing $artist next")
        playbackManager.playNext(musicSettings.artistSongSort.songs(artist.songs))
    }

    /**
     * Add a [Genre] to the top of the queue.
     *
     * @param genre The [Genre] to add.
     */
    fun playNext(genre: Genre) {
        logD("Playing $genre next")
        playbackManager.playNext(musicSettings.genreSongSort.songs(genre.songs))
    }

    /**
     * Add a [Playlist] to the top of the queue.
     *
     * @param playlist The [Playlist] to add.
     */
    fun playNext(playlist: Playlist) {
        logD("Playing $playlist next")
        playbackManager.playNext(playlist.songs)
    }

    /**
     * Add [Song]s to the top of the queue.
     *
     * @param songs The [Song]s to add.
     */
    fun playNext(songs: List<Song>) {
        logD("Playing ${songs.size} songs next")
        playbackManager.playNext(songs)
    }

    /**
     * Add a [Song] to the end of the queue.
     *
     * @param song The [Song] to add.
     */
    fun addToQueue(song: Song) {
        logD("Adding $song to queue")
        playbackManager.addToQueue(song)
    }

    /**
     * Add a [Album] to the end of the queue.
     *
     * @param album The [Album] to add.
     */
    fun addToQueue(album: Album) {
        logD("Adding $album to queue")
        playbackManager.addToQueue(musicSettings.albumSongSort.songs(album.songs))
    }

    /**
     * Add a [Artist] to the end of the queue.
     *
     * @param artist The [Artist] to add.
     */
    fun addToQueue(artist: Artist) {
        logD("Adding $artist to queue")
        playbackManager.addToQueue(musicSettings.artistSongSort.songs(artist.songs))
    }

    /**
     * Add a [Genre] to the end of the queue.
     *
     * @param genre The [Genre] to add.
     */
    fun addToQueue(genre: Genre) {
        logD("Adding $genre to queue")
        playbackManager.addToQueue(musicSettings.genreSongSort.songs(genre.songs))
    }

    /**
     * Add a [Playlist] to the end of the queue.
     *
     * @param playlist The [Playlist] to add.
     */
    fun addToQueue(playlist: Playlist) {
        logD("Adding $playlist to queue")
        playbackManager.addToQueue(playlist.songs)
    }

    /**
     * Add [Song]s to the end of the queue.
     *
     * @param songs The [Song]s to add.
     */
    fun addToQueue(songs: List<Song>) {
        logD("Adding ${songs.size} songs to queue")
        playbackManager.addToQueue(songs)
    }

    // --- STATUS FUNCTIONS ---

    /** Toggle [isPlaying] (i.e from playing to paused) */
    fun togglePlaying() {
        logD("Toggling playing state")
        playbackManager.setPlaying(!playbackManager.playerState.isPlaying)
    }

    /** Toggle [isShuffled] (ex. from on to off) */
    fun toggleShuffled() {
        logD("Toggling shuffled state")
        playbackManager.reorder(!playbackManager.queue.isShuffled)
    }

    /**
     * Toggle [repeatMode] (ex. from [RepeatMode.NONE] to [RepeatMode.TRACK])
     *
     * @see RepeatMode.increment
     */
    fun toggleRepeatMode() {
        logD("Toggling repeat mode")
        playbackManager.repeatMode = playbackManager.repeatMode.increment()
    }

    // --- UI CONTROL ---

    /** Open the main panel, closing all other panels. */
    fun openMain() = openImpl(OpenPanel.MAIN)

    /** Open the playback panel, closing the queue panel if needed. */
    fun openPlayback() = openImpl(OpenPanel.PLAYBACK)

    /**
     * Open the queue panel, assuming that it exists in the current layout, is collapsed, and with
     * the playback panel already being expanded.
     */
    fun openQueue() = openImpl(OpenPanel.QUEUE)

    private fun openImpl(panel: OpenPanel) {
        val existing = openPanel.flow.value
        if (existing != null) {
            logD("Already opening $existing, ignoring opening $panel")
            return
        }
        _openPanel.put(panel)
    }

    // --- SAVE/RESTORE FUNCTIONS ---

    /**
     * Force-save the current playback state.
     *
     * @param onDone Called when the save is completed with true if successful, and false otherwise.
     */
    fun savePlaybackState(onDone: (Boolean) -> Unit) {
        logD("Saving playback state")
        viewModelScope.launch {
            onDone(persistenceRepository.saveState(playbackManager.toSavedState()))
        }
    }

    /**
     * Clear the current playback state.
     *
     * @param onDone Called when the wipe is completed with true if successful, and false otherwise.
     */
    fun wipePlaybackState(onDone: (Boolean) -> Unit) {
        logD("Wiping playback state")
        viewModelScope.launch { onDone(persistenceRepository.saveState(null)) }
    }

    /**
     * Force-restore the current playback state.
     *
     * @param onDone Called when the restoration is completed with true if successful, and false
     *   otherwise.
     */
    fun tryRestorePlaybackState(onDone: (Boolean) -> Unit) {
        logD("Force-restoring playback state")
        viewModelScope.launch {
            val savedState = persistenceRepository.readState()
            if (savedState != null) {
                playbackManager.applySavedState(savedState, true)
                onDone(true)
                return@launch
            }
            onDone(false)
        }
    }
}

/**
 * Command for controlling the main playback panel UI.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
enum class OpenPanel {
    /** Open the main view, collapsing all other panels. */
    MAIN,
    /** Open the playback panel, collapsing the queue panel if applicable. */
    PLAYBACK,
    /**
     * Open the queue panel, assuming that it exists in the current layout, is collapsed, and with
     * the playback panel already being expanded. Do nothing if these conditions are not met.
     */
    QUEUE
}

/**
 * Command for opening decision dialogs when playback from a [Song] is ambiguous.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed interface PlaybackDecision {
    /** The [Song] currently attempting to be played from. */
    val song: Song
    /** Navigate to a dialog to determine which [Artist] a [Song] should be played from. */
    class PlayFromArtist(override val song: Song) : PlaybackDecision
    /** Navigate to a dialog to determine which [Genre] a [Song] should be played from. */
    class PlayFromGenre(override val song: Song) : PlaybackDecision
}
