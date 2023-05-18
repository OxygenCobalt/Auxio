/*
 * Copyright (c) 2021 Auxio Project
 * MusicViewModel.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.util.Event
import org.oxycblt.auxio.util.MutableEvent

/**
 * A [ViewModel] providing data specific to the music loading process.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@HiltViewModel
class MusicViewModel
@Inject
constructor(
    private val musicRepository: MusicRepository,
    private val musicSettings: MusicSettings
) : ViewModel(), MusicRepository.UpdateListener, MusicRepository.IndexingListener {

    private val _indexingState = MutableStateFlow<IndexingState?>(null)
    /** The current music loading state, or null if no loading is going on. */
    val indexingState: StateFlow<IndexingState?> = _indexingState

    private val _statistics = MutableStateFlow<Statistics?>(null)
    /** [Statistics] about the last completed music load. */
    val statistics: StateFlow<Statistics?>
        get() = _statistics

    private val _newPlaylistSongs = MutableEvent<List<Song>>()
    /** Flag for opening a dialog to create a playlist of the given [Song]s. */
    val newPlaylistSongs: Event<List<Song>> = _newPlaylistSongs

    private val _songsToAdd = MutableEvent<List<Song>>()
    /** Flag for opening a dialog to add the given [Song]s to a playlist. */
    val songsToAdd: Event<List<Song>> = _songsToAdd

    private val _playlistToDelete = MutableEvent<Playlist>()
    /** Flag for opening a dialog to confirm deletion of the given [Playlist]. */
    val playlistToDelete: Event<Playlist>
        get() = _playlistToDelete

    init {
        musicRepository.addUpdateListener(this)
        musicRepository.addIndexingListener(this)
    }

    override fun onCleared() {
        musicRepository.removeUpdateListener(this)
        musicRepository.removeIndexingListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        if (!changes.deviceLibrary) return
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        _statistics.value =
            Statistics(
                deviceLibrary.songs.size,
                deviceLibrary.albums.size,
                deviceLibrary.artists.size,
                deviceLibrary.genres.size,
                deviceLibrary.songs.sumOf { it.durationMs })
    }

    override fun onIndexingStateChanged() {
        _indexingState.value = musicRepository.indexingState
    }

    /** Requests that the music library should be re-loaded while leveraging the cache. */
    fun refresh() {
        musicRepository.requestIndex(true)
    }

    /** Requests that the music library be re-loaded without the cache. */
    fun rescan() {
        musicRepository.requestIndex(false)
    }

    /**
     * Create a new generic [Playlist].
     *
     * @param name The name of the new [Playlist]. If null, the user will be prompted for one.
     * @param songs The [Song]s to be contained in the new playlist.
     */
    fun createPlaylist(name: String? = null, songs: List<Song> = listOf()) {
        if (name != null) {
            musicRepository.createPlaylist(name, songs)
        } else {
            _newPlaylistSongs.put(songs)
        }
    }

    /**
     * Delete a [Playlist].
     *
     * @param playlist The playlist to delete.
     * @param rude Whether to immediately delete the playlist or prompt the user first. This should
     *   be false at almost all times.
     */
    fun deletePlaylist(playlist: Playlist, rude: Boolean = false) {
        if (rude) {
            musicRepository.deletePlaylist(playlist)
        } else {
            _playlistToDelete.put(playlist)
        }
    }

    /**
     * Add a [Song] to a [Playlist].
     *
     * @param song The [Song] to add to the [Playlist].
     * @param playlist The [Playlist] to add to. If null, the user will be prompted for one.
     */
    fun addToPlaylist(song: Song, playlist: Playlist? = null) {
        addToPlaylist(listOf(song), playlist)
    }

    /**
     * Add an [Album] to a [Playlist].
     *
     * @param album The [Album] to add to the [Playlist].
     * @param playlist The [Playlist] to add to. If null, the user will be prompted for one.
     */
    fun addToPlaylist(album: Album, playlist: Playlist? = null) {
        addToPlaylist(musicSettings.albumSongSort.songs(album.songs), playlist)
    }

    /**
     * Add an [Artist] to a [Playlist].
     *
     * @param artist The [Artist] to add to the [Playlist].
     * @param playlist The [Playlist] to add to. If null, the user will be prompted for one.
     */
    fun addToPlaylist(artist: Artist, playlist: Playlist? = null) {
        addToPlaylist(musicSettings.artistSongSort.songs(artist.songs), playlist)
    }

    /**
     * Add a [Genre] to a [Playlist].
     *
     * @param genre The [Genre] to add to the [Playlist].
     * @param playlist The [Playlist] to add to. If null, the user will be prompted for one.
     */
    fun addToPlaylist(genre: Genre, playlist: Playlist? = null) {
        addToPlaylist(musicSettings.genreSongSort.songs(genre.songs), playlist)
    }

    /**
     * Add [Song]s to a [Playlist].
     *
     * @param songs The [Song]s to add to the [Playlist].
     * @param playlist The [Playlist] to add to. If null, the user will be prompted for one.
     */
    fun addToPlaylist(songs: List<Song>, playlist: Playlist? = null) {
        if (playlist != null) {
            musicRepository.addToPlaylist(songs, playlist)
        } else {
            _songsToAdd.put(songs)
        }
    }

    /**
     * Non-manipulated statistics bound the last successful music load.
     *
     * @param songs The amount of [Song]s that were loaded.
     * @param albums The amount of [Album]s that were created.
     * @param artists The amount of [Artist]s that were created.
     * @param genres The amount of [Genre]s that were created.
     * @param durationMs The total duration of all songs in the library, in milliseconds.
     */
    data class Statistics(
        val songs: Int,
        val albums: Int,
        val artists: Int,
        val genres: Int,
        val durationMs: Long
    )
}
