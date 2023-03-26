/*
 * Copyright (c) 2023 Auxio Project
 * PlaybackPickerViewModel.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.picker

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.picker.PickerChoices

/**
 * A [ViewModel] that stores the choices shown in the playback picker dialogs.
 *
 * @author OxygenCobalt (Alexander Capehart)
 */
@HiltViewModel
class PlaybackPickerViewModel @Inject constructor(private val musicRepository: MusicRepository) :
    ViewModel(), MusicRepository.UpdateListener {
    private val _currentArtistChoices = MutableStateFlow<ArtistPlaybackChoices?>(null)
    /** The current set of [Artist] choices to show in the picker, or null if to show nothing. */
    val currentArtistChoices: StateFlow<ArtistPlaybackChoices?>
        get() = _currentArtistChoices

    private val _currentGenreChoices = MutableStateFlow<GenrePlaybackChoices?>(null)
    /** The current set of [Genre] choices to show in the picker, or null if to show nothing. */
    val currentGenreChoices: StateFlow<GenrePlaybackChoices?>
        get() = _currentGenreChoices

    init {
        musicRepository.addUpdateListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        if (!changes.deviceLibrary) return
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        _currentArtistChoices.value =
            _currentArtistChoices.value?.run {
                deviceLibrary.findSong(song.uid)?.let { newSong -> ArtistPlaybackChoices(newSong) }
            }
        _currentGenreChoices.value =
            _currentGenreChoices.value?.run {
                deviceLibrary.findSong(song.uid)?.let { newSong -> GenrePlaybackChoices(newSong) }
            }
    }

    override fun onCleared() {
        super.onCleared()
        musicRepository.removeUpdateListener(this)
    }

    /**
     * Set the [Music.UID] of the item to show [Artist] choices for.
     *
     * @param uid The [Music.UID] of the item to show. Must be a [Song].
     */
    fun setArtistChoiceUid(uid: Music.UID) {
        _currentArtistChoices.value =
            musicRepository.deviceLibrary?.findSong(uid)?.let { ArtistPlaybackChoices(it) }
    }

    /**
     * Set the [Music.UID] of the item to show [Genre] choices for.
     *
     * @param uid The [Music.UID] of the item to show. Must be a [Song].
     */
    fun setGenreChoiceUid(uid: Music.UID) {
        _currentGenreChoices.value =
            musicRepository.deviceLibrary?.findSong(uid)?.let { GenrePlaybackChoices(it) }
    }
}

data class ArtistPlaybackChoices(val song: Song) : PickerChoices<Artist> {
    override val choices = song.artists
}

data class GenrePlaybackChoices(val song: Song) : PickerChoices<Genre> {
    override val choices = song.genres
}
