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
import androidx.lifecycle.ViewModel
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
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicMode
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.Sort
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.logD

/**
 * An [AndroidViewModel] that keeps performs search operations and tracks their results.
 * @author Alexander Capehart (OxygenCobalt)
 */
class SearchViewModel(application: Application) :
    AndroidViewModel(application), MusicStore.Callback {
    private val musicStore = MusicStore.getInstance()
    private val settings = Settings(context)
    private var lastQuery: String? = null
    private var currentSearchJob: Job? = null

    private val _searchResults = MutableStateFlow(listOf<Item>())
    /** The results of the last [search] call, if any. */
    val searchResults: StateFlow<List<Item>>
        get() = _searchResults

    init {
        musicStore.addCallback(this)
    }

    override fun onCleared() {
        super.onCleared()
        musicStore.removeCallback(this)
    }

    override fun onLibraryChanged(library: MusicStore.Library?) {
        if (library != null) {
            // Make sure our query is up to date with the music library.
            search(lastQuery)
        }
    }

    /**
     * Use [query] to perform a search of the music library. Will push results to [searchResults].
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

    private fun searchImpl(library: MusicStore.Library, query: String): List<Item> {
        val sort = Sort(Sort.Mode.ByName, true)
        val filterMode = settings.searchFilterMode
        val results = mutableListOf<Item>()

        // Note: A null filter mode maps to the "All" filter option, hence the check.

        if (filterMode == null || filterMode == MusicMode.ARTISTS) {
            library.artists.filterArtistsBy(query)?.let { artists ->
                results.add(Header(R.string.lbl_artists))
                results.addAll(sort.artists(artists))
            }
        }

        if (filterMode == null || filterMode == MusicMode.ALBUMS) {
            library.albums.filterAlbumsBy(query)?.let { albums ->
                results.add(Header(R.string.lbl_albums))
                results.addAll(sort.albums(albums))
            }
        }

        if (filterMode == null || filterMode == MusicMode.GENRES) {
            library.genres.filterGenresBy(query)?.let { genres ->
                results.add(Header(R.string.lbl_genres))
                results.addAll(sort.genres(genres))
            }
        }

        if (filterMode == null || filterMode == MusicMode.SONGS) {
            library.songs.filterSongsBy(query)?.let { songs ->
                results.add(Header(R.string.lbl_songs))
                results.addAll(sort.songs(songs))
            }
        }

        // Handle if we were canceled while searching.
        return results
    }

    private fun List<Song>.filterSongsBy(value: String) =
        searchListImpl(value) {
            // Include both the sort name (can have normalized versions of titles) and
            // file name (helpful for poorly tagged songs) to the filtering.
            it.rawSortName?.contains(value, ignoreCase = true) == true ||
                it.path.name.contains(value)
        }

    private fun List<Album>.filterAlbumsBy(value: String) =
        // Include the sort name (can have normalized versions of names) to the filtering.
        searchListImpl(value) { it.rawSortName?.contains(value, ignoreCase = true) == true }

    private fun List<Artist>.filterArtistsBy(value: String) =
        // Include the sort name (can have normalized versions of names) to the filtering.
        searchListImpl(value) { it.rawSortName?.contains(value, ignoreCase = true) == true }

    private fun List<Genre>.filterGenresBy(value: String) = searchListImpl(value) { false }

    private inline fun <T : Music> List<T>.searchListImpl(query: String, fallback: (T) -> Boolean) =
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
            val normalizedName = NORMALIZATION_SANITIZE_REGEX.replace(
                Normalizer.normalize(name, Normalizer.Form.NFKD), "")
            if (normalizedName.contains(query, ignoreCase = true)) {
                return@filter true
            }

            fallback(it)
        }
        .ifEmpty { null }

    /**
     * Returns the ID of the filter option to currently highlight.
     * @return A menu item ID of the filtering option selected.
     */
    @IdRes
    fun getFilterOptionId() =
        when (settings.searchFilterMode) {
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
        settings.searchFilterMode = newFilterMode
        search(lastQuery)
    }


    companion object {
        /**
         * Converts the output of [Normalizer] to remove any junk characters added by it's
         * replacements.
         */
        private val NORMALIZATION_SANITIZE_REGEX = Regex("\\p{InCombiningDiacriticalMarks}+")
    }
}
