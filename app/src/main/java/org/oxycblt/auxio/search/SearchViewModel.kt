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
import java.text.Normalizer
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
import org.oxycblt.auxio.util.context
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

    private fun searchImpl(library: Library, query: String): List<Item> {
        val sort = Sort(Sort.Mode.ByName, true)
        val filterMode = searchSettings.searchFilterMode
        val results = mutableListOf<Item>()

        // Note: A null filter mode maps to the "All" filter option, hence the check.

        if (filterMode == null || filterMode == MusicMode.ARTISTS) {
            library.artists.searchListImpl(query)?.let {
                results.add(Header(R.string.lbl_artists))
                results.addAll(sort.artists(it))
            }
        }

        if (filterMode == null || filterMode == MusicMode.ALBUMS) {
            library.albums.searchListImpl(query)?.let {
                results.add(Header(R.string.lbl_albums))
                results.addAll(sort.albums(it))
            }
        }

        if (filterMode == null || filterMode == MusicMode.GENRES) {
            library.genres.searchListImpl(query)?.let {
                results.add(Header(R.string.lbl_genres))
                results.addAll(sort.genres(it))
            }
        }

        if (filterMode == null || filterMode == MusicMode.SONGS) {
            library.songs
                .searchListImpl(query) { q, song -> song.path.name.contains(q) }
                ?.let {
                    results.add(Header(R.string.lbl_songs))
                    results.addAll(sort.songs(it))
                }
        }

        // Handle if we were canceled while searching.
        return results
    }

    /**
     * Search a given [Music] list.
     * @param query The query to search for. The routine will compare this query to the names of
     * each object in the list and
     * @param fallback Additional comparison code to run if the item does not match the query
     * initially. This can be used to compare against additional attributes to improve search result
     * quality.
     */
    private inline fun <T : Music> List<T>.searchListImpl(
        query: String,
        fallback: (String, T) -> Boolean = { _, _ -> false }
    ) =
        filter {
                // See if the plain resolved name matches the query. This works for most situations.
                val name = it.resolveName(context)
                if (name.contains(query, ignoreCase = true)) {
                    return@filter true
                }

                // See if the sort name matches. This can sometimes be helpful as certain libraries
                // will tag sort names to have a alphabetized version of the title.
                val sortName = it.rawSortName
                if (sortName != null && sortName.contains(query, ignoreCase = true)) {
                    return@filter true
                }

                // As a last-ditch effort, see if the normalized name matches. This will replace
                // any non-alphabetical characters with their alphabetical representations, which
                // could make it match the query.
                val normalizedName =
                    NORMALIZATION_SANITIZE_REGEX.replace(
                        Normalizer.normalize(name, Normalizer.Form.NFKD), "")
                if (normalizedName.contains(query, ignoreCase = true)) {
                    return@filter true
                }

                fallback(query, it)
            }
            .ifEmpty { null }

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
        /**
         * Converts the output of [Normalizer] to remove any junk characters added by it's
         * replacements.
         */
        val NORMALIZATION_SANITIZE_REGEX = Regex("\\p{InCombiningDiacriticalMarks}+")
    }
}
