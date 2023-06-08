/*
 * Copyright (c) 2022 Auxio Project
 * SelectionViewModel.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.selection

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.MusicSettings
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.logD

/**
 * A [ViewModel] that manages the current selection.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@HiltViewModel
class SelectionViewModel
@Inject
constructor(
    private val musicRepository: MusicRepository,
    private val musicSettings: MusicSettings
) : ViewModel(), MusicRepository.UpdateListener {
    private val _selected = MutableStateFlow(listOf<Music>())
    /** the currently selected items. These are ordered in earliest selected and latest selected. */
    val selected: StateFlow<List<Music>>
        get() = _selected

    init {
        musicRepository.addUpdateListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        val deviceLibrary = musicRepository.deviceLibrary ?: return
        val userLibrary = musicRepository.userLibrary ?: return
        // Sanitize the selection to remove items that no longer exist and thus
        // won't appear in any list.
        _selected.value =
            _selected.value.mapNotNull {
                when (it) {
                    is Song -> deviceLibrary.findSong(it.uid)
                    is Album -> deviceLibrary.findAlbum(it.uid)
                    is Artist -> deviceLibrary.findArtist(it.uid)
                    is Genre -> deviceLibrary.findGenre(it.uid)
                    is Playlist -> userLibrary.findPlaylist(it.uid)
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        musicRepository.removeUpdateListener(this)
    }

    /**
     * Select a new [Music] item. If this item is already within the selected items, the item will
     * be removed. Otherwise, it will be added.
     *
     * @param music The [Music] item to select.
     */
    fun select(music: Music) {
        if (music is MusicParent && music.songs.isEmpty()) {
            logD("Cannot select empty parent, ignoring operation")
            return
        }

        val selected = _selected.value.toMutableList()
        if (!selected.remove(music)) {
            logD("Adding $music to selection")
            selected.add(music)
        } else {
            logD("Removed $music from selection")
        }

        _selected.value = selected
    }

    /**
     * Clear the current selection and return it.
     *
     * @return A list of [Song]s collated from each item selected.
     */
    fun take(): List<Song> {
        logD("Taking selection")
        return _selected.value
            .flatMap {
                when (it) {
                    is Song -> listOf(it)
                    is Album -> musicSettings.albumSongSort.songs(it.songs)
                    is Artist -> musicSettings.artistSongSort.songs(it.songs)
                    is Genre -> musicSettings.genreSongSort.songs(it.songs)
                    is Playlist -> it.songs
                }
            }
            .also { _selected.value = listOf() }
    }

    /**
     * Clear the current selection.
     *
     * @return true if the prior selection was non-empty, false otherwise.
     */
    fun drop(): Boolean {
        logD("Dropping selection [empty=${_selected.value.isEmpty()}]")
        return _selected.value.isNotEmpty().also { _selected.value = listOf() }
    }
}
