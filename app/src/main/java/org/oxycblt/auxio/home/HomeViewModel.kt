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
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.oxycblt.auxio.home.tabs.Tab
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.Sort

/**
 * The ViewModel for managing [HomeFragment]'s data, sorting modes, and tab state.
 * @author OxygenCobalt
 */
class HomeViewModel : ViewModel(), SettingsManager.Callback {
    private val settingsManager = SettingsManager.getInstance()

    private val mSongs = MutableLiveData(listOf<Song>())
    val songs: LiveData<List<Song>> get() = mSongs

    private val mAlbums = MutableLiveData(listOf<Album>())
    val albums: LiveData<List<Album>> get() = mAlbums

    private val mArtists = MutableLiveData(listOf<Artist>())
    val artists: LiveData<List<Artist>> get() = mArtists

    private val mGenres = MutableLiveData(listOf<Genre>())
    val genres: LiveData<List<Genre>> get() = mGenres

    var tabs: List<DisplayMode> = visibleTabs
        private set

    /** Internal getter for getting the visible library tabs */
    private val visibleTabs: List<DisplayMode> get() = settingsManager.libTabs
        .filterIsInstance<Tab.Visible>().map { it.mode }

    private val mCurTab = MutableLiveData(tabs[0])
    val curTab: LiveData<DisplayMode> = mCurTab

    /**
     * Marker to recreate all library tabs, usually initiated by a settings change.
     * When this flag is set, all tabs (and their respective viewpager fragments) will be
     * recreated from scratch.
     */
    private val mRecreateTabs = MutableLiveData(false)
    val recreateTabs: LiveData<Boolean> = mRecreateTabs

    private val mFastScrolling = MutableLiveData(false)
    val fastScrolling: LiveData<Boolean> = mFastScrolling

    init {
        settingsManager.addCallback(this)

        viewModelScope.launch {
            val musicStore = MusicStore.awaitInstance()

            mSongs.value = settingsManager.libSongSort.sortSongs(musicStore.songs)
            mAlbums.value = settingsManager.libAlbumSort.sortAlbums(musicStore.albums)
            mArtists.value = settingsManager.libArtistSort.sortParents(musicStore.artists)
            mGenres.value = settingsManager.libGenreSort.sortParents(musicStore.genres)
        }
    }

    /**
     * Update the current tab based off of the new ViewPager position.
     */
    fun updateCurrentTab(pos: Int) {
        mCurTab.value = tabs[pos]
    }

    fun finishRecreateTabs() {
        mRecreateTabs.value = false
    }

    fun getSortForDisplay(displayMode: DisplayMode): Sort {
        return when (displayMode) {
            DisplayMode.SHOW_SONGS -> settingsManager.libSongSort
            DisplayMode.SHOW_ALBUMS -> settingsManager.libAlbumSort
            DisplayMode.SHOW_ARTISTS -> settingsManager.libArtistSort
            DisplayMode.SHOW_GENRES -> settingsManager.libGenreSort
        }
    }

    /**
     * Update the currently displayed item's [Sort].
     */
    fun updateCurrentSort(sort: Sort) {
        when (mCurTab.value) {
            DisplayMode.SHOW_SONGS -> {
                settingsManager.libSongSort = sort
                mSongs.value = sort.sortSongs(mSongs.value!!)
            }

            DisplayMode.SHOW_ALBUMS -> {
                settingsManager.libAlbumSort = sort
                mAlbums.value = sort.sortAlbums(mAlbums.value!!)
            }

            DisplayMode.SHOW_ARTISTS -> {
                settingsManager.libArtistSort = sort
                mArtists.value = sort.sortParents(mArtists.value!!)
            }

            DisplayMode.SHOW_GENRES -> {
                settingsManager.libGenreSort = sort
                mGenres.value = sort.sortParents(mGenres.value!!)
            }

            else -> {}
        }
    }

    /**
     * Update the fast scroll state. This is used to control the FAB visibility whenever
     * the user begins to fast scroll.
     */
    fun updateFastScrolling(scrolling: Boolean) {
        mFastScrolling.value = scrolling
    }

    // --- OVERRIDES ---

    override fun onLibTabsUpdate(libTabs: Array<Tab>) {
        tabs = visibleTabs
        mRecreateTabs.value = true
    }

    override fun onCleared() {
        super.onCleared()
        settingsManager.removeCallback(this)
    }
}
