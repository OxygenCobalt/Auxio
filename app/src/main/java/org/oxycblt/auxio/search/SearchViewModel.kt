/*
 * Copyright (c) 2021 Auxio Project
 * SearchViewModel.kt is part of Auxio.
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

import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.HeaderString
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.Sort
import java.text.Normalizer

/**
 * The [ViewModel] for the search functionality
 * @author OxygenCobalt
 */
class SearchViewModel : ViewModel() {
    private val mSearchResults = MutableLiveData(listOf<BaseModel>())
    private var mIsNavigating = false
    private var mFilterMode: DisplayMode? = null
    private var mLastQuery = ""

    /** Current search results from the last [search] call. */
    val searchResults: LiveData<List<BaseModel>> get() = mSearchResults
    val isNavigating: Boolean get() = mIsNavigating
    val filterMode: DisplayMode? get() = mFilterMode

    private val settingsManager = SettingsManager.getInstance()

    init {
        mFilterMode = settingsManager.searchFilterMode

        viewModelScope.launch {
            MusicStore.awaitInstance()
            search(mLastQuery)
        }
    }

    /**
     * Use [query] to perform a search of the music library.
     * Will push results to [searchResults].
     */
    fun search(query: String) {
        val musicStore = MusicStore.maybeGetInstance()
        mLastQuery = query

        if (query.isEmpty() || musicStore == null) {
            mSearchResults.value = listOf()

            return
        }

        // Searching can be quite expensive, so hop on a co-routine
        viewModelScope.launch {
            val sort = Sort.ByName(true)
            val results = mutableListOf<BaseModel>()

            // A filter mode of null means to not filter at all.

            if (mFilterMode == null || mFilterMode == DisplayMode.SHOW_ARTISTS) {
                musicStore.artists.filterByOrNull(query)?.let { artists ->
                    results.add(Header(-1, HeaderString.Single(R.string.lbl_artists)))
                    results.addAll(sort.sortParents(artists))
                }
            }

            if (mFilterMode == null || mFilterMode == DisplayMode.SHOW_ALBUMS) {
                musicStore.albums.filterByOrNull(query)?.let { albums ->
                    results.add(Header(-2, HeaderString.Single(R.string.lbl_albums)))
                    results.addAll(sort.sortAlbums(albums))
                }
            }

            if (mFilterMode == null || mFilterMode == DisplayMode.SHOW_GENRES) {
                musicStore.genres.filterByOrNull(query)?.let { genres ->
                    results.add(Header(-3, HeaderString.Single(R.string.lbl_genres)))
                    results.addAll(sort.sortParents(genres))
                }
            }

            if (mFilterMode == null || mFilterMode == DisplayMode.SHOW_SONGS) {
                musicStore.songs.filterByOrNull(query)?.let { songs ->
                    results.add(Header(-4, HeaderString.Single(R.string.lbl_songs)))
                    results.addAll(sort.sortSongs(songs))
                }
            }

            mSearchResults.value = results
        }
    }

    /**
     * Update the current filter mode with a menu [id].
     * New value will be pushed to [filterMode].
     */
    fun updateFilterModeWithId(@IdRes id: Int) {
        mFilterMode = when (id) {
            R.id.option_filter_songs -> DisplayMode.SHOW_SONGS
            R.id.option_filter_albums -> DisplayMode.SHOW_ALBUMS
            R.id.option_filter_artists -> DisplayMode.SHOW_ARTISTS
            R.id.option_filter_genres -> DisplayMode.SHOW_GENRES

            else -> null
        }

        settingsManager.searchFilterMode = mFilterMode

        search(mLastQuery)
    }

    /**
     * Shortcut that will run a ignoreCase filter on a list and only return
     * a value if the resulting list is empty.
     */
    private fun <T : Music> List<T>.filterByOrNull(value: String): List<T>? {
        val filtered = filter {
            // Ensure the name we match with is correct.
            val name = if (it is MusicParent) {
                it.resolvedName
            } else {
                it.name
            }

            // First see if the normal item name will work. If that fails, try the "normalized"
            // [e.g all accented/unicode chars become latin chars] instead. Hopefully this
            // shouldn't break other language's search functionality.
            name.contains(value, ignoreCase = true) ||
                name.normalized().contains(value, ignoreCase = true)
        }

        return if (filtered.isNotEmpty()) filtered else null
    }

    private fun String.normalized(): String {
        // This method normalizes strings so that songs with accented characters will show
        // up in search even if the actual character was not inputted.
        // https://stackoverflow.com/a/32030586/14143986

        // Normalize with NFKD [Meaning that symbols with identical meanings will be turned into
        // their letter variants].
        val norm = Normalizer.normalize(this, Normalizer.Form.NFKD)

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
                6, 8 -> continue

                else -> sb.appendCodePoint(cp)
            }
        }

        return sb.toString()
    }

    /**
     * Update the current navigation status to [isNavigating]
     */
    fun setNavigating(isNavigating: Boolean) {
        mIsNavigating = isNavigating
    }
}
