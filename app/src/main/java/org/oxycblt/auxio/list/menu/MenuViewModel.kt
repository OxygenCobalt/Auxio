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

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.playback.PlaySong
import org.oxycblt.auxio.util.logW

/**
 * Manages the state information for [MenuDialogFragment] implementations.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@HiltViewModel
class MenuViewModel @Inject constructor(private val musicRepository: MusicRepository) :
    ViewModel(), MusicRepository.UpdateListener {
    private val _currentMenu = MutableStateFlow<Menu?>(null)
    /** The current [Menu] information being shown in a dialog. */
    val currentMenu: StateFlow<Menu?> = _currentMenu

    init {
        musicRepository.addUpdateListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        _currentMenu.value = _currentMenu.value?.let { unpackParcel(it.parcel) }
    }

    override fun onCleared() {
        musicRepository.removeUpdateListener(this)
    }

    fun setMenu(parcel: Menu.Parcel) {
        _currentMenu.value = unpackParcel(parcel)
        if (_currentMenu.value == null) {
            logW("Given menu parcel $parcel was invalid")
        }
    }

    private fun unpackParcel(parcel: Menu.Parcel) =
        when (parcel) {
            is Menu.ForSong.Parcel -> unpackSongParcel(parcel)
            is Menu.ForAlbum.Parcel -> unpackAlbumParcel(parcel)
            is Menu.ForArtist.Parcel -> unpackArtistParcel(parcel)
            is Menu.ForGenre.Parcel -> unpackGenreParcel(parcel)
            is Menu.ForPlaylist.Parcel -> unpackPlaylistParcel(parcel)
            is Menu.ForSelection.Parcel -> unpackSelectionParcel(parcel)
        }

    private fun unpackSongParcel(parcel: Menu.ForSong.Parcel): Menu.ForSong? {
        val song = musicRepository.deviceLibrary?.findSong(parcel.songUid) ?: return null
        val parent = parcel.playWithUid?.let(musicRepository::find) as MusicParent?
        val playWith = PlaySong.fromIntCode(parcel.playWithCode, parent) ?: return null
        return Menu.ForSong(parcel.res, song, playWith)
    }

    private fun unpackAlbumParcel(parcel: Menu.ForAlbum.Parcel): Menu.ForAlbum? {
        val album = musicRepository.deviceLibrary?.findAlbum(parcel.albumUid) ?: return null
        return Menu.ForAlbum(parcel.res, album)
    }

    private fun unpackArtistParcel(parcel: Menu.ForArtist.Parcel): Menu.ForArtist? {
        val artist = musicRepository.deviceLibrary?.findArtist(parcel.artistUid) ?: return null
        return Menu.ForArtist(parcel.res, artist)
    }

    private fun unpackGenreParcel(parcel: Menu.ForGenre.Parcel): Menu.ForGenre? {
        val genre = musicRepository.deviceLibrary?.findGenre(parcel.genreUid) ?: return null
        return Menu.ForGenre(parcel.res, genre)
    }

    private fun unpackPlaylistParcel(parcel: Menu.ForPlaylist.Parcel): Menu.ForPlaylist? {
        val playlist = musicRepository.userLibrary?.findPlaylist(parcel.playlistUid) ?: return null
        return Menu.ForPlaylist(parcel.res, playlist)
    }

    private fun unpackSelectionParcel(parcel: Menu.ForSelection.Parcel): Menu.ForSelection? {
        val deviceLibrary = musicRepository.deviceLibrary ?: return null
        val songs = parcel.songUids.mapNotNull(deviceLibrary::findSong)
        return Menu.ForSelection(parcel.res, songs)
    }
}
