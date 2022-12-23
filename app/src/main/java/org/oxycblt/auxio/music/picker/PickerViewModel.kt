/*
 * Copyright (c) 2022 Auxio Project
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

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * a [ViewModel] that manages the current music picker state.
 * TODO: This really shouldn't exist. Make it so that the dialogs just contain the music
 * themselves and then exit if the library changes.
 * TODO: While we are at it, let's go and add ClickableSpan too to reduce the extent of
 * this dialog.
 * @author Alexander Capehart (OxygenCobalt)
 */
class PickerViewModel : ViewModel(), MusicStore.Callback {
    private val musicStore = MusicStore.getInstance()

    private val _currentSong = MutableStateFlow<Song?>(null)
    /**
     * The current [Song] whose choices are being shown in the picker. Null if there is no [Song].
     */
    val currentSong: StateFlow<Song?>
        get() = _currentSong

    private val _currentArtists = MutableStateFlow<List<Artist>?>(null)
    /**
     * The current [Artist] whose choices are being shown in the picker. Null/Empty if there is none.
     */
    val currentArtists: StateFlow<List<Artist>?>
        get() = _currentArtists

    override fun onCleared() {
        musicStore.removeCallback(this)
    }

    override fun onLibraryChanged(library: MusicStore.Library?) {
        if (library != null) {
            // If we are showing any item right now, we will need to refresh it (and any information
            // related to it) with the new library in order to prevent stale items from appearing
            // in the UI.
            val song = _currentSong.value
            val artists = _currentArtists.value
            if (song != null) {
                _currentSong.value = library.sanitize(song)
                _currentArtists.value = _currentSong.value?.artists
            } else if (artists != null) {
                _currentArtists.value = artists.mapNotNull { library.sanitize(it) }
            }
        }
    }

    /**
     * Set a new [currentSong] from it's [Music.UID].
     * @param uid The [Music.UID] of the [Song] to update to.
     */
    fun setSongUid(uid: Music.UID) {
        val library = unlikelyToBeNull(musicStore.library)
        _currentSong.value = library.find(uid)
        _currentArtists.value = _currentSong.value?.artists
    }

    /**
     * Set a new [currentArtists] list from a list of [Music.UID]'s.
     * @param uids The [Music.UID]s of the [Artist]s to [currentArtists] to.
     */
    fun setArtistUids(uids: Array<Music.UID>) {
        val library = unlikelyToBeNull(musicStore.library)
        // Map the UIDs to artist instances and filter out the ones that can't be found.
        _currentArtists.value = uids.mapNotNull { library.find<Artist>(it) }.ifEmpty { null }
    }

}
