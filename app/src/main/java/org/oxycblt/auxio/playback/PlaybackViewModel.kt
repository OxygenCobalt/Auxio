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
import org.oxycblt.auxio.list.ListSettings
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.DeferredPlayback
import org.oxycblt.auxio.playback.state.PlaybackCommand
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.Progression
import org.oxycblt.auxio.playback.state.QueueChange
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.playback.state.ShuffleMode
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
    private val commandFactory: PlaybackCommand.Factory,
    private val listSettings: ListSettings,
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
     * The current audio session ID of the internal player. Null if no audio player is available.
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

    override fun onIndexMoved(index: Int) {
        logD("Index moved, updating current song")
        _song.value = playbackManager.currentSong
    }

    override fun onQueueChanged(queue: List<Song>, index: Int, change: QueueChange) {
        // Other types of queue changes preserve the current song.
        if (change.type == QueueChange.Type.SONG) {
            logD("Queue changed, updating current song")
            _song.value = playbackManager.currentSong
        }
    }

    override fun onQueueReordered(queue: List<Song>, index: Int, isShuffled: Boolean) {
        logD("Queue completely changed, updating current song")
        _isShuffled.value = isShuffled
    }

    override fun onNewPlayback(
        parent: MusicParent?,
        queue: List<Song>,
        index: Int,
        isShuffled: Boolean
    ) {
        logD("New playback started, updating playback information")
        _song.value = playbackManager.currentSong
        _parent.value = parent
        _isShuffled.value = isShuffled
    }

    override fun onProgressionChanged(progression: Progression) {
        logD("Player state changed, starting new position polling")
        _isPlaying.value = progression.isPlaying
        // Still need to update the position now due to co-routine launch delays
        _positionDs.value = progression.calculateElapsedPositionMs().msToDs()
        // Replace the previous position co-routine with a new one that uses the new
        // state information.
        lastPositionJob?.cancel()
        lastPositionJob =
            viewModelScope.launch {
                while (true) {
                    _positionDs.value = progression.calculateElapsedPositionMs().msToDs()
                    // Wait a deci-second for the next position tick.
                    delay(100)
                }
            }
    }

    override fun onRepeatModeChanged(repeatMode: RepeatMode) {
        _repeatMode.value = repeatMode
    }

    override fun onBarActionChanged() {
        _currentBarAction.value = playbackSettings.barAction
    }

    // --- PLAYING FUNCTIONS ---

    fun play(song: Song, with: PlaySong) {
        logD("Playing $song with $with")
        playWithImpl(song, with, ShuffleMode.IMPLICIT)
    }

    fun playExplicit(song: Song, with: PlaySong) {
        playWithImpl(song, with, ShuffleMode.OFF)
    }

    fun shuffleExplicit(song: Song, with: PlaySong) {
        playWithImpl(song, with, ShuffleMode.ON)
    }

    /** Shuffle all songs in the music library. */
    fun shuffleAll() {
        logD("Shuffling all songs")
        playFromAllImpl(null, ShuffleMode.ON)
    }

    /**
     * Play a [Song] from one of it's [Artist]s.
     *
     * @param song The [Song] to play.
     * @param artist The [Artist] to play from. Must be linked to the [Song]. If null, the user will
     *   be prompted on what artist to play. Defaults to null.
     */
    fun playFromArtist(song: Song, artist: Artist? = null) {
        playFromArtistImpl(song, artist, ShuffleMode.IMPLICIT)
    }

    /**
     * Play a [Song] from one of it's [Genre]s.
     *
     * @param song The [Song] to play.
     * @param genre The [Genre] to play from. Must be linked to the [Song]. If null, the user will
     *   be prompted on what artist to play. Defaults to null.
     */
    fun playFromGenre(song: Song, genre: Genre? = null) {
        playFromGenreImpl(song, genre, ShuffleMode.IMPLICIT)
    }

    private fun playWithImpl(song: Song, with: PlaySong, shuffle: ShuffleMode) {
        when (with) {
            is PlaySong.FromAll -> playFromAllImpl(song, shuffle)
            is PlaySong.FromAlbum -> playFromAlbumImpl(song, shuffle)
            is PlaySong.FromArtist -> playFromArtistImpl(song, with.which, shuffle)
            is PlaySong.FromGenre -> playFromGenreImpl(song, with.which, shuffle)
            is PlaySong.FromPlaylist -> playFromPlaylistImpl(song, with.which, shuffle)
            is PlaySong.ByItself -> playItselfImpl(song, shuffle)
        }
    }

    private fun playItselfImpl(song: Song, shuffle: ShuffleMode) {
        playbackManager.play(
            requireNotNull(commandFactory.song(song, shuffle)) {
                "Invalid playback parameters [$song $shuffle]"
            })
    }

    private fun playFromAllImpl(song: Song?, shuffle: ShuffleMode) {
        val params =
            if (song != null) {
                commandFactory.songFromAll(song, shuffle)
            } else {
                commandFactory.all(shuffle)
            }

        playImpl(params)
    }

    private fun playFromAlbumImpl(song: Song, shuffle: ShuffleMode) {
        logD("Playing $song from album")
        playImpl(commandFactory.songFromAlbum(song, shuffle))
    }

    private fun playFromArtistImpl(song: Song, artist: Artist?, shuffle: ShuffleMode) {
        val params = commandFactory.songFromArtist(song, artist, shuffle)
        if (params != null) {
            playbackManager.play(params)
            return
        }
        logD(
            "Cannot use given artist parameter for $song [$artist from ${song.artists}], showing choice dialog")
        startPlaybackDecision(PlaybackDecision.PlayFromArtist(song))
    }

    private fun playFromGenreImpl(song: Song, genre: Genre?, shuffle: ShuffleMode) {
        val params = commandFactory.songFromGenre(song, genre, shuffle)
        if (params != null) {
            playbackManager.play(params)
            return
        }
        logD(
            "Cannot use given genre parameter for $song [$genre from ${song.genres}], showing choice dialog")
        startPlaybackDecision(PlaybackDecision.PlayFromArtist(song))
    }

    private fun playFromPlaylistImpl(song: Song, playlist: Playlist, shuffle: ShuffleMode) {
        logD("Playing $song from $playlist")
        playImpl(commandFactory.songFromPlaylist(song, playlist, shuffle))
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
        playImpl(commandFactory.album(album, ShuffleMode.OFF))
    }

    /**
     * Shuffle an [Album].
     *
     * @param album The [Album] to shuffle.
     */
    fun shuffle(album: Album) {
        logD("Shuffling $album")
        playImpl(commandFactory.album(album, ShuffleMode.ON))
    }

    /**
     * Play an [Artist].
     *
     * @param artist The [Artist] to play.
     */
    fun play(artist: Artist) {
        logD("Playing $artist")
        playImpl(commandFactory.artist(artist, ShuffleMode.OFF))
    }

    /**
     * Shuffle an [Artist].
     *
     * @param artist The [Artist] to shuffle.
     */
    fun shuffle(artist: Artist) {
        logD("Shuffling $artist")
        playImpl(commandFactory.artist(artist, ShuffleMode.ON))
    }

    /**
     * Play a [Genre].
     *
     * @param genre The [Genre] to play.
     */
    fun play(genre: Genre) {
        logD("Playing $genre")
        playImpl(commandFactory.genre(genre, ShuffleMode.OFF))
    }

    /**
     * Shuffle a [Genre].
     *
     * @param genre The [Genre] to shuffle.
     */
    fun shuffle(genre: Genre) {
        logD("Shuffling $genre")
        playImpl(commandFactory.genre(genre, ShuffleMode.ON))
    }

    /**
     * Play a [Playlist].
     *
     * @param playlist The [Playlist] to play.
     */
    fun play(playlist: Playlist) {
        logD("Playing $playlist")
        playImpl(commandFactory.playlist(playlist, ShuffleMode.OFF))
    }

    /**
     * Shuffle a [Playlist].
     *
     * @param playlist The [Playlist] to shuffle.
     */
    fun shuffle(playlist: Playlist) {
        logD("Shuffling $playlist")
        playImpl(commandFactory.playlist(playlist, ShuffleMode.ON))
    }

    /**
     * Play a list of [Song]s.
     *
     * @param songs The [Song]s to play.
     */
    fun play(songs: List<Song>) {
        logD("Playing ${songs.size} songs")
        playImpl(commandFactory.songs(songs, ShuffleMode.OFF))
    }

    /**
     * Shuffle a list of [Song]s.
     *
     * @param songs The [Song]s to shuffle.
     */
    fun shuffle(songs: List<Song>) {
        logD("Shuffling ${songs.size} songs")
        playImpl(commandFactory.songs(songs, ShuffleMode.ON))
    }

    private fun playImpl(command: PlaybackCommand?) {
        playbackManager.play(requireNotNull(command) { "Invalid playback parameters" })
    }

    /**
     * Start the given [DeferredPlayback] to be completed eventually. This can be used to enqueue a
     * playback action at startup to then occur when the music library is fully loaded.
     *
     * @param action The [DeferredPlayback] to perform eventually.
     */
    fun playDeferred(action: DeferredPlayback) {
        logD("Starting action $action")
        playbackManager.playDeferred(action)
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
        playbackManager.playNext(listSettings.albumSongSort.songs(album.songs))
    }

    /**
     * Add a [Artist] to the top of the queue.
     *
     * @param artist The [Artist] to add.
     */
    fun playNext(artist: Artist) {
        logD("Playing $artist next")
        playbackManager.playNext(listSettings.artistSongSort.songs(artist.songs))
    }

    /**
     * Add a [Genre] to the top of the queue.
     *
     * @param genre The [Genre] to add.
     */
    fun playNext(genre: Genre) {
        logD("Playing $genre next")
        playbackManager.playNext(listSettings.genreSongSort.songs(genre.songs))
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
        playbackManager.addToQueue(listSettings.albumSongSort.songs(album.songs))
    }

    /**
     * Add a [Artist] to the end of the queue.
     *
     * @param artist The [Artist] to add.
     */
    fun addToQueue(artist: Artist) {
        logD("Adding $artist to queue")
        playbackManager.addToQueue(listSettings.artistSongSort.songs(artist.songs))
    }

    /**
     * Add a [Genre] to the end of the queue.
     *
     * @param genre The [Genre] to add.
     */
    fun addToQueue(genre: Genre) {
        logD("Adding $genre to queue")
        playbackManager.addToQueue(listSettings.genreSongSort.songs(genre.songs))
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
        playbackManager.playing(!playbackManager.progression.isPlaying)
    }

    /** Toggle [isShuffled] (ex. from on to off) */
    fun toggleShuffled() {
        logD("Toggling shuffled state")
        playbackManager.shuffled(!playbackManager.isShuffled)
    }

    /**
     * Toggle [repeatMode] (ex. from [RepeatMode.NONE] to [RepeatMode.TRACK])
     *
     * @see RepeatMode.increment
     */
    fun toggleRepeatMode() {
        logD("Toggling repeat mode")
        playbackManager.repeatMode(playbackManager.repeatMode.increment())
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
