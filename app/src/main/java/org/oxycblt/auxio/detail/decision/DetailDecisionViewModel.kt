/*
 * Copyright (c) 2023 Auxio Project
 * DetailDecisionViewModel.kt is part of Auxio.
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
 
package org.oxycblt.auxio.detail.decision

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.device.DeviceLibrary
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

/**
 * A [ViewModel] that stores choice information for [ShowArtistDialog], and possibly others in the
 * future.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@HiltViewModel
class DetailPickerViewModel @Inject constructor(private val musicRepository: MusicRepository) :
    ViewModel(), MusicRepository.UpdateListener {
    private val _artistChoices = MutableStateFlow<ArtistShowChoices?>(null)
    /** The current set of [Artist] choices to show in the picker, or null if to show nothing. */
    val artistChoices: StateFlow<ArtistShowChoices?>
        get() = _artistChoices

    init {
        musicRepository.addUpdateListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        musicRepository.removeUpdateListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        if (!changes.deviceLibrary) return
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        // Need to sanitize different items depending on the current set of choices.
        _artistChoices.value = _artistChoices.value?.sanitize(deviceLibrary)
        logD("Updated artist choices: ${_artistChoices.value}")
    }

    /**
     * Set the [Music.UID] of the item to show artist choices for.
     *
     * @param itemUid The [Music.UID] of the item to show. Must be a [Song] or [Album].
     */
    fun setArtistChoiceUid(itemUid: Music.UID) {
        logD("Opening navigation choices for $itemUid")
        // Support Songs and Albums, which have parent artists.
        _artistChoices.value =
            when (val music = musicRepository.find(itemUid)) {
                is Song -> {
                    logD("Creating navigation choices for song")
                    ArtistShowChoices.FromSong(music)
                }
                is Album -> {
                    logD("Creating navigation choices for album")
                    ArtistShowChoices.FromAlbum(music)
                }
                else -> {
                    logW("Given song/album UID was invalid")
                    null
                }
            }
    }
}

/**
 * The current list of choices to show in the artist navigation picker dialog.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed interface ArtistShowChoices {
    /** The UID of the item. */
    val uid: Music.UID
    /** The current [Artist] choices. */
    val choices: List<Artist>
    /** Sanitize this instance with a [DeviceLibrary]. */
    fun sanitize(newLibrary: DeviceLibrary): ArtistShowChoices?

    /** Backing implementation of [ArtistShowChoices] that is based on a [Song]. */
    class FromSong(val song: Song) : ArtistShowChoices {
        override val uid = song.uid
        override val choices = song.artists

        override fun sanitize(newLibrary: DeviceLibrary) =
            newLibrary.findSong(uid)?.let { FromSong(it) }
    }

    /** Backing implementation of [ArtistShowChoices] that is based on an [Album]. */
    data class FromAlbum(val album: Album) : ArtistShowChoices {
        override val uid = album.uid
        override val choices = album.artists

        override fun sanitize(newLibrary: DeviceLibrary) =
            newLibrary.findAlbum(uid)?.let { FromAlbum(it) }
    }
}
