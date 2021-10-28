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

import android.content.Context
import androidx.annotation.IdRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.DisplayMode
import java.text.Normalizer

/**
 * The [ViewModel] for the search functionality
 * @author OxygenCobalt
 */
class SearchViewModel(context: Context) : ViewModel(), MusicStore.MusicCallback {
    private val mSearchResults = MutableLiveData(listOf<BaseModel>())
    private var mIsNavigating = false
    private var mFilterMode: DisplayMode? = null
    private var mLastQuery = ""

    /** Current search results from the last [doSearch] call. */
    val searchResults: LiveData<List<BaseModel>> get() = mSearchResults
    val isNavigating: Boolean get() = mIsNavigating
    val filterMode: DisplayMode? get() = mFilterMode

    private val settingsManager = SettingsManager.getInstance()

    private val songHeader = Header(id = -1, context.getString(R.string.lbl_songs))
    private val albumHeader = Header(id = -1, context.getString(R.string.lbl_albums))
    private val artistHeader = Header(id = -1, context.getString(R.string.lbl_artists))
    private val genreHeader = Header(id = -1, context.getString(R.string.lbl_genres))

    init {
        mFilterMode = settingsManager.searchFilterMode

        MusicStore.awaitInstance(this)
    }

    /**
     * Use [query] to perform a search of the music library.
     * Will push results to [searchResults].
     */
    fun doSearch(query: String) {
        val musicStore = MusicStore.maybeGetInstance()
        mLastQuery = query

        if (query.isEmpty() || musicStore == null) {
            mSearchResults.value = listOf()

            return
        }

        viewModelScope.launch {
            val results = mutableListOf<BaseModel>()

            // A filter mode of null means to not filter at all.

            if (mFilterMode == null || mFilterMode == DisplayMode.SHOW_ARTISTS) {
                musicStore.artists.filterByOrNull(query)?.let { artists ->
                    results.add(artistHeader)
                    results.addAll(artists)
                }
            }

            if (mFilterMode == null || mFilterMode == DisplayMode.SHOW_ALBUMS) {
                musicStore.albums.filterByOrNull(query)?.let { albums ->
                    results.add(albumHeader)
                    results.addAll(albums)
                }
            }

            if (mFilterMode == null || mFilterMode == DisplayMode.SHOW_GENRES) {
                musicStore.genres.filterByOrNull(query)?.let { genres ->
                    results.add(genreHeader)
                    results.addAll(genres)
                }
            }

            if (mFilterMode == null || mFilterMode == DisplayMode.SHOW_SONGS) {
                musicStore.songs.filterByOrNull(query)?.let { songs ->
                    results.add(songHeader)
                    results.addAll(songs)
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

        doSearch(mLastQuery)
    }

    /**
     * Shortcut that will run a ignoreCase filter on a list and only return
     * a value if the resulting list is empty.
     */
    private fun List<BaseModel>.filterByOrNull(value: String): List<BaseModel>? {
        val filtered = filter {
            // First see if the normal item name will work. If that fails, try the "normalized"
            // [e.g all accented/unicode chars become latin chars] instead. Hopefully this
            // shouldn't break other language's search functionality.
            it.name.contains(value, ignoreCase = true) ||
                it.name.normalized().contains(value, ignoreCase = true)
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

    // --- OVERRIDES ---

    override fun onLoaded(musicStore: MusicStore) {
        doSearch(mLastQuery)
    }

    override fun onCleared() {
        super.onCleared()
        MusicStore.cancelAwaitInstance(this)
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            check(modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                "SearchViewModel.Factory does not support this class"
            }

            @Suppress("UNCHECKED_CAST")
            return SearchViewModel(context) as T
        }
    }
}
