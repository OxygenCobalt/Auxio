/*
 * Copyright (c) 2023 Auxio Project
 * PlaylistDialogViewModel.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.dialog

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.parcelize.Parcelize
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.Song

/**
 * A [ViewModel] managing the state of the playlist editing dialogs.
 *
 * @author Alexander Capehart
 */
@HiltViewModel
class PlaylistDialogViewModel @Inject constructor(private val musicRepository: MusicRepository) :
    ViewModel(), MusicRepository.UpdateListener {
    var pendingPlaylist: PendingPlaylist? = null
        private set

    private val _pendingPlaylistValid = MutableStateFlow(false)
    val pendingPlaylistValid: StateFlow<Boolean> = _pendingPlaylistValid

    init {
        musicRepository.addUpdateListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        pendingPlaylist?.let(::validateName)
    }

    override fun onCleared() {
        musicRepository.removeUpdateListener(this)
    }

    /**
     * Update the current [PendingPlaylist]. Will do nothing if already equal.
     *
     * @param pendingPlaylist The [PendingPlaylist] to update with.
     */
    fun setPendingName(pendingPlaylist: PendingPlaylist) {
        if (this.pendingPlaylist == pendingPlaylist) return
        this.pendingPlaylist = pendingPlaylist
        validateName(pendingPlaylist)
    }

    /**
     * Update the current [PendingPlaylist] based on new user input.
     *
     * @param name The new user-inputted name.
     */
    fun updatePendingName(name: String) {
        val current = pendingPlaylist ?: return
        // Remove any additional whitespace from the string to be consistent with all other
        // music items.
        val new = PendingPlaylist(name.trim(), current.songUids)
        pendingPlaylist = new
        validateName(new)
    }

    /** Confirm the current [PendingPlaylist] operation and write it to the database. */
    fun confirmPendingName() {
        val playlist = pendingPlaylist ?: return
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        musicRepository.createPlaylist(
            playlist.name, playlist.songUids.mapNotNull(deviceLibrary::findSong))
    }

    private fun validateName(pendingPlaylist: PendingPlaylist) {
        val userLibrary = musicRepository.userLibrary
        _pendingPlaylistValid.value =
            pendingPlaylist.name.isNotBlank() &&
                userLibrary != null &&
                userLibrary.findPlaylist(pendingPlaylist.name) == null
    }
}

/**
 * Represents a playlist that is currently being named before actually being completed.
 *
 * @param name The name of the playlist.
 * @param songUids The [Music.UID]s of the [Song]s to be contained by the playlist.
 */
@Parcelize data class PendingPlaylist(val name: String, val songUids: List<Music.UID>) : Parcelable
