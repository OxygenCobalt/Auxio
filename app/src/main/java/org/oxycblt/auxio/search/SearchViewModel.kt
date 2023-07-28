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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.BasicHeader
import org.oxycblt.auxio.list.Divider
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.sort.Sort
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.music.MusicType
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.device.DeviceLibrary
import org.oxycblt.auxio.music.user.UserLibrary
import org.oxycblt.auxio.playback.PlaySong
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.util.logD

/**
 * An [ViewModel] that keeps performs search operations and tracks their results.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@HiltViewModel
class SearchViewModel
@Inject
constructor(
    private val musicRepository: MusicRepository,
    private val searchEngine: SearchEngine,
    private val searchSettings: SearchSettings,
    private val playbackSettings: PlaybackSettings,
) : ViewModel(), MusicRepository.UpdateListener {
    private var lastQuery: String? = null
    private var currentSearchJob: Job? = null

    private val _searchResults = MutableStateFlow(listOf<Item>())
    /** The results of the last [search] call, if any. */
    val searchResults: StateFlow<List<Item>>
        get() = _searchResults

    /** The [PlaySong] instructions to use when playing a [Song]. */
    val playWith
        get() = playbackSettings.playInListWith

    init {
        musicRepository.addUpdateListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        musicRepository.removeUpdateListener(this)
    }

    override fun onMusicChanges(changes: MusicRepository.Changes) {
        if (changes.deviceLibrary || changes.userLibrary) {
            logD("Music changed, re-searching library")
            search(lastQuery)
        }
    }

    /**
     * Asynchronously search the music library. Results will be pushed to [searchResults]. Will
     * cancel any previous search operations started prior.
     *
     * @param query The query to search the music library for.
     */
    fun search(query: String?) {
        // Cancel the previous background search.
        currentSearchJob?.cancel()
        lastQuery = query

        val deviceLibrary = musicRepository.deviceLibrary
        val userLibrary = musicRepository.userLibrary
        if (query.isNullOrEmpty() || deviceLibrary == null || userLibrary == null) {
            logD("Cannot search for the current query, aborting")
            _searchResults.value = listOf()
            return
        }

        // Searching is time-consuming, so do it in the background.
        logD("Searching music library for $query")
        currentSearchJob =
            viewModelScope.launch {
                _searchResults.value =
                    searchImpl(deviceLibrary, userLibrary, query).also { yield() }
            }
    }

    private suspend fun searchImpl(
        deviceLibrary: DeviceLibrary,
        userLibrary: UserLibrary,
        query: String
    ): List<Item> {
        val filter = searchSettings.filterTo

        val items =
            if (filter == null) {
                // A nulled filter type means to not filter anything.
                logD("No filter specified, using entire library")
                SearchEngine.Items(
                    deviceLibrary.songs,
                    deviceLibrary.albums,
                    deviceLibrary.artists,
                    deviceLibrary.genres,
                    userLibrary.playlists)
            } else {
                logD("Filter specified, reducing library")
                SearchEngine.Items(
                    songs = if (filter == MusicType.SONGS) deviceLibrary.songs else null,
                    albums = if (filter == MusicType.ALBUMS) deviceLibrary.albums else null,
                    artists = if (filter == MusicType.ARTISTS) deviceLibrary.artists else null,
                    genres = if (filter == MusicType.GENRES) deviceLibrary.genres else null,
                    playlists = if (filter == MusicType.PLAYLISTS) userLibrary.playlists else null)
            }

        val results = searchEngine.search(items, query)

        return buildList {
            results.artists?.let {
                logD("Adding ${it.size} artists to search results")
                val header = BasicHeader(R.string.lbl_artists)
                add(header)
                addAll(SORT.artists(it))
            }
            results.albums?.let {
                logD("Adding ${it.size} albums to search results")
                val header = BasicHeader(R.string.lbl_albums)
                if (isNotEmpty()) {
                    add(Divider(header))
                }

                add(header)
                addAll(SORT.albums(it))
            }
            results.playlists?.let {
                logD("Adding ${it.size} playlists to search results")
                val header = BasicHeader(R.string.lbl_playlists)
                if (isNotEmpty()) {
                    add(Divider(header))
                }

                add(header)
                addAll(SORT.playlists(it))
            }
            results.genres?.let {
                logD("Adding ${it.size} genres to search results")
                val header = BasicHeader(R.string.lbl_genres)
                if (isNotEmpty()) {
                    add(Divider(header))
                }

                add(header)
                addAll(SORT.genres(it))
            }
            results.songs?.let {
                logD("Adding ${it.size} songs to search results")
                val header = BasicHeader(R.string.lbl_songs)
                if (isNotEmpty()) {
                    add(Divider(header))
                }

                add(header)
                addAll(SORT.songs(it))
            }
        }
    }

    /**
     * Returns the ID of the filter option to currently highlight.
     *
     * @return A menu item ID of the filtering option selected.
     */
    @IdRes
    fun getFilterOptionId() =
        when (searchSettings.filterTo) {
            MusicType.SONGS -> R.id.option_filter_songs
            MusicType.ALBUMS -> R.id.option_filter_albums
            MusicType.ARTISTS -> R.id.option_filter_artists
            MusicType.GENRES -> R.id.option_filter_genres
            MusicType.PLAYLISTS -> R.id.option_filter_playlists
            // Null maps to filtering nothing.
            null -> R.id.option_filter_all
        }

    /**
     * Update the filter type with the newly-selected filter option.
     *
     * @return A menu item ID of the new filtering option selected.
     */
    fun setFilterOptionId(@IdRes id: Int) {
        val newFilter =
            when (id) {
                R.id.option_filter_songs -> MusicType.SONGS
                R.id.option_filter_albums -> MusicType.ALBUMS
                R.id.option_filter_artists -> MusicType.ARTISTS
                R.id.option_filter_genres -> MusicType.GENRES
                R.id.option_filter_playlists -> MusicType.PLAYLISTS
                // Null maps to filtering nothing.
                R.id.option_filter_all -> null
                else -> error("Invalid option ID provided")
            }
        logD("Updating filter type to $newFilter")
        searchSettings.filterTo = newFilter
        search(lastQuery)
    }

    private companion object {
        val SORT = Sort(Sort.Mode.ByName, Sort.Direction.ASCENDING)
    }
}
