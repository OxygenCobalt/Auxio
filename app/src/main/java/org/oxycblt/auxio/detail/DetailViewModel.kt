/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio.detail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.R
import org.oxycblt.auxio.detail.recycler.DiscHeader
import org.oxycblt.auxio.detail.recycler.SortHeader
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.Header
import org.oxycblt.auxio.ui.Item
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * ViewModel that stores data for the [DetailFragment]s. This includes:
 * - What item the fragment should be showing
 * - The RecyclerView data for each fragment
 * - Menu triggers for each fragment
 * @author OxygenCobalt
 */
class DetailViewModel : ViewModel() {
    private val musicStore = MusicStore.getInstance()
    private val settingsManager = SettingsManager.getInstance()

    private val _currentAlbum = MutableStateFlow<Album?>(null)
    val currentAlbum: StateFlow<Album?>
        get() = _currentAlbum

    private val _albumData = MutableStateFlow(listOf<Item>())
    val albumData: StateFlow<List<Item>>
        get() = _albumData

    var albumSort: Sort
        get() = settingsManager.detailAlbumSort
        set(value) {
            settingsManager.detailAlbumSort = value
            currentAlbum.value?.let(::refreshAlbumData)
        }

    private val _currentArtist = MutableStateFlow<Artist?>(null)
    val currentArtist: StateFlow<Artist?>
        get() = _currentArtist

    private val _artistData = MutableStateFlow(listOf<Item>())
    val artistData: StateFlow<List<Item>> = _artistData

    var artistSort: Sort
        get() = settingsManager.detailArtistSort
        set(value) {
            settingsManager.detailArtistSort = value
            currentArtist.value?.let(::refreshArtistData)
        }

    private val _currentGenre = MutableStateFlow<Genre?>(null)
    val currentGenre: StateFlow<Genre?>
        get() = _currentGenre

    private val _genreData = MutableStateFlow(listOf<Item>())
    val genreData: StateFlow<List<Item>> = _genreData

    var genreSort: Sort
        get() = settingsManager.detailGenreSort
        set(value) {
            settingsManager.detailGenreSort = value
            currentGenre.value?.let(::refreshGenreData)
        }

    fun setAlbumId(id: Long) {
        if (_currentAlbum.value?.id == id) return
        val library = unlikelyToBeNull(musicStore.library)
        val album =
            requireNotNull(library.albums.find { it.id == id }) { "Invalid album id provided " }

        _currentAlbum.value = album
        refreshAlbumData(album)
    }

    fun setArtistId(id: Long) {
        if (_currentArtist.value?.id == id) return
        val library = unlikelyToBeNull(musicStore.library)
        val artist =
            requireNotNull(library.artists.find { it.id == id }) { "Invalid artist id provided" }
        _currentArtist.value = artist
        refreshArtistData(artist)
    }

    fun setGenreId(id: Long) {
        if (_currentGenre.value?.id == id) return
        val library = unlikelyToBeNull(musicStore.library)
        val genre =
            requireNotNull(library.genres.find { it.id == id }) { "Invalid genre id provided" }
        _currentGenre.value = genre
        refreshGenreData(genre)
    }

    private fun refreshAlbumData(album: Album) {
        logD("Refreshing album data")
        val data = mutableListOf<Item>(album)
        data.add(SortHeader(id = -2, R.string.lbl_songs))

        // To create a good user experience regarding disc numbers, we intersperse
        // items that show the disc number throughout the album's songs. In the case
        // that the  album does not have disc numbers, we omit the header.
        val songs = albumSort.songs(album.songs)
        val byDisc = songs.groupBy { it.disc ?: 1 }
        if (byDisc.size > 1) {
            for (entry in byDisc.entries) {
                val disc = entry.key
                val discSongs = entry.value
                data.add(DiscHeader(id = -2L - disc, disc)) // Ensure ID uniqueness
                data.addAll(discSongs)
            }
        } else {
            data.addAll(songs)
        }

        _albumData.value = data
    }

    private fun refreshArtistData(artist: Artist) {
        logD("Refreshing artist data")
        val data = mutableListOf<Item>(artist)
        data.add(Header(-2, R.string.lbl_albums))
        data.addAll(Sort.ByYear(false).albums(artist.albums))
        data.add(SortHeader(-3, R.string.lbl_songs))
        data.addAll(artistSort.songs(artist.songs))
        _artistData.value = data.toList()
    }

    private fun refreshGenreData(genre: Genre) {
        logD("Refreshing genre data")
        val data = mutableListOf<Item>(genre)
        data.add(SortHeader(-2, R.string.lbl_songs))
        data.addAll(genreSort.songs(genre.songs))
        _genreData.value = data
    }
}
