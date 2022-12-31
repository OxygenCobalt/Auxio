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
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.R
import org.oxycblt.auxio.home.tabs.Tab
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.Sort
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.logD

/**
 * The ViewModel for managing the tab data and lists of the home view.
 * @author Alexander Capehart (OxygenCobalt)
 */
class HomeViewModel(application: Application) :
    AndroidViewModel(application),
    MusicStore.Listener,
    SharedPreferences.OnSharedPreferenceChangeListener {
    private val musicStore = MusicStore.getInstance()
    private val settings = Settings(application)

    private val _songsList = MutableStateFlow(listOf<Song>())
    /** A list of [Song]s, sorted by the preferred [Sort], to be shown in the home view. */
    val songLists: StateFlow<List<Song>>
        get() = _songsList

    private val _albumsLists = MutableStateFlow(listOf<Album>())
    /** A list of [Album]s, sorted by the preferred [Sort], to be shown in the home view. */
    val albumsList: StateFlow<List<Album>>
        get() = _albumsLists

    private val _artistsList = MutableStateFlow(listOf<Artist>())
    /**
     * A list of [Artist]s, sorted by the preferred [Sort], to be shown in the home view. Note that
     * if "Hide collaborators" is on, this list will not include [Artist]s where
     * [Artist.isCollaborator] is true.
     */
    val artistsList: MutableStateFlow<List<Artist>>
        get() = _artistsList

    private val _genresList = MutableStateFlow(listOf<Genre>())
    /** A list of [Genre]s, sorted by the preferred [Sort], to be shown in the home view. */
    val genresList: StateFlow<List<Genre>>
        get() = _genresList

    /**
     * A list of [MusicMode] corresponding to the current [Tab] configuration, excluding invisible
     * [Tab]s.
     */
    var currentTabModes: List<MusicMode> = makeTabModes()
        private set

    private val _currentTabMode = MutableStateFlow(currentTabModes[0])
    /** The [MusicMode] of the currently shown [Tab]. */
    val currentTabMode: StateFlow<MusicMode> = _currentTabMode

    private val _shouldRecreate = MutableStateFlow(false)
    /**
     * A marker to re-create all library tabs, usually initiated by a settings change. When this
     * flag is true, all tabs (and their respective ViewPager2 fragments) will be re-created from
     * scratch.
     */
    val shouldRecreate: StateFlow<Boolean> = _shouldRecreate

    private val _isFastScrolling = MutableStateFlow(false)
    /** A marker for whether the user is fast-scrolling in the home view or not. */
    val isFastScrolling: StateFlow<Boolean> = _isFastScrolling

    init {
        musicStore.addListener(this)
        settings.addListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        musicStore.removeListener(this)
        settings.removeListener(this)
    }

    override fun onLibraryChanged(library: MusicStore.Library?) {
        if (library != null) {
            logD("Library changed, refreshing library")
            // Get the each list of items in the library to use as our list data.
            // Applying the preferred sorting to them.
            _songsList.value = settings.libSongSort.songs(library.songs)
            _albumsLists.value = settings.libAlbumSort.albums(library.albums)
            _artistsList.value =
                settings.libArtistSort.artists(
                    if (settings.shouldHideCollaborators) {
                        // Hide Collaborators is enabled, filter out collaborators.
                        library.artists.filter { !it.isCollaborator }
                    } else {
                        library.artists
                    })
            _genresList.value = settings.libGenreSort.genres(library.genres)
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            context.getString(R.string.set_key_lib_tabs) -> {
                // Tabs changed, update  the current tabs and set up a re-create event.
                currentTabModes = makeTabModes()
                _shouldRecreate.value = true
            }
            context.getString(R.string.set_key_hide_collaborators) -> {
                // Changes in the hide collaborator setting will change the artist contents
                // of the library, consider it a library update.
                onLibraryChanged(musicStore.library)
            }
        }
    }

    /**
     * Update [currentTabMode] to reflect a new ViewPager2 position
     * @param pagerPos The new position of the ViewPager2 instance.
     */
    fun synchronizeTabPosition(pagerPos: Int) {
        logD("Updating current tab to ${currentTabModes[pagerPos]}")
        _currentTabMode.value = currentTabModes[pagerPos]
    }

    /**
     * Mark the recreation process as complete.
     * @see shouldRecreate
     */
    fun finishRecreate() {
        _shouldRecreate.value = false
    }

    /**
     * Get the preferred [Sort] for a given [Tab].
     * @param tabMode The [MusicMode] of the [Tab] desired.
     * @return The [Sort] preferred for that [Tab]
     */
    fun getSortForTab(tabMode: MusicMode) =
        when (tabMode) {
            MusicMode.SONGS -> settings.libSongSort
            MusicMode.ALBUMS -> settings.libAlbumSort
            MusicMode.ARTISTS -> settings.libArtistSort
            MusicMode.GENRES -> settings.libGenreSort
        }

    /**
     * Update the preferred [Sort] for the current [Tab]. Will update corresponding list.
     * @param sort The new [Sort] to apply. Assumed to be an allowed sort for the current [Tab].
     */
    fun setSortForCurrentTab(sort: Sort) {
        logD("Updating ${_currentTabMode.value} sort to $sort")
        // Can simply re-sort the current list of items without having to access the library.
        when (_currentTabMode.value) {
            MusicMode.SONGS -> {
                settings.libSongSort = sort
                _songsList.value = sort.songs(_songsList.value)
            }
            MusicMode.ALBUMS -> {
                settings.libAlbumSort = sort
                _albumsLists.value = sort.albums(_albumsLists.value)
            }
            MusicMode.ARTISTS -> {
                settings.libArtistSort = sort
                _artistsList.value = sort.artists(_artistsList.value)
            }
            MusicMode.GENRES -> {
                settings.libGenreSort = sort
                _genresList.value = sort.genres(_genresList.value)
            }
        }
    }

    /**
     * Update whether the user is fast scrolling or not in the home view.
     * @param isFastScrolling true if the user is currently fast scrolling, false otherwise.
     */
    fun setFastScrolling(isFastScrolling: Boolean) {
        logD("Updating fast scrolling state: $isFastScrolling")
        _isFastScrolling.value = isFastScrolling
    }

    /**
     * Create a list of [MusicMode]s representing a simpler version of the [Tab] configuration.
     * @return A list of the [MusicMode]s for each visible [Tab] in the configuration, ordered in
     * the same way as the configuration.
     */
    private fun makeTabModes() = settings.libTabs.filterIsInstance<Tab.Visible>().map { it.mode }
}
