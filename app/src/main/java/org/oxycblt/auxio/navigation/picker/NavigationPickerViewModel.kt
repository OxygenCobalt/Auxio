/*
 * Copyright (c) 2023 Auxio Project
 * NavigationPickerViewModel.kt is part of Auxio.
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
 
package org.oxycblt.auxio.navigation.picker

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.picker.PickerChoices

/**
 * A [ViewModel] that stores the current information required for [ArtistNavigationPickerDialog].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@HiltViewModel
class NavigationPickerViewModel @Inject constructor(private val musicRepository: MusicRepository) :
    ViewModel(), MusicRepository.UpdateListener {
    private val _currentArtistChoices = MutableStateFlow<ArtistNavigationChoices?>(null)
    /** The current set of [Artist] choices to show in the picker, or null if to show nothing. */
    val currentArtistChoices: StateFlow<PickerChoices<Artist>?>
        get() = _currentArtistChoices

    init {
        musicRepository.addUpdateListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        if (!changes.deviceLibrary) return
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        // Need to sanitize different items depending on the current set of choices.
        _currentArtistChoices.value =
            when (val choices = _currentArtistChoices.value) {
                is ArtistNavigationChoices.FromSong ->
                    deviceLibrary.findSong(choices.song.uid)?.let {
                        ArtistNavigationChoices.FromSong(it)
                    }
                is ArtistNavigationChoices.FromAlbum ->
                    deviceLibrary.findAlbum(choices.album.uid)?.let {
                        ArtistNavigationChoices.FromAlbum(it)
                    }
                else -> null
            }
    }

    override fun onCleared() {
        super.onCleared()
        musicRepository.removeUpdateListener(this)
    }

    /**
     * Set the [Music.UID] of the item to show artist choices for.
     *
     * @param uid The [Music.UID] of the item to show. Must be a [Song] or [Album].
     */
    fun setArtistChoiceUid(uid: Music.UID) {
        // Support Songs and Albums, which have parent artists.
        _currentArtistChoices.value =
            when (val music = musicRepository.find(uid)) {
                is Song -> ArtistNavigationChoices.FromSong(music)
                is Album -> ArtistNavigationChoices.FromAlbum(music)
                else -> null
            }
    }

    private sealed interface ArtistNavigationChoices : PickerChoices<Artist> {
        data class FromSong(val song: Song) : ArtistNavigationChoices {
            override val choices = song.artists
        }

        data class FromAlbum(val album: Album) : ArtistNavigationChoices {
            override val choices = album.artists
        }
    }
}
