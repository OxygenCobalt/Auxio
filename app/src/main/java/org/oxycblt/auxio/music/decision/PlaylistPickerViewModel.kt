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
 
package org.oxycblt.auxio.music.decision

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.sort.Sort
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.PlaylistDecision
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.external.ExportConfig
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.util.logW

/**
 * A [ViewModel] managing the state of the playlist picker dialogs.
 *
 * @author Alexander Capehart
 */
@HiltViewModel
class PlaylistPickerViewModel @Inject constructor(private val musicRepository: MusicRepository) :
    ViewModel(), MusicRepository.UpdateListener {
    private val _currentPendingNewPlaylist = MutableStateFlow<PendingNewPlaylist?>(null)
    /** A new [Playlist] having it's name chosen by the user. Null if none yet. */
    val currentPendingNewPlaylist: StateFlow<PendingNewPlaylist?>
        get() = _currentPendingNewPlaylist

    private val _currentPendingRenamePlaylist = MutableStateFlow<PendingRenamePlaylist?>(null)
    /** An existing [Playlist] that is being renamed. Null if none yet. */
    val currentPendingRenamePlaylist: StateFlow<PendingRenamePlaylist?>
        get() = _currentPendingRenamePlaylist

    private val _currentPlaylistToExport = MutableStateFlow<Playlist?>(null)
    /** An existing [Playlist] that is being exported. Null if none yet. */
    val currentPlaylistToExport: StateFlow<Playlist?>
        get() = _currentPlaylistToExport

    private val _currentExportConfig = MutableStateFlow(DEFAULT_EXPORT_CONFIG)
    /** The current [ExportConfig] to use when exporting a playlist. */
    val currentExportConfig: StateFlow<ExportConfig>
        get() = _currentExportConfig

    private val _currentPlaylistToDelete = MutableStateFlow<Playlist?>(null)
    /** The current [Playlist] that needs it's deletion confirmed. Null if none yet. */
    val currentPlaylistToDelete: StateFlow<Playlist?>
        get() = _currentPlaylistToDelete

    private val _chosenName = MutableStateFlow<ChosenName>(ChosenName.Empty)
    /** The users chosen name for [currentPendingNewPlaylist] or [currentPendingRenamePlaylist]. */
    val chosenName: StateFlow<ChosenName>
        get() = _chosenName

    private val _currentSongsToAdd = MutableStateFlow<List<Song>?>(null)
    /** A batch of [Song]s to add to a playlist chosen by the user. Null if none yet. */
    val currentSongsToAdd: StateFlow<List<Song>?>
        get() = _currentSongsToAdd

    private val _playlistAddChoices = MutableStateFlow<List<PlaylistChoice>>(listOf())
    /** The [Playlist]s that [currentSongsToAdd] could be added to. */
    val playlistAddChoices: StateFlow<List<PlaylistChoice>>
        get() = _playlistAddChoices

    init {
        musicRepository.addUpdateListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        var refreshChoicesWith: List<Song>? = null
        val deviceLibrary = musicRepository.deviceLibrary
        if (changes.deviceLibrary && deviceLibrary != null) {
            _currentPendingNewPlaylist.value =
                _currentPendingNewPlaylist.value?.let { pendingPlaylist ->
                    PendingNewPlaylist(
                        pendingPlaylist.preferredName,
                        pendingPlaylist.songs.mapNotNull { deviceLibrary.findSong(it.uid) },
                        pendingPlaylist.template,
                        pendingPlaylist.reason)
                }
            logD("Updated pending playlist: ${_currentPendingNewPlaylist.value?.preferredName}")

            _currentSongsToAdd.value =
                _currentSongsToAdd.value?.let { pendingSongs ->
                    pendingSongs
                        .mapNotNull { deviceLibrary.findSong(it.uid) }
                        .ifEmpty { null }
                        .also { refreshChoicesWith = it }
                }
            logD("Updated songs to add: ${_currentSongsToAdd.value?.size} songs")
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
            logD("Updated chosen name to $chosenName")
            refreshChoicesWith = refreshChoicesWith ?: _currentSongsToAdd.value

            // TODO: Add music syncing for other playlist states here

            _currentPlaylistToExport.value =
                _currentPlaylistToExport.value?.let { playlist ->
                    musicRepository.userLibrary?.findPlaylist(playlist.uid)
                }
            logD("Updated playlist to export to ${_currentPlaylistToExport.value}")
        }

        refreshChoicesWith?.let(::refreshPlaylistChoices)
    }

    override fun onCleared() {
        musicRepository.removeUpdateListener(this)
    }

    /**
     * Set a new [currentPendingNewPlaylist] from a new batch of pending [Song] [Music.UID]s.
     *
     * @param context [Context] required to generate a playlist name.
     * @param songUids The [Music.UID]s of songs to be present in the playlist.
     * @param reason The reason the playlist is being created.
     */
    fun setPendingPlaylist(
        context: Context,
        songUids: Array<Music.UID>,
        template: String?,
        reason: PlaylistDecision.New.Reason
    ) {
        logD("Opening ${songUids.size} songs to create a playlist from")
        val userLibrary = musicRepository.userLibrary ?: return
        val songs =
            musicRepository.deviceLibrary
                ?.let { songUids.mapNotNull(it::findSong) }
                ?.also(::refreshPlaylistChoices)

        val possibleName =
            musicRepository.userLibrary?.let {
                // Attempt to generate a unique default name for the playlist, like "Playlist 1".
                var i = 1
                var possibleName: String
                do {
                    possibleName = context.getString(R.string.fmt_def_playlist, i)
                    logD("Trying $possibleName as a playlist name")
                    ++i
                } while (userLibrary.playlists.any { it.name.resolve(context) == possibleName })
                logD("$possibleName is unique, using it as the playlist name")
                possibleName
            }

        _currentPendingNewPlaylist.value =
            if (possibleName != null && songs != null) {
                PendingNewPlaylist(possibleName, songs, template, reason)
            } else {
                logW("Given song UIDs to create were invalid")
                null
            }
    }

    /**
     * Set a new [currentPendingRenamePlaylist] from a [Playlist] [Music.UID].
     *
     * @param playlistUid The [Music.UID]s of the [Playlist] to rename.
     */
    fun setPlaylistToRename(
        playlistUid: Music.UID,
        applySongUids: Array<Music.UID>,
        template: String?,
        reason: PlaylistDecision.Rename.Reason
    ) {
        logD("Opening playlist $playlistUid to rename")
        val playlist = musicRepository.userLibrary?.findPlaylist(playlistUid)
        val applySongs =
            musicRepository.deviceLibrary?.let { applySongUids.mapNotNull(it::findSong) }

        _currentPendingRenamePlaylist.value =
            if (playlist != null && applySongs != null) {
                PendingRenamePlaylist(playlist, applySongs, template, reason)
            } else {
                logW("Given playlist UID to rename was invalid")
                null
            }
    }

    /**
     * Set a new [currentPlaylistToExport] from a [Playlist] [Music.UID].
     *
     * @param playlistUid The [Music.UID] of the [Playlist] to export.
     */
    fun setPlaylistToExport(playlistUid: Music.UID) {
        logD("Opening playlist $playlistUid to export")
        // TODO: Add this guard to the rest of the methods here
        if (_currentPlaylistToExport.value?.uid == playlistUid) return
        _currentPlaylistToExport.value = musicRepository.userLibrary?.findPlaylist(playlistUid)
        if (_currentPlaylistToExport.value == null) {
            logW("Given playlist UID to export was invalid")
        } else {
            _currentExportConfig.value = DEFAULT_EXPORT_CONFIG
        }
    }

    /**
     * Update [currentExportConfig] based on new user input.
     *
     * @param exportConfig The new [ExportConfig] to use.
     */
    fun setExportConfig(exportConfig: ExportConfig) {
        logD("Setting export config to $exportConfig")
        _currentExportConfig.value = exportConfig
    }

    /**
     * Set a new [currentPendingNewPlaylist] from a new [Playlist] [Music.UID].
     *
     * @param playlistUid The [Music.UID] of the [Playlist] to delete.
     */
    fun setPlaylistToDelete(playlistUid: Music.UID) {
        logD("Opening playlist $playlistUid to delete")
        _currentPlaylistToDelete.value = musicRepository.userLibrary?.findPlaylist(playlistUid)
        if (_currentPlaylistToDelete.value == null) {
            logW("Given playlist UID to delete was invalid")
        }
    }

    /**
     * Update the current [chosenName] based on new user input.
     *
     * @param name The new user-inputted name, or null if not present.
     */
    fun updateChosenName(name: String?) {
        logD("Updating chosen name to $name")
        _chosenName.value =
            when {
                name.isNullOrEmpty() -> {
                    logE("Chosen name is empty")
                    ChosenName.Empty
                }
                name.isBlank() -> {
                    logE("Chosen name is blank")
                    ChosenName.Blank
                }
                else -> {
                    val trimmed = name.trim()
                    val userLibrary = musicRepository.userLibrary
                    if (userLibrary != null && userLibrary.findPlaylist(trimmed) == null) {
                        logD("Chosen name is valid")
                        ChosenName.Valid(trimmed)
                    } else {
                        logD("Chosen name already exists in library")
                        ChosenName.AlreadyExists(trimmed)
                    }
                }
            }
    }

    /**
     * Set a new [currentSongsToAdd] from a new batch of pending [Song] [Music.UID]s.
     *
     * @param songUids The [Music.UID]s of songs to add to a playlist.
     */
    fun setSongsToAdd(songUids: Array<Music.UID>) {
        logD("Opening ${songUids.size} songs to add to a playlist")
        _currentSongsToAdd.value =
            musicRepository.deviceLibrary
                ?.let { songUids.mapNotNull(it::findSong).ifEmpty { null } }
                ?.also(::refreshPlaylistChoices)
        if (_currentSongsToAdd.value == null || songUids.size != _currentSongsToAdd.value?.size) {
            logW("Given song UIDs to add were (partially) invalid")
        }
    }

    private fun refreshPlaylistChoices(songs: List<Song>) {
        val userLibrary = musicRepository.userLibrary ?: return
        logD("Refreshing playlist choices")
        _playlistAddChoices.value =
            Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING).playlists(userLibrary.playlists).map {
                val songSet = it.songs.toSet()
                PlaylistChoice(it, songs.all(songSet::contains))
            }
    }

    private companion object {
        private val DEFAULT_EXPORT_CONFIG = ExportConfig(absolute = false, windowsPaths = false)
    }
}

/**
 * Represents a playlist that will be created as soon as a name is chosen.
 *
 * @param preferredName The name to be used by default if no other name is chosen.
 * @param songs The [Song]s to be contained in the [PendingNewPlaylist]
 * @param reason The reason the playlist is being created.
 * @author Alexander Capehart (OxygenCobalt)
 */
data class PendingNewPlaylist(
    val preferredName: String,
    val songs: List<Song>,
    val template: String?,
    val reason: PlaylistDecision.New.Reason
)

data class PendingRenamePlaylist(
    val playlist: Playlist,
    val applySongs: List<Song>,
    val template: String?,
    val reason: PlaylistDecision.Rename.Reason
)

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
    data object Empty : ChosenName
    /** The current name only consists of whitespace. */
    data object Blank : ChosenName
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
