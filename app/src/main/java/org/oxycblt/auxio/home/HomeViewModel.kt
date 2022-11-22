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

import android.app.Application
import androidx.collection.arraySetOf
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.getAndUpdate
import org.oxycblt.auxio.R
import org.oxycblt.auxio.home.tabs.Tab
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.Sort
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.application
import org.oxycblt.auxio.util.logD

/**
 * The ViewModel for managing [HomeFragment]'s data, sorting modes, and tab state.
 * @author OxygenCobalt
 */
class HomeViewModel(application: Application) :
    AndroidViewModel(application), Settings.Callback, MusicStore.Callback {
    private val musicStore = MusicStore.getInstance()
    private val settings = Settings(application, this)

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

    private val _selected = MutableStateFlow(listOf<Music>())
    val selected: StateFlow<List<Music>>
        get() = _selected

    var tabs: List<MusicMode> = visibleTabs
        private set

    /** Internal getter for getting the visible library tabs */
    private val visibleTabs: List<MusicMode>
        get() = settings.libTabs.filterIsInstance<Tab.Visible>().map { it.mode }

    private val _currentTab = MutableStateFlow(tabs[0])
    val currentTab: StateFlow<MusicMode> = _currentTab

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
    }

    /** Select a music item. */
    fun select(item: Music) {
        val items = _selected.value.toMutableList()
        if (items.remove(item)) {
            logD("Unselecting item $item")
            _selected.value = items
        } else {
            logD("Selecting item $item")
            items.add(item)
            _selected.value = items
        }
    }

    /** Update the current tab based off of the new ViewPager position. */
    fun updateCurrentTab(pos: Int) {
        logD("Updating current tab to ${tabs[pos]}")
        _currentTab.value = tabs[pos]
    }

    fun finishRecreateTabs() {
        _shouldRecreateTabs.value = false
    }

    /** Get the specific sort for the given [MusicMode]. */
    fun getSortForTab(tabMode: MusicMode): Sort =
        when (tabMode) {
            MusicMode.SONGS -> settings.libSongSort
            MusicMode.ALBUMS -> settings.libAlbumSort
            MusicMode.ARTISTS -> settings.libArtistSort
            MusicMode.GENRES -> settings.libGenreSort
        }

    /** Update the currently displayed item's [Sort]. */
    fun updateCurrentSort(sort: Sort) {
        logD("Updating ${_currentTab.value} sort to $sort")
        when (_currentTab.value) {
            MusicMode.SONGS -> {
                settings.libSongSort = sort
                _songs.value = sort.songs(_songs.value)
            }
            MusicMode.ALBUMS -> {
                settings.libAlbumSort = sort
                _albums.value = sort.albums(_albums.value)
            }
            MusicMode.ARTISTS -> {
                settings.libArtistSort = sort
                _artists.value = sort.artists(_artists.value)
            }
            MusicMode.GENRES -> {
                settings.libGenreSort = sort
                _genres.value = sort.genres(_genres.value)
            }
        }
    }

    /**
     * Update the fast scroll state. This is used to control the FAB visibility whenever the user
     * begins to fast scroll.
     */
    fun updateFastScrolling(scrolling: Boolean) {
        logD("Updating fast scrolling state: $scrolling")
        _isFastScrolling.value = scrolling
    }

    // --- OVERRIDES ---

    override fun onLibraryChanged(library: MusicStore.Library?) {
        if (library != null) {
            logD("Library changed, refreshing library")
            _songs.value = settings.libSongSort.songs(library.songs)
            _albums.value = settings.libAlbumSort.albums(library.albums)

            _artists.value =
                settings.libArtistSort.artists(
                    if (settings.shouldHideCollaborators) {
                        library.artists.filter { !it.isCollaborator }
                    } else {
                        library.artists
                    })

            _genres.value = settings.libGenreSort.genres(library.genres)
        }
    }

    override fun onSettingChanged(key: String) {
        if (key == application.getString(R.string.set_key_lib_tabs)) {
            tabs = visibleTabs
            _shouldRecreateTabs.value = true
        }

        if (key == application.getString(R.string.set_key_hide_collaborators)) {
            onLibraryChanged(musicStore.library)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicStore.removeCallback(this)
        settings.release()
    }
}
