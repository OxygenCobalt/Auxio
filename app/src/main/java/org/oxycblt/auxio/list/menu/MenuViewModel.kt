/*
 * Copyright (c) 2023 Auxio Project
 * MenuViewModel.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.menu

import androidx.annotation.MenuRes
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.Event
import org.oxycblt.auxio.util.MutableEvent
import org.oxycblt.auxio.util.logW

@HiltViewModel
class MenuViewModel @Inject constructor(private val musicRepository: MusicRepository) :
    ViewModel(), MusicRepository.UpdateListener {
    private val _pendingMenu = MutableEvent<PendingMenu>()
    val pendingMenu: Event<PendingMenu> = _pendingMenu

    private val _currentMusic = MutableStateFlow<Music?>(null)
    val currentMusic: StateFlow<Music?> = _currentMusic

    init {
        musicRepository.addUpdateListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        _currentMusic.value = _currentMusic.value?.let { musicRepository.find(it.uid) }
    }

    override fun onCleared() {
        musicRepository.removeUpdateListener(this)
    }

    fun openMenu(@MenuRes menuRes: Int, song: Song) =
        openMenuImpl(PendingMenu.ForSong(menuRes, song))

    fun openMenu(@MenuRes menuRes: Int, album: Album) =
        openMenuImpl(PendingMenu.ForAlbum(menuRes, album))

    fun openMenu(@MenuRes menuRes: Int, artist: Artist) =
        openMenuImpl(PendingMenu.ForArtist(menuRes, artist))

    fun openMenu(@MenuRes menuRes: Int, genre: Genre) =
        openMenuImpl(PendingMenu.ForGenre(menuRes, genre))

    fun openMenu(@MenuRes menuRes: Int, playlist: Playlist) =
        openMenuImpl(PendingMenu.ForPlaylist(menuRes, playlist))

    private fun openMenuImpl(pendingMenu: PendingMenu) {
        val existing = _pendingMenu.flow.value
        if (existing != null) {
            logW("Already opening $existing, ignoring $pendingMenu")
            return
        }
        _pendingMenu.put(pendingMenu)
    }

    fun setCurrentMenu(uid: Music.UID) {
        _currentMusic.value = musicRepository.find(uid)
        if (_currentMusic.value == null) {
            logW("Given Music UID to show was invalid")
        }
    }
}

sealed interface PendingMenu {
    val menuRes: Int
    val music: Music

    class ForSong(@MenuRes override val menuRes: Int, override val music: Song) : PendingMenu
    class ForAlbum(@MenuRes override val menuRes: Int, override val music: Album) : PendingMenu
    class ForArtist(@MenuRes override val menuRes: Int, override val music: Artist) : PendingMenu
    class ForGenre(@MenuRes override val menuRes: Int, override val music: Genre) : PendingMenu
    class ForPlaylist(@MenuRes override val menuRes: Int, override val music: Playlist) :
        PendingMenu
}
