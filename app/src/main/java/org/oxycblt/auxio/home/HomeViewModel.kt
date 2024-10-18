/*
 * Copyright (c) 2021 Auxio Project
 * HomeViewModel.kt is part of Auxio.
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

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.home.tabs.Tab
import org.oxycblt.auxio.list.ListSettings
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.list.sort.Sort
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicType
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaySong
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.util.Event
import org.oxycblt.auxio.util.MutableEvent
import timber.log.Timber as L

/**
 * The ViewModel for managing the tab data and lists of the home view.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val listSettings: ListSettings,
    private val playbackSettings: PlaybackSettings,
    homeGeneratorFactory: HomeGenerator.Factory
) : ViewModel(), HomeGenerator.Invalidator {
    private val _songList = MutableStateFlow(listOf<Song>())
    /** A list of [Song]s, sorted by the preferred [Sort], to be shown in the home view. */
    val songList: StateFlow<List<Song>>
        get() = _songList

    private val _songInstructions = MutableEvent<UpdateInstructions>()
    /** Instructions for how to update [songList] in the UI. */
    val songInstructions: Event<UpdateInstructions>
        get() = _songInstructions

    /** The current [Sort] used for [songList]. */
    val songSort: Sort
        get() = listSettings.songSort

    /** The [PlaySong] instructions to use when playing a [Song]. */
    val playWith
        get() = playbackSettings.playInListWith

    private val _albumList = MutableStateFlow(listOf<Album>())
    /** A list of [Album]s, sorted by the preferred [Sort], to be shown in the home view. */
    val albumList: StateFlow<List<Album>>
        get() = _albumList

    private val _albumInstructions = MutableEvent<UpdateInstructions>()
    /** Instructions for how to update [albumList] in the UI. */
    val albumInstructions: Event<UpdateInstructions>
        get() = _albumInstructions

    /** The current [Sort] used for [albumList]. */
    val albumSort: Sort
        get() = listSettings.albumSort

    private val _artistList = MutableStateFlow(listOf<Artist>())
    /**
     * A list of [Artist]s, sorted by the preferred [Sort], to be shown in the home view. Note that
     * if "Hide collaborators" is on, this list will not include collaborator [Artist]s.
     */
    val artistList: MutableStateFlow<List<Artist>>
        get() = _artistList

    private val _artistInstructions = MutableEvent<UpdateInstructions>()
    /** Instructions for how to update [artistList] in the UI. */
    val artistInstructions: Event<UpdateInstructions>
        get() = _artistInstructions

    /** The current [Sort] used for [artistList]. */
    val artistSort: Sort
        get() = listSettings.artistSort

    private val _genreList = MutableStateFlow(listOf<Genre>())
    /** A list of [Genre]s, sorted by the preferred [Sort], to be shown in the home view. */
    val genreList: StateFlow<List<Genre>>
        get() = _genreList

    private val _genreInstructions = MutableEvent<UpdateInstructions>()
    /** Instructions for how to update [genreList] in the UI. */
    val genreInstructions: Event<UpdateInstructions>
        get() = _genreInstructions

    /** The current [Sort] used for [genreList]. */
    val genreSort: Sort
        get() = listSettings.genreSort

    private val _playlistList = MutableStateFlow(listOf<Playlist>())
    /** A list of [Playlist]s, sorted by the preferred [Sort], to be shown in the home view. */
    val playlistList: StateFlow<List<Playlist>>
        get() = _playlistList

    private val _playlistInstructions = MutableEvent<UpdateInstructions>()
    /** Instructions for how to update [genreList] in the UI. */
    val playlistInstructions: Event<UpdateInstructions>
        get() = _playlistInstructions

    /** The current [Sort] used for [genreList]. */
    val playlistSort: Sort
        get() = listSettings.playlistSort

    private val homeGenerator = homeGeneratorFactory.create(this)

    /**
     * A list of [MusicType] corresponding to the current [Tab] configuration, excluding invisible
     * [Tab]s.
     */
    var currentTabTypes = homeGenerator.tabs()
        private set

    private val _currentTabType = MutableStateFlow(currentTabTypes[0])
    /** The [MusicType] of the currently shown [Tab]. */
    val currentTabType: StateFlow<MusicType> = _currentTabType

    private val _shouldRecreate = MutableEvent<Unit>()
    /**
     * A marker to re-create all library tabs, usually initiated by a settings change. When this
     * flag is true, all tabs (and their respective ViewPager2 fragments) will be re-created from
     * scratch.
     */
    val recreateTabs: Event<Unit>
        get() = _shouldRecreate

    private val _isFastScrolling = MutableStateFlow(false)
    /** A marker for whether the user is fast-scrolling in the home view or not. */
    val isFastScrolling: StateFlow<Boolean> = _isFastScrolling

    private val _showOuter = MutableEvent<Outer>()
    val showOuter: Event<Outer>
        get() = _showOuter

    init {
        homeGenerator.attach()
    }

    override fun onCleared() {
        super.onCleared()
        homeGenerator.release()
    }

    override fun invalidateMusic(type: MusicType, instructions: UpdateInstructions) {
        when (type) {
            MusicType.SONGS -> {
                _songInstructions.put(instructions)
                _songList.value = homeGenerator.songs()
            }
            MusicType.ALBUMS -> {
                _albumInstructions.put(instructions)
                _albumList.value = homeGenerator.albums()
            }
            MusicType.ARTISTS -> {
                _artistInstructions.put(instructions)
                _artistList.value = homeGenerator.artists()
            }
            MusicType.GENRES -> {
                _genreInstructions.put(instructions)
                _genreList.value = homeGenerator.genres()
            }
            MusicType.PLAYLISTS -> {
                _playlistInstructions.put(instructions)
                _playlistList.value = homeGenerator.playlists()
            }
        }
    }

    override fun invalidateTabs() {
        currentTabTypes = homeGenerator.tabs()
        _shouldRecreate.put(Unit)
    }

    /**
     * Apply a new [Sort] to [songList].
     *
     * @param sort The [Sort] to apply.
     */
    fun applySongSort(sort: Sort) {
        listSettings.songSort = sort
    }

    /**
     * Apply a new [Sort] to [albumList].
     *
     * @param sort The [Sort] to apply.
     */
    fun applyAlbumSort(sort: Sort) {
        listSettings.albumSort = sort
    }

    /**
     * Apply a new [Sort] to [artistList].
     *
     * @param sort The [Sort] to apply.
     */
    fun applyArtistSort(sort: Sort) {
        listSettings.artistSort = sort
    }

    /**
     * Apply a new [Sort] to [genreList].
     *
     * @param sort The [Sort] to apply.
     */
    fun applyGenreSort(sort: Sort) {
        listSettings.genreSort = sort
    }

    /**
     * Apply a new [Sort] to [playlistList].
     *
     * @param sort The [Sort] to apply.
     */
    fun applyPlaylistSort(sort: Sort) {
        listSettings.playlistSort = sort
    }

    /**
     * Update [currentTabType] to reflect a new ViewPager2 position
     *
     * @param pagerPos The new position of the ViewPager2 instance.
     */
    fun synchronizeTabPosition(pagerPos: Int) {
        L.d("Updating current tab to ${currentTabTypes[pagerPos]}")
        _currentTabType.value = currentTabTypes[pagerPos]
    }

    /**
     * Update whether the user is fast scrolling or not in the home view.
     *
     * @param isFastScrolling true if the user is currently fast scrolling, false otherwise.
     */
    fun setFastScrolling(isFastScrolling: Boolean) {
        L.d("Updating fast scrolling state: $isFastScrolling")
        _isFastScrolling.value = isFastScrolling
    }

    fun showSettings() {
        _showOuter.put(Outer.Settings)
    }

    fun showAbout() {
        _showOuter.put(Outer.About)
    }
}

sealed interface Outer {
    data object Settings : Outer

    data object About : Outer
}
