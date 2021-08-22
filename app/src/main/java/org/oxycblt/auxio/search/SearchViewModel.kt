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
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.recycler.DisplayMode
import org.oxycblt.auxio.settings.SettingsManager

/**
 * The [ViewModel] for the search functionality
 * @author OxygenCobalt
 */
class SearchViewModel : ViewModel() {
    private val mSearchResults = MutableLiveData(listOf<BaseModel>())
    private var mIsNavigating = false
    private var mFilterMode: DisplayMode? = null
    private var mLastQuery = ""

    /** Current search results from the last [doSearch] call. */
    val searchResults: LiveData<List<BaseModel>> get() = mSearchResults
    val isNavigating: Boolean get() = mIsNavigating
    val filterMode: DisplayMode? get() = mFilterMode

    private val musicStore = MusicStore.getInstance()
    private val settingsManager = SettingsManager.getInstance()

    init {
        mFilterMode = settingsManager.searchFilterMode
    }

    /**
     * Use [query] to perform a search of the music library.
     * Will push results to [searchResults].
     */
    fun doSearch(query: String, context: Context) {
        mLastQuery = query

        if (query.isEmpty()) {
            mSearchResults.value = listOf()

            return
        }

        viewModelScope.launch {
            val results = mutableListOf<BaseModel>()

            // A filter mode of null means to not filter at all.

            if (mFilterMode == null || mFilterMode == DisplayMode.SHOW_ARTISTS) {
                musicStore.artists.filterByOrNull(query)?.let { artists ->
                    results.add(Header(id = -1, name = context.getString(R.string.lbl_artists)))
                    results.addAll(artists)
                }
            }

            if (mFilterMode == null || mFilterMode == DisplayMode.SHOW_ALBUMS) {
                musicStore.albums.filterByOrNull(query)?.let { albums ->
                    results.add(Header(id = -2, name = context.getString(R.string.lbl_albums)))
                    results.addAll(albums)
                }
            }

            if (mFilterMode == null || mFilterMode == DisplayMode.SHOW_GENRES) {
                musicStore.genres.filterByOrNull(query)?.let { genres ->
                    results.add(Header(id = -3, name = context.getString(R.string.lbl_genres)))
                    results.addAll(genres)
                }
            }

            if (mFilterMode == null || mFilterMode == DisplayMode.SHOW_SONGS) {
                musicStore.songs.filterByOrNull(query)?.let { songs ->
                    results.add(Header(id = -4, name = context.getString(R.string.lbl_songs)))
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
    fun updateFilterModeWithId(@IdRes id: Int, context: Context) {
        mFilterMode = when (id) {
            R.id.option_filter_songs -> DisplayMode.SHOW_SONGS
            R.id.option_filter_albums -> DisplayMode.SHOW_ALBUMS
            R.id.option_filter_artists -> DisplayMode.SHOW_ARTISTS
            R.id.option_filter_genres -> DisplayMode.SHOW_GENRES

            else -> null
        }

        settingsManager.searchFilterMode = mFilterMode

        doSearch(mLastQuery, context)
    }

    /**
     * Shortcut that will run a ignoreCase filter on a list and only return
     * a value if the resulting list is empty.
     */
    private fun List<BaseModel>.filterByOrNull(value: String): List<BaseModel>? {
        val filtered = filter {
            it.name.contains(value, ignoreCase = true)
        }

        return if (filtered.isNotEmpty()) filtered else null
    }

    /**
     * Update the current navigation status to [isNavigating]
     */
    fun setNavigating(isNavigating: Boolean) {
        mIsNavigating = isNavigating
    }
}
