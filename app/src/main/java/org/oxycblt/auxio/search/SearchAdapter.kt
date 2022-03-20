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

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Item
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.AlbumViewHolder
import org.oxycblt.auxio.ui.ArtistViewHolder
import org.oxycblt.auxio.ui.DiffCallback
import org.oxycblt.auxio.ui.GenreViewHolder
import org.oxycblt.auxio.ui.HeaderViewHolder
import org.oxycblt.auxio.ui.SongViewHolder

/**
 * A Multi-ViewHolder adapter that displays the results of a search query.
 * @author OxygenCobalt
 */
class SearchAdapter(
    private val doOnClick: (data: Music) -> Unit,
    private val doOnLongClick: (view: View, data: Music) -> Unit
) : ListAdapter<Item, RecyclerView.ViewHolder>(DiffCallback<Item>()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Genre -> IntegerTable.ITEM_TYPE_GENRE
            is Artist -> IntegerTable.ITEM_TYPE_ARTIST
            is Album -> IntegerTable.ITEM_TYPE_ALBUM
            is Song -> IntegerTable.ITEM_TYPE_SONG
            is Header -> IntegerTable.ITEM_TYPE_HEADER
            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            IntegerTable.ITEM_TYPE_GENRE ->
                GenreViewHolder.from(parent.context, doOnClick, doOnLongClick)
            IntegerTable.ITEM_TYPE_ARTIST ->
                ArtistViewHolder.from(parent.context, doOnClick, doOnLongClick)
            IntegerTable.ITEM_TYPE_ALBUM ->
                AlbumViewHolder.from(parent.context, doOnClick, doOnLongClick)
            IntegerTable.ITEM_TYPE_SONG ->
                SongViewHolder.from(parent.context, doOnClick, doOnLongClick)
            IntegerTable.ITEM_TYPE_HEADER -> HeaderViewHolder.from(parent.context)
            else -> error("Invalid ViewHolder item type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Genre -> (holder as GenreViewHolder).bind(item)
            is Artist -> (holder as ArtistViewHolder).bind(item)
            is Album -> (holder as AlbumViewHolder).bind(item)
            is Song -> (holder as SongViewHolder).bind(item)
            is Header -> (holder as HeaderViewHolder).bind(item)
            else -> {}
        }
    }
}
