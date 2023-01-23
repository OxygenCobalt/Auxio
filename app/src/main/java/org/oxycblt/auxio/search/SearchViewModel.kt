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
 
package org.oxycblt.auxio.search

import android.app.Application
import androidx.annotation.IdRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.Header
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.library.Library
import org.oxycblt.auxio.music.library.Sort
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.util.logD

/**
 * An [AndroidViewModel] that keeps performs search operations and tracks their results.
 * @author Alexander Capehart (OxygenCobalt)
 */
class SearchViewModel(application: Application) :
    AndroidViewModel(application), MusicStore.Listener {
    private val musicStore = MusicStore.getInstance()
    private val searchSettings = SearchSettings.from(application)
    private val playbackSettings = PlaybackSettings.from(application)
    private var searchEngine = SearchEngine.from(application)
    private var lastQuery: String? = null
    private var currentSearchJob: Job? = null

    private val _searchResults = MutableStateFlow(listOf<Item>())
    /** The results of the last [search] call, if any. */
    val searchResults: StateFlow<List<Item>>
        get() = _searchResults

    /** The [MusicMode] to use when playing a [Song] from the UI. */
    val playbackMode: MusicMode
        get() = playbackSettings.inListPlaybackMode

    init {
        musicStore.addListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        musicStore.removeListener(this)
    }

    override fun onLibraryChanged(library: Library?) {
        if (library != null) {
            // Make sure our query is up to date with the music library.
            search(lastQuery)
        }
    }

    /**
     * Asynchronously search the music library. Results will be pushed to [searchResults]. Will
     * cancel any previous search operations started prior.
     * @param query The query to search the music library for.
     */
    fun search(query: String?) {
        // Cancel the previous background search.
        currentSearchJob?.cancel()
        lastQuery = query

        val library = musicStore.library
        if (query.isNullOrEmpty() || library == null) {
            logD("Search query is not applicable.")
            _searchResults.value = listOf()
            return
        }

        logD("Searching music library for $query")

        // Searching is time-consuming, so do it in the background.
        currentSearchJob =
            viewModelScope.launch {
                _searchResults.value = searchImpl(library, query).also { yield() }
            }
    }

    private suspend fun searchImpl(library: Library, query: String): List<Item> {
        val filterMode = searchSettings.searchFilterMode

        val items =
            if (filterMode == null) {
                // A nulled filter mode means to not filter anything.
                SearchEngine.Items(library.songs, library.albums, library.artists, library.genres)
            } else {
                SearchEngine.Items(
                    songs = if (filterMode == MusicMode.SONGS) library.songs else null,
                    albums = if (filterMode == MusicMode.ALBUMS) library.albums else null,
                    artists = if (filterMode == MusicMode.ARTISTS) library.artists else null,
                    genres = if (filterMode == MusicMode.GENRES) library.genres else null)
            }

        val results = searchEngine.search(items, query)

        return buildList {
            results.artists?.let { artists ->
                add(Header(R.string.lbl_artists))
                addAll(SORT.artists(artists))
            }
            results.albums?.let { albums ->
                add(Header(R.string.lbl_albums))
                addAll(SORT.albums(albums))
            }
            results.genres?.let { genres ->
                add(Header(R.string.lbl_genres))
                addAll(SORT.genres(genres))
            }
            results.songs?.let { songs ->
                add(Header(R.string.lbl_songs))
                addAll(SORT.songs(songs))
            }
        }
    }

    /**
     * Returns the ID of the filter option to currently highlight.
     * @return A menu item ID of the filtering option selected.
     */
    @IdRes
    fun getFilterOptionId() =
        when (searchSettings.searchFilterMode) {
            MusicMode.SONGS -> R.id.option_filter_songs
            MusicMode.ALBUMS -> R.id.option_filter_albums
            MusicMode.ARTISTS -> R.id.option_filter_artists
            MusicMode.GENRES -> R.id.option_filter_genres
            // Null maps to filtering nothing.
            null -> R.id.option_filter_all
        }

    /**
     * Update the filter mode with the newly-selected filter option.
     * @return A menu item ID of the new filtering option selected.
     */
    fun setFilterOptionId(@IdRes id: Int) {
        val newFilterMode =
            when (id) {
                R.id.option_filter_songs -> MusicMode.SONGS
                R.id.option_filter_albums -> MusicMode.ALBUMS
                R.id.option_filter_artists -> MusicMode.ARTISTS
                R.id.option_filter_genres -> MusicMode.GENRES
                // Null maps to filtering nothing.
                R.id.option_filter_all -> null
                else -> error("Invalid option ID provided")
            }
        logD("Updating filter mode to $newFilterMode")
        searchSettings.searchFilterMode = newFilterMode
        search(lastQuery)
    }

    private companion object {
        val SORT = Sort(Sort.Mode.ByName, true)
    }
}
