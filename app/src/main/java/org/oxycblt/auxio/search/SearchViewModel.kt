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
import android.content.Context
import androidx.annotation.IdRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.text.Normalizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.ui.recycler.Header
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.util.application
import org.oxycblt.auxio.util.logD

/**
 * The [ViewModel] for search functionality.
 * @author OxygenCobalt
 */
class SearchViewModel(application: Application) :
    AndroidViewModel(application), MusicStore.Callback {
    private val musicStore = MusicStore.getInstance()
    private val settings = Settings(application)

    private val _searchResults = MutableStateFlow(listOf<Item>())
    /** Current search results from the last [search] call. */
    val searchResults: StateFlow<List<Item>>
        get() = _searchResults

    val filterMode: DisplayMode?
        get() = settings.searchFilterMode

    private var lastQuery: String? = null

    /**
     * Use [query] to perform a search of the music library. Will push results to [searchResults].
     */
    fun search(query: String?) {
        lastQuery = query

        val library = musicStore.library
        if (query.isNullOrEmpty() || library == null) {
            logD("No music/query, ignoring search")
            _searchResults.value = listOf()
            return
        }

        logD("Performing search for $query")

        // Searching can be quite expensive, so get on a co-routine
        viewModelScope.launch {
            val sort = Sort(Sort.Mode.ByName, true)
            val results = mutableListOf<Item>()

            // Note: a filter mode of null means to not filter at all.

            if (filterMode == null || filterMode == DisplayMode.SHOW_ARTISTS) {
                library.artists.filterByOrNull(query)?.let { artists ->
                    results.add(Header(-1, R.string.lbl_artists))
                    results.addAll(sort.artists(artists))
                }
            }

            if (filterMode == null || filterMode == DisplayMode.SHOW_ALBUMS) {
                library.albums.filterByOrNull(query)?.let { albums ->
                    results.add(Header(-2, R.string.lbl_albums))
                    results.addAll(sort.albums(albums))
                }
            }

            if (filterMode == null || filterMode == DisplayMode.SHOW_GENRES) {
                library.genres.filterByOrNull(query)?.let { genres ->
                    results.add(Header(-3, R.string.lbl_genres))
                    results.addAll(sort.genres(genres))
                }
            }

            if (filterMode == null || filterMode == DisplayMode.SHOW_SONGS) {
                library.songs.filterByOrNull(query)?.let { songs ->
                    results.add(Header(-4, R.string.lbl_songs))
                    results.addAll(sort.songs(songs))
                }
            }

            _searchResults.value = results
        }
    }

    /**
     * Update the current filter mode with a menu [id]. New value will be pushed to [filterMode].
     */
    fun updateFilterModeWithId(@IdRes id: Int) {
        val newFilterMode =
            when (id) {
                R.id.option_filter_songs -> DisplayMode.SHOW_SONGS
                R.id.option_filter_albums -> DisplayMode.SHOW_ALBUMS
                R.id.option_filter_artists -> DisplayMode.SHOW_ARTISTS
                R.id.option_filter_genres -> DisplayMode.SHOW_GENRES
                else -> null
            }

        logD("Updating filter mode to $newFilterMode")

        settings.searchFilterMode = filterMode

        search(lastQuery)
    }

    /**
     * Shortcut that will run a ignoreCase filter on a list and only return a value if the resulting
     * list is empty.
     */
    private fun <T : Music> List<T>.filterByOrNull(value: String): List<T>? {
        val filtered = filter {
            // Compare normalized names, which are names with unicode characters that are
            // normalized to their non-unicode forms. This is just for quality-of-life,
            // and I hope it doesn't bork search functionality for other languages.
            it.resolveNameNormalized(application).contains(value, ignoreCase = true) ||
                it.resolveNameNormalized(application).contains(value, ignoreCase = true)
        }

        return filtered.ifEmpty { null }
    }

    private fun Music.resolveNameNormalized(context: Context): String {
        // This method normalizes strings so that songs with accented characters will show
        // up in search even if the actual character was not inputted.
        // https://stackoverflow.com/a/32030586/14143986

        // Normalize with NFKD [Meaning that symbols with identical meanings will be turned into
        // their letter variants].
        val norm = Normalizer.normalize(resolveName(context), Normalizer.Form.NFKD)

        // Normalizer doesn't exactly finish the job though. We have to rebuild all the codepoints
        // in the string and remove the hidden characters that were added by Normalizer.
        var idx = 0
        val sb = StringBuilder()

        while (idx < norm.length) {
            val cp = norm.codePointAt(idx)
            idx += Character.charCount(cp)

            when (Character.getType(cp)) {
                // Character.NON_SPACING_MARK and Character.COMBINING_SPACING_MARK were added
                // by normalizer
                6,
                8 -> continue
                else -> sb.appendCodePoint(cp)
            }
        }

        return sb.toString()
    }

    override fun onLibraryChanged(library: MusicStore.Library?) {
        if (library != null) {
            // Make sure our query is up to date with the music library.
            search(lastQuery)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicStore.removeCallback(this)
    }
}
