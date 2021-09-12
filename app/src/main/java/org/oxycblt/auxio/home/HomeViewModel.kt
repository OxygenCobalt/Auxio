/*
 * Copyright (c) 2021 Auxio Project
 * HomeViewModel.kt is part of Auxio.
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

package org.oxycblt.auxio.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.SortMode

/**
 * The ViewModel for managing [HomeFragment]'s data and sorting modes.
 */
class HomeViewModel : ViewModel() {
    private val mSongs = MutableLiveData(listOf<Song>())
    val songs: LiveData<List<Song>> get() = mSongs

    private val mAlbums = MutableLiveData(listOf<Album>())
    val albums: LiveData<List<Album>> get() = mAlbums

    private val mArtists = MutableLiveData(listOf<Artist>())
    val artists: LiveData<List<Artist>> get() = mArtists

    private val mGenres = MutableLiveData(listOf<Genre>())
    val genres: LiveData<List<Genre>> get() = mGenres

    private val mTabs = MutableLiveData(
        arrayOf(
            DisplayMode.SHOW_SONGS, DisplayMode.SHOW_ALBUMS,
            DisplayMode.SHOW_ARTISTS, DisplayMode.SHOW_GENRES
        )
    )
    val tabs: LiveData<Array<DisplayMode>> = mTabs

    private val mCurTab = MutableLiveData(mTabs.value!![0])
    val curTab: LiveData<DisplayMode> = mCurTab

    var genreSortMode = SortMode.ASCENDING
        private set

    var artistSortMode = SortMode.ASCENDING
        private set

    var albumSortMode = SortMode.ASCENDING
        private set

    var songSortMode = SortMode.ASCENDING
        private set

    private val musicStore = MusicStore.getInstance()

    init {
        mSongs.value = songSortMode.sortSongs(musicStore.songs)
        mAlbums.value = albumSortMode.sortAlbums(musicStore.albums)
        mArtists.value = artistSortMode.sortModels(musicStore.artists)
        mGenres.value = genreSortMode.sortModels(musicStore.genres)
    }

    /**
     * Update the current tab based off of the new ViewPager position.
     */
    fun updateCurrentTab(pos: Int) {
        val mode = mTabs.value!![pos]

        mCurTab.value = mode
    }

    /**
     * Update the currently displayed item's [SortMode].
     */
    fun updateCurrentSort(sort: SortMode) {
        when (mCurTab.value) {
            DisplayMode.SHOW_SONGS -> {
                songSortMode = sort
                mSongs.value = sort.sortSongs(mSongs.value!!)
            }

            DisplayMode.SHOW_ALBUMS -> {
                albumSortMode = sort
                mAlbums.value = sort.sortAlbums(mAlbums.value!!)
            }
            DisplayMode.SHOW_ARTISTS -> {
                artistSortMode = sort
                mArtists.value = sort.sortModels(mArtists.value!!)
            }
            DisplayMode.SHOW_GENRES -> {
                genreSortMode = sort
                mGenres.value = sort.sortModels(mGenres.value!!)
            }
        }
    }
}
