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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.R
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

/**
 * ViewModel that stores data for the [DetailFragment]s. This includes:
 * - What item the fragment should be showing
 * - The RecyclerView data for each fragment
 * - Menu triggers for each fragment
 * @author OxygenCobalt
 */
class DetailViewModel : ViewModel() {
    private val settingsManager = SettingsManager.getInstance()

    private val mCurrentAlbum = MutableLiveData<Album?>()
    val currentAlbum: LiveData<Album?>
        get() = mCurrentAlbum

    private val mAlbumData = MutableLiveData(listOf<Item>())
    val albumData: LiveData<List<Item>>
        get() = mAlbumData

    var albumSort: Sort
        get() = settingsManager.detailAlbumSort
        set(value) {
            settingsManager.detailAlbumSort = value
            currentAlbum.value?.let(::refreshAlbumData)
        }

    private val mCurrentArtist = MutableLiveData<Artist?>()
    val currentArtist: LiveData<Artist?>
        get() = mCurrentArtist

    private val mArtistData = MutableLiveData(listOf<Item>())
    val artistData: LiveData<List<Item>> = mArtistData

    var artistSort: Sort
        get() = settingsManager.detailArtistSort
        set(value) {
            settingsManager.detailArtistSort = value
            currentArtist.value?.let(::refreshArtistData)
        }

    private val mCurrentGenre = MutableLiveData<Genre?>()
    val currentGenre: LiveData<Genre?>
        get() = mCurrentGenre

    private val mGenreData = MutableLiveData(listOf<Item>())
    val genreData: LiveData<List<Item>> = mGenreData

    var genreSort: Sort
        get() = settingsManager.detailGenreSort
        set(value) {
            settingsManager.detailGenreSort = value
            currentGenre.value?.let(::refreshGenreData)
        }

    fun setAlbumId(id: Long) {
        if (mCurrentAlbum.value?.id == id) return
        val musicStore = MusicStore.requireInstance()
        val album =
            requireNotNull(musicStore.albums.find { it.id == id }) { "Invalid album id provided " }

        mCurrentAlbum.value = album
        refreshAlbumData(album)
    }

    fun setArtistId(id: Long) {
        if (mCurrentArtist.value?.id == id) return
        val musicStore = MusicStore.requireInstance()
        val artist = requireNotNull(musicStore.artists.find { it.id == id }) {}
        mCurrentArtist.value = artist
        refreshArtistData(artist)
    }

    fun setGenreId(id: Long) {
        if (mCurrentGenre.value?.id == id) return
        val musicStore = MusicStore.requireInstance()
        val genre = requireNotNull(musicStore.genres.find { it.id == id })
        mCurrentGenre.value = genre
        refreshGenreData(genre)
    }

    private fun refreshGenreData(genre: Genre) {
        logD("Refreshing genre data")
        val data = mutableListOf<Item>(genre)
        data.add(SortHeader(-2, R.string.lbl_songs))
        data.addAll(genreSort.genre(genre))
        mGenreData.value = data
    }

    private fun refreshArtistData(artist: Artist) {
        logD("Refreshing artist data")
        val data = mutableListOf<Item>(artist)
        data.add(Header(-2, R.string.lbl_albums))
        data.addAll(Sort.ByYear(false).albums(artist.albums))
        data.add(SortHeader(-3, R.string.lbl_songs))
        data.addAll(artistSort.artist(artist))
        mArtistData.value = data.toList()
    }

    private fun refreshAlbumData(album: Album) {
        logD("Refreshing album data")
        val data = mutableListOf<Item>(album)
        data.add(SortHeader(id = -2, R.string.lbl_songs))
        data.addAll(albumSort.album(album))
        mAlbumData.value = data
    }
}
