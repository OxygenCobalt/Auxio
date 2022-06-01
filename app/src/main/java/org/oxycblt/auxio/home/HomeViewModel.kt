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
 
package org.oxycblt.auxio.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.home.tabs.Tab
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * The ViewModel for managing [HomeFragment]'s data, sorting modes, and tab state.
 * @author OxygenCobalt
 */
class HomeViewModel : ViewModel(), SettingsManager.Callback, MusicStore.Callback {
    private val musicStore = MusicStore.getInstance()
    private val settingsManager = SettingsManager.getInstance()

    private val _songs = MutableStateFlow(listOf<Song>())
    val songs: StateFlow<List<Song>>
        get() = _songs

    private val _albums = MutableStateFlow(listOf<Album>())
    val albums: StateFlow<List<Album>>
        get() = _albums

    private val _artists = MutableStateFlow(listOf<Artist>())
    val artists: MutableStateFlow<List<Artist>>
        get() = _artists

    private val _genres = MutableStateFlow(listOf<Genre>())
    val genres: StateFlow<List<Genre>>
        get() = _genres

    var tabs: List<DisplayMode> = visibleTabs
        private set

    /** Internal getter for getting the visible library tabs */
    private val visibleTabs: List<DisplayMode>
        get() = settingsManager.libTabs.filterIsInstance<Tab.Visible>().map { it.mode }

    private val _currentTab = MutableStateFlow(tabs[0])
    val currentTab: StateFlow<DisplayMode> = _currentTab

    /**
     * Marker to recreate all library tabs, usually initiated by a settings change. When this flag
     * is set, all tabs (and their respective viewpager fragments) will be recreated from scratch.
     */
    private val _shouldRecreateTabs = MutableStateFlow(false)
    val recreateTabs: StateFlow<Boolean> = _shouldRecreateTabs

    private val _isFastScrolling = MutableStateFlow(false)
    val isFastScrolling: StateFlow<Boolean> = _isFastScrolling

    init {
        musicStore.addCallback(this)
        settingsManager.addCallback(this)
    }

    /** Update the current tab based off of the new ViewPager position. */
    fun updateCurrentTab(pos: Int) {
        logD("Updating current tab to ${tabs[pos]}")
        _currentTab.value = tabs[pos]
    }

    fun finishRecreateTabs() {
        _shouldRecreateTabs.value = false
    }

    /** Get the specific sort for the given [DisplayMode]. */
    fun getSortForDisplay(displayMode: DisplayMode): Sort {
        return when (displayMode) {
            DisplayMode.SHOW_SONGS -> settingsManager.libSongSort
            DisplayMode.SHOW_ALBUMS -> settingsManager.libAlbumSort
            DisplayMode.SHOW_ARTISTS -> settingsManager.libArtistSort
            DisplayMode.SHOW_GENRES -> settingsManager.libGenreSort
        }
    }

    /** Update the currently displayed item's [Sort]. */
    fun updateCurrentSort(sort: Sort) {
        logD("Updating ${_currentTab.value} sort to $sort")
        when (_currentTab.value) {
            DisplayMode.SHOW_SONGS -> {
                settingsManager.libSongSort = sort
                _songs.value = sort.songs(unlikelyToBeNull(_songs.value))
            }
            DisplayMode.SHOW_ALBUMS -> {
                settingsManager.libAlbumSort = sort
                _albums.value = sort.albums(unlikelyToBeNull(_albums.value))
            }
            DisplayMode.SHOW_ARTISTS -> {
                settingsManager.libArtistSort = sort
                _artists.value = sort.artists(unlikelyToBeNull(_artists.value))
            }
            DisplayMode.SHOW_GENRES -> {
                settingsManager.libGenreSort = sort
                _genres.value = sort.genres(unlikelyToBeNull(_genres.value))
            }
        }
    }

    /**
     * Update the fast scroll state. This is used to control the FAB visibility whenever the user
     * begins to fast scroll.
     */
    fun updateFastScrolling(scrolling: Boolean) {
        _isFastScrolling.value = scrolling
    }

    // --- OVERRIDES ---

    override fun onMusicUpdate(response: MusicStore.Response) {
        if (response is MusicStore.Response.Ok) {
            val library = response.library
            _songs.value = settingsManager.libSongSort.songs(library.songs)
            _albums.value = settingsManager.libAlbumSort.albums(library.albums)
            _artists.value = settingsManager.libArtistSort.artists(library.artists)
            _genres.value = settingsManager.libGenreSort.genres(library.genres)
        }
    }

    override fun onLibrarySettingsChanged() {
        tabs = visibleTabs
        _shouldRecreateTabs.value = true
    }

    override fun onCleared() {
        super.onCleared()
        musicStore.removeCallback(this)
        settingsManager.removeCallback(this)
    }
}
