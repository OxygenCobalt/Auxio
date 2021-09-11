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

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.oxycblt.auxio.R
import org.oxycblt.auxio.home.LibSortMode
import org.oxycblt.auxio.music.ActionHeader
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.ui.SortMode

/**
 * ViewModel that stores data for the [DetailFragment]s, such as what they're showing & what
 * [SortMode] they are currently on.
 * TODO: Re-add sorting
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

    var isNavigating = false
        private set

    private val mNavToItem = MutableLiveData<BaseModel?>()

    /** Flag for unified navigation. Observe this to coordinate navigation to an item's UI. */
    val navToItem: LiveData<BaseModel?> get() = mNavToItem

    private val musicStore = MusicStore.getInstance()

    fun setGenre(id: Long, context: Context) {
        if (mCurGenre.value?.id == id) return

        mCurGenre.value = musicStore.genres.find { it.id == id }

        val data = mutableListOf<BaseModel>(curGenre.value!!)

        data.add(
            ActionHeader(
                id = -2,
                name = context.getString(R.string.lbl_songs),
                icon = R.drawable.ic_sort,
                desc = R.string.lbl_sort,
                onClick = {
                }
            )
        )

        data.addAll(LibSortMode.ASCENDING.sortGenre(curGenre.value!!))

        mGenreData.value = data
    }

    fun setArtist(id: Long, context: Context) {
        if (mCurArtist.value?.id == id) return

        mCurArtist.value = musicStore.artists.find { it.id == id }

        val artist = curArtist.value!!
        val data = mutableListOf<BaseModel>(artist)

        data.add(
            Header(
                id = -2,
                name = context.getString(R.string.lbl_albums)
            )
        )

        data.addAll(LibSortMode.YEAR.sortAlbums(artist.albums))

        data.add(
            ActionHeader(
                id = -3,
                name = context.getString(R.string.lbl_songs),
                icon = R.drawable.ic_sort,
                desc = R.string.lbl_sort,
                onClick = {
                }
            )
        )

        data.addAll(LibSortMode.YEAR.sortArtist(artist))

        mArtistData.value = data.toList()
    }

    fun setAlbum(id: Long, context: Context) {
        if (mCurAlbum.value?.id == id) return

        mCurAlbum.value = musicStore.albums.find { it.id == id }

        val data = mutableListOf<BaseModel>(curAlbum.value!!)

        data.add(
            ActionHeader(
                id = -2,
                name = context.getString(R.string.lbl_songs),
                icon = R.drawable.ic_sort,
                desc = R.string.lbl_sort,
                onClick = {
                }
            )
        )

        data.addAll(LibSortMode.ASCENDING.sortAlbum(curAlbum.value!!))

        mAlbumData.value = data
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
}
