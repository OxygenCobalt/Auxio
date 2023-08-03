/*
 * Copyright (c) 2023 Auxio Project
 * ListViewModel.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list

import androidx.annotation.MenuRes
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.list.menu.Menu
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaySong
import org.oxycblt.auxio.util.Event
import org.oxycblt.auxio.util.MutableEvent
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

/**
 * A [ViewModel] that orchestrates menu dialogs and selection state.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@HiltViewModel
class ListViewModel
@Inject
constructor(private val listSettings: ListSettings, private val musicRepository: MusicRepository) :
    ViewModel(), MusicRepository.UpdateListener {
    private val _selected = MutableStateFlow(listOf<Music>())
    /** The currently selected items. These are ordered in earliest selected and latest selected. */
    val selected: StateFlow<List<Music>>
        get() = _selected

    private val _menu = MutableEvent<Menu>()
    /**
     * A [Menu] command that is awaiting a view capable of responding to it. Null if none currently.
     */
    val menu: Event<Menu> = _menu

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
    fun peekSelection() =
        _selected.value.flatMap {
            when (it) {
                is Song -> listOf(it)
                is Album -> listSettings.albumSongSort.songs(it.songs)
                is Artist -> listSettings.artistSongSort.songs(it.songs)
                is Genre -> listSettings.genreSongSort.songs(it.songs)
                is Playlist -> it.songs
            }
        }

    /**
     * Clear the current selection and return it.
     *
     * @return A list of [Song]s collated from each item selected.
     */
    fun takeSelection(): List<Song> {
        logD("Taking selection")
        return peekSelection().also { _selected.value = listOf() }
    }

    /**
     * Clear the current selection.
     *
     * @return true if the prior selection was non-empty, false otherwise.
     */
    fun dropSelection(): Boolean {
        logD("Dropping selection [empty=${_selected.value.isEmpty()}]")
        return _selected.value.isNotEmpty().also { _selected.value = listOf() }
    }

    /**
     * Open a menu for a [Song]. This is not a popup menu, instead actually a dialog of menu options
     * with additional information.
     *
     * @param menuRes The resource of the menu to use.
     * @param song The [Song] to show.
     * @param playWith A [PlaySong] command to give context to what "Play" and "Shuffle" actions
     *   should do.
     */
    fun openMenu(@MenuRes menuRes: Int, song: Song, playWith: PlaySong) {
        logD("Opening menu for $song")
        openImpl(Menu.ForSong(menuRes, song, playWith))
    }

    /**
     * Open a menu for a [Album]. This is not a popup menu, instead actually a dialog of menu
     * options with additional information.
     *
     * @param menuRes The resource of the menu to use.
     * @param album The [Album] to show.
     */
    fun openMenu(@MenuRes menuRes: Int, album: Album) {
        logD("Opening menu for $album")
        openImpl(Menu.ForAlbum(menuRes, album))
    }

    /**
     * Open a menu for a [Artist]. This is not a popup menu, instead actually a dialog of menu
     * options with additional information.
     *
     * @param menuRes The resource of the menu to use.
     * @param artist The [Artist] to show.
     */
    fun openMenu(@MenuRes menuRes: Int, artist: Artist) {
        logD("Opening menu for $artist")
        openImpl(Menu.ForArtist(menuRes, artist))
    }

    /**
     * Open a menu for a [Genre]. This is not a popup menu, instead actually a dialog of menu
     * options with additional information.
     *
     * @param menuRes The resource of the menu to use.
     * @param genre The [Genre] to show.
     */
    fun openMenu(@MenuRes menuRes: Int, genre: Genre) {
        logD("Opening menu for $genre")
        openImpl(Menu.ForGenre(menuRes, genre))
    }

    /**
     * Open a menu for a [Playlist]. This is not a popup menu, instead actually a dialog of menu
     * options with additional information.
     *
     * @param menuRes The resource of the menu to use.
     * @param playlist The [Playlist] to show.
     */
    fun openMenu(@MenuRes menuRes: Int, playlist: Playlist) {
        logD("Opening menu for $playlist")
        openImpl(Menu.ForPlaylist(menuRes, playlist))
    }

    /**
     * Open a menu for a [Song] selection. This is not a popup menu, instead actually a dialog of
     * menu options with additional information.
     *
     * @param menuRes The resource of the menu to use.
     * @param songs The [Song] selection to show.
     */
    fun openMenu(@MenuRes menuRes: Int, songs: List<Song>) {
        logD("Opening menu for ${songs.size} songs")
        openImpl(Menu.ForSelection(menuRes, songs))
    }

    private fun openImpl(menu: Menu) {
        val existing = _menu.flow.value
        if (existing != null) {
            logW("Already opening $existing, ignoring $menu")
            return
        }
        _menu.put(menu)
    }
}
