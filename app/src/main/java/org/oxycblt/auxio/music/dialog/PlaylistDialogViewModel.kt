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
 * @author Alexander Capehart
 */
@HiltViewModel
class PlaylistDialogViewModel @Inject constructor(private val musicRepository: MusicRepository) :
    ViewModel(), MusicRepository.UpdateListener {
    private val _currentPendingName = MutableStateFlow<PendingName?>(null)
    val currentPendingName: StateFlow<PendingName?> = _currentPendingName

    init {
        musicRepository.addUpdateListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        if (!changes.deviceLibrary) return
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        // Update the pending name to reflect new information in the music library.
        _currentPendingName.value =
            _currentPendingName.value?.let { pendingName ->
                PendingName(
                    pendingName.name,
                    pendingName.songs.mapNotNull { deviceLibrary.findSong(it.uid) })
            }
    }

    override fun onCleared() {
        musicRepository.removeUpdateListener(this)
    }

    /**
     * Update the current [PendingName] based on the given [PendingName.Args].
     * @param args The [PendingName.Args] to update with.
     */
    fun setPendingName(args: PendingName.Args) {
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        val name =
            PendingName(args.preferredName, args.songUids.mapNotNull(deviceLibrary::findSong))
        _currentPendingName.value = name
    }

    /**
     * Update the current [PendingName] based on new user input.
     * @param name The new user-inputted name, directly from the UI.
     */
    fun updatePendingName(name: String?) {
        // Remove any additional whitespace from the string to be consistent with all other
        // music items.
        val normalized = (name ?: return).trim()
        _currentPendingName.value =
            _currentPendingName.value?.run { PendingName(normalized, songs) }
    }

    /**
     * Confirm the current [PendingName] operation and write it to the database.
     */
    fun confirmPendingName() {
        val pendingName = _currentPendingName.value ?: return
        musicRepository.createPlaylist(pendingName.name, pendingName.songs)
        _currentPendingName.value = null
    }
}

/**
 * Represents a name operation
 */
data class PendingName(val name: String, val songs: List<Song>) {
    /**
     * A [Parcelable] version of [PendingName], to be used as a dialog argument.
     * @param preferredName The name to be used initially by the dialog.
     * @param songUids The [Music.UID] of any pending [Song]s that will be put in the playlist.
     */
    @Parcelize
    data class Args(val preferredName: String, val songUids: List<Music.UID>) : Parcelable
}
