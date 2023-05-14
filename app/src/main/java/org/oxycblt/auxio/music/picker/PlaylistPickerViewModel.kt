/*
 * Copyright (c) 2023 Auxio Project
 * PlaylistPickerViewModel.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.picker

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.Sort
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song

/**
 * A [ViewModel] managing the state of the playlist picker dialogs.
 *
 * @author Alexander Capehart
 */
@HiltViewModel
class PlaylistPickerViewModel @Inject constructor(private val musicRepository: MusicRepository) :
    ViewModel(), MusicRepository.UpdateListener {
    private val _currentPendingPlaylist = MutableStateFlow<PendingPlaylist?>(null)
    val currentPendingPlaylist: StateFlow<PendingPlaylist?>
        get() = _currentPendingPlaylist

    private val _chosenName = MutableStateFlow<ChosenName>(ChosenName.Empty)
    val chosenName: StateFlow<ChosenName>
        get() = _chosenName

    private val _currentPendingSongs = MutableStateFlow<List<Song>?>(null)
    val currentPendingSongs: StateFlow<List<Song>?>
        get() = _currentPendingSongs

    private val _playlistChoices = MutableStateFlow<List<PlaylistChoice>>(listOf())
    val playlistChoices: StateFlow<List<PlaylistChoice>>
        get() = _playlistChoices

    init {
        musicRepository.addUpdateListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        var refreshChoicesWith: List<Song>? = null
        val deviceLibrary = musicRepository.deviceLibrary
        if (changes.deviceLibrary && deviceLibrary != null) {
            _currentPendingPlaylist.value =
                _currentPendingPlaylist.value?.let { pendingPlaylist ->
                    PendingPlaylist(
                        pendingPlaylist.preferredName,
                        pendingPlaylist.songs.mapNotNull { deviceLibrary.findSong(it.uid) })
                }
            _currentPendingSongs.value =
                _currentPendingSongs.value?.let { pendingSongs ->
                    pendingSongs
                        .mapNotNull { deviceLibrary.findSong(it.uid) }
                        .ifEmpty { null }
                        .also { refreshChoicesWith = it }
                }
        }

        val chosenName = _chosenName.value
        if (changes.userLibrary) {
            when (chosenName) {
                is ChosenName.Valid -> updateChosenName(chosenName.value)
                is ChosenName.AlreadyExists -> updateChosenName(chosenName.prior)
                else -> {
                    // Nothing to do.
                }
            }
            refreshChoicesWith = refreshChoicesWith ?: _currentPendingSongs.value
        }

        refreshChoicesWith?.let(::refreshPlaylistChoices)
    }

    override fun onCleared() {
        musicRepository.removeUpdateListener(this)
    }

    /**
     * Update the current [PendingPlaylist]. Will do nothing if already equal.
     *
     * @param context [Context] required to generate a playlist name.
     * @param songUids The [Music.UID]s of songs to be present in the playlist.
     */
    fun setPendingPlaylist(context: Context, songUids: Array<Music.UID>) {
        if (currentPendingPlaylist.value?.songs?.map { it.uid } == songUids) {
            // Nothing to do.
            return
        }
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        val songs = songUids.mapNotNull(deviceLibrary::findSong)

        val userLibrary = musicRepository.userLibrary ?: return
        var i = 1
        while (true) {
            val possibleName = context.getString(R.string.fmt_def_playlist, i)
            if (userLibrary.playlists.none { it.name.resolve(context) == possibleName }) {
                _currentPendingPlaylist.value = PendingPlaylist(possibleName, songs)
                return
            }
            ++i
        }
    }

    /**
     * Update the current [ChosenName] based on new user input.
     *
     * @param name The new user-inputted name, or null if not present.
     */
    fun updateChosenName(name: String?) {
        _chosenName.value =
            when {
                name.isNullOrEmpty() -> ChosenName.Empty
                name.isBlank() -> ChosenName.Blank
                else -> {
                    val trimmed = name.trim()
                    val userLibrary = musicRepository.userLibrary
                    if (userLibrary != null && userLibrary.findPlaylist(trimmed) == null) {
                        ChosenName.Valid(trimmed)
                    } else {
                        ChosenName.AlreadyExists(trimmed)
                    }
                }
            }
    }

    /** Confirm the playlist creation process as completed. */
    fun confirmPlaylistCreation() {
        // Confirm any playlist additions if needed, as the creation process may have been started
        // by it and is still waiting on a result.
        confirmPlaylistAddition()
        _currentPendingPlaylist.value = null
        _chosenName.value = ChosenName.Empty
    }

    /**
     * Update the current [Song]s that to show playlist add choices for. Will do nothing if already
     * equal.
     *
     * @param songUids The [Music.UID]s of songs to add to a playlist.
     */
    fun setPendingSongs(songUids: Array<Music.UID>) {
        if (currentPendingSongs.value?.map { it.uid } == songUids) return
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        val songs = songUids.mapNotNull(deviceLibrary::findSong)
        _currentPendingSongs.value = songs
        refreshPlaylistChoices(songs)
    }

    /** Mark the addition process as complete. */
    fun confirmPlaylistAddition() {
        _currentPendingSongs.value = null
    }

    private fun refreshPlaylistChoices(songs: List<Song>) {
        val userLibrary = musicRepository.userLibrary ?: return
        _playlistChoices.value =
            Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING).playlists(userLibrary.playlists).map {
                val songSet = it.songs.toSet()
                PlaylistChoice(it, songs.all(songSet::contains))
            }
    }
}

/**
 * Represents a playlist that will be created as soon as a name is chosen.
 *
 * @param preferredName The name to be used by default if no other name is chosen.
 * @param songs The [Song]s to be contained in the [PendingPlaylist]
 * @author Alexander Capehart (OxygenCobalt)
 */
data class PendingPlaylist(val preferredName: String, val songs: List<Song>)

/**
 * Represents the (processed) user input from the playlist naming dialogs.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed interface ChosenName {
    /** The current name is valid. */
    data class Valid(val value: String) : ChosenName
    /** The current name already exists. */
    data class AlreadyExists(val prior: String) : ChosenName
    /** The current name is empty. */
    object Empty : ChosenName
    /** The current name only consists of whitespace. */
    object Blank : ChosenName
}

/**
 * An individual [Playlist] choice to add [Song]s to.
 *
 * @param playlist The [Playlist] represented.
 * @param alreadyAdded Whether the songs currently pending addition have already been added to the
 *   [Playlist].
 * @author Alexander Capehart (OxygenCobalt)
 */
data class PlaylistChoice(val playlist: Playlist, val alreadyAdded: Boolean) : Item
