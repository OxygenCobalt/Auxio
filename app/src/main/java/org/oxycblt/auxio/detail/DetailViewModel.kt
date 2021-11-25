/*
 * Copyright (c) 2021 Auxio Project
 * DetailViewModel.kt is part of Auxio.
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

package org.oxycblt.auxio.detail

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.ActionHeader
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.HeaderString
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.Sort

/**
 * ViewModel that stores data for the [DetailFragment]s. This includes:
 * - What item the fragment should be showing
 * - The RecyclerView data for each fragment
 * - Menu triggers for each fragment
 * - Navigation triggers for each fragment [e.g "Go to artist"]
 * @author OxygenCobalt
 */
class DetailViewModel : ViewModel() {
    // --- CURRENT VALUES ---

    private val mCurGenre = MutableLiveData<Genre?>()
    val curGenre: LiveData<Genre?> get() = mCurGenre

    private val mGenreData = MutableLiveData(listOf<BaseModel>())
    val genreData: LiveData<List<BaseModel>> = mGenreData

    private val mCurArtist = MutableLiveData<Artist?>()
    val curArtist: LiveData<Artist?> get() = mCurArtist

    private val mArtistData = MutableLiveData(listOf<BaseModel>())
    val artistData: LiveData<List<BaseModel>> = mArtistData

    private val mCurAlbum = MutableLiveData<Album?>()
    val curAlbum: LiveData<Album?> get() = mCurAlbum

    private val mAlbumData = MutableLiveData(listOf<BaseModel>())
    val albumData: LiveData<List<BaseModel>> get() = mAlbumData

    data class MenuConfig(val anchor: View, val sortMode: Sort)

    private val mShowMenu = MutableLiveData<MenuConfig?>(null)
    val showMenu: LiveData<MenuConfig?> = mShowMenu

    private val mNavToItem = MutableLiveData<BaseModel?>()

    /** Flag for unified navigation. Observe this to coordinate navigation to an item's UI. */
    val navToItem: LiveData<BaseModel?> get() = mNavToItem

    var isNavigating = false
        private set

    private var currentMenuContext: DisplayMode? = null

    private val settingsManager = SettingsManager.getInstance()

    fun setGenre(id: Long) {
        if (mCurGenre.value?.id == id) return

        val musicStore = MusicStore.requireInstance()
        mCurGenre.value = musicStore.genres.find { it.id == id }
        refreshGenreData()
    }

    fun setArtist(id: Long) {
        if (mCurArtist.value?.id == id) return

        val musicStore = MusicStore.requireInstance()
        mCurArtist.value = musicStore.artists.find { it.id == id }
        refreshArtistData()
    }

    fun setAlbum(id: Long) {
        if (mCurAlbum.value?.id == id) return

        val musicStore = MusicStore.requireInstance()
        mCurAlbum.value = musicStore.albums.find { it.id == id }
        refreshAlbumData()
    }

    /**
     * Mark that the menu process is done with the new [Sort].
     * Pass null if there was no change.
     */
    fun finishShowMenu(newMode: Sort?) {
        mShowMenu.value = null

        if (newMode != null) {
            when (currentMenuContext) {
                DisplayMode.SHOW_ALBUMS -> {
                    settingsManager.detailAlbumSort = newMode
                    refreshAlbumData()
                }
                DisplayMode.SHOW_ARTISTS -> {
                    settingsManager.detailArtistSort = newMode
                    refreshArtistData()
                }
                DisplayMode.SHOW_GENRES -> {
                    settingsManager.detailGenreSort = newMode
                    refreshGenreData()
                }
                else -> {}
            }
        }

        currentMenuContext = null
    }

    /**
     * Navigate to an item, whether a song/album/artist
     */
    fun navToItem(item: BaseModel) {
        mNavToItem.value = item
    }

    /**
     * Mark that the navigation process is done.
     */
    fun finishNavToItem() {
        mNavToItem.value = null
    }

    /**
     * Update the current navigation status to [isNavigating]
     */
    fun setNavigating(navigating: Boolean) {
        isNavigating = navigating
    }

    private fun refreshGenreData() {
        val data = mutableListOf<BaseModel>(curGenre.value!!)

        data.add(
            ActionHeader(
                id = -2,
                string = HeaderString.Single(R.string.lbl_songs),
                icon = R.drawable.ic_sort,
                desc = R.string.lbl_sort,
                onClick = { view ->
                    currentMenuContext = DisplayMode.SHOW_GENRES
                    mShowMenu.value = MenuConfig(view, settingsManager.detailGenreSort)
                }
            )
        )

        data.addAll(settingsManager.detailGenreSort.sortGenre(curGenre.value!!))

        mGenreData.value = data
    }

    private fun refreshArtistData() {
        val artist = curArtist.value!!
        val data = mutableListOf<BaseModel>(artist)

        data.add(
            Header(
                id = -2,
                string = HeaderString.Single(R.string.lbl_albums)
            )
        )

        data.addAll(Sort.ByYear(false).sortAlbums(artist.albums))

        data.add(
            ActionHeader(
                id = -3,
                string = HeaderString.Single(R.string.lbl_songs),
                icon = R.drawable.ic_sort,
                desc = R.string.lbl_sort,
                onClick = { view ->
                    currentMenuContext = DisplayMode.SHOW_ARTISTS
                    mShowMenu.value = MenuConfig(view, settingsManager.detailArtistSort)
                }
            )
        )

        data.addAll(settingsManager.detailArtistSort.sortArtist(artist))

        mArtistData.value = data.toList()
    }

    private fun refreshAlbumData() {
        val data = mutableListOf<BaseModel>(curAlbum.value!!)

        data.add(
            ActionHeader(
                id = -2,
                string = HeaderString.Single(R.string.lbl_songs),
                icon = R.drawable.ic_sort,
                desc = R.string.lbl_sort,
                onClick = { view ->
                    currentMenuContext = DisplayMode.SHOW_ALBUMS
                    mShowMenu.value = MenuConfig(view, settingsManager.detailAlbumSort)
                }
            )
        )

        data.addAll(settingsManager.detailAlbumSort.sortAlbum(curAlbum.value!!))

        mAlbumData.value = data
    }
}
