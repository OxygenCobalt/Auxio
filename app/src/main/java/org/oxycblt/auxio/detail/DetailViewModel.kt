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
 
package org.oxycblt.auxio.detail

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.ActionHeader
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Item
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.util.logD

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
    val curGenre: LiveData<Genre?>
        get() = mCurGenre

    private val mGenreData = MutableLiveData(listOf<Item>())
    val genreData: LiveData<List<Item>> = mGenreData

    private val mCurArtist = MutableLiveData<Artist?>()
    val curArtist: LiveData<Artist?>
        get() = mCurArtist

    private val mArtistData = MutableLiveData(listOf<Item>())
    val artistData: LiveData<List<Item>> = mArtistData

    private val mCurAlbum = MutableLiveData<Album?>()
    val curAlbum: LiveData<Album?>
        get() = mCurAlbum

    private val mAlbumData = MutableLiveData(listOf<Item>())
    val albumData: LiveData<List<Item>>
        get() = mAlbumData

    data class MenuConfig(val anchor: View, val sortMode: Sort)

    private val mShowMenu = MutableLiveData<MenuConfig?>(null)
    val showMenu: LiveData<MenuConfig?> = mShowMenu

    private val mNavToItem = MutableLiveData<Item?>()

    /** Flag for unified navigation. Observe this to coordinate navigation to an item's UI. */
    val navToItem: LiveData<Item?>
        get() = mNavToItem

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

    /** Mark that the menu process is done with the new [Sort]. Pass null if there was no change. */
    fun finishShowMenu(newMode: Sort?) {
        mShowMenu.value = null

        if (newMode != null) {
            logD("Applying new sort mode")
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

    /** Navigate to an item, whether a song/album/artist */
    fun navToItem(item: Item) {
        mNavToItem.value = item
    }

    /** Mark that the navigation process is done. */
    fun finishNavToItem() {
        mNavToItem.value = null
    }

    /** Update the current navigation status to [isNavigating] */
    fun setNavigating(navigating: Boolean) {
        isNavigating = navigating
    }

    private fun refreshGenreData() {
        logD("Refreshing genre data")
        val genre = requireNotNull(curGenre.value)
        val data = mutableListOf<Item>(genre)

        data.add(
            ActionHeader(
                id = -2,
                string = R.string.lbl_songs,
                icon = R.drawable.ic_sort,
                desc = R.string.lbl_sort,
                onClick = { view ->
                    currentMenuContext = DisplayMode.SHOW_GENRES
                    mShowMenu.value = MenuConfig(view, settingsManager.detailGenreSort)
                }))

        data.addAll(settingsManager.detailGenreSort.genre(curGenre.value!!))

        mGenreData.value = data
    }

    private fun refreshArtistData() {
        logD("Refreshing artist data")
        val artist = requireNotNull(curArtist.value)
        val data = mutableListOf<Item>(artist)

        data.add(Header(id = -2, string = R.string.lbl_albums))

        data.addAll(Sort.ByYear(false).albums(artist.albums))

        data.add(
            ActionHeader(
                id = -3,
                string = R.string.lbl_songs,
                icon = R.drawable.ic_sort,
                desc = R.string.lbl_sort,
                onClick = { view ->
                    currentMenuContext = DisplayMode.SHOW_ARTISTS
                    mShowMenu.value = MenuConfig(view, settingsManager.detailArtistSort)
                }))

        data.addAll(settingsManager.detailArtistSort.artist(artist))

        mArtistData.value = data.toList()
    }

    private fun refreshAlbumData() {
        logD("Refreshing album data")
        val album = requireNotNull(curAlbum.value)
        val data = mutableListOf<Item>(album)

        data.add(
            ActionHeader(
                id = -2,
                string = R.string.lbl_songs,
                icon = R.drawable.ic_sort,
                desc = R.string.lbl_sort,
                onClick = { view ->
                    currentMenuContext = DisplayMode.SHOW_ALBUMS
                    mShowMenu.value = MenuConfig(view, settingsManager.detailAlbumSort)
                }))

        data.addAll(settingsManager.detailAlbumSort.album(curAlbum.value!!))

        mAlbumData.value = data
    }
}
