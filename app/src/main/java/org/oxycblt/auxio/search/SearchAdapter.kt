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

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.recycler.AlbumViewHolder
import org.oxycblt.auxio.ui.recycler.ArtistViewHolder
import org.oxycblt.auxio.ui.recycler.AuxioRecyclerView
import org.oxycblt.auxio.ui.recycler.GenreViewHolder
import org.oxycblt.auxio.ui.recycler.Header
import org.oxycblt.auxio.ui.recycler.HeaderViewHolder
import org.oxycblt.auxio.ui.recycler.IndicatorAdapter
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.ui.recycler.MenuItemListener
import org.oxycblt.auxio.ui.recycler.SimpleItemCallback
import org.oxycblt.auxio.ui.recycler.SongViewHolder

class SearchAdapter(private val listener: MenuItemListener) :
    IndicatorAdapter<RecyclerView.ViewHolder>(), AuxioRecyclerView.SpanSizeLookup {
    private val differ = AsyncListDiffer(this, DIFFER)

    override fun getItemCount() = differ.currentList.size

    override fun getItemViewType(position: Int) =
        when (differ.currentList[position]) {
            is Song -> SongViewHolder.VIEW_TYPE
            is Album -> AlbumViewHolder.VIEW_TYPE
            is Artist -> ArtistViewHolder.VIEW_TYPE
            is Genre -> GenreViewHolder.VIEW_TYPE
            is Header -> HeaderViewHolder.VIEW_TYPE
            else -> super.getItemViewType(position)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            SongViewHolder.VIEW_TYPE -> SongViewHolder.new(parent)
            AlbumViewHolder.VIEW_TYPE -> AlbumViewHolder.new(parent)
            ArtistViewHolder.VIEW_TYPE -> ArtistViewHolder.new(parent)
            GenreViewHolder.VIEW_TYPE -> GenreViewHolder.new(parent)
            HeaderViewHolder.VIEW_TYPE -> HeaderViewHolder.new(parent)
            else -> error("Invalid item type $viewType")
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)

        if (payloads.isEmpty()) {
            when (val item = differ.currentList[position]) {
                is Song -> (holder as SongViewHolder).bind(item, listener)
                is Album -> (holder as AlbumViewHolder).bind(item, listener)
                is Artist -> (holder as ArtistViewHolder).bind(item, listener)
                is Genre -> (holder as GenreViewHolder).bind(item, listener)
                is Header -> (holder as HeaderViewHolder).bind(item)
            }
        }
    }

    override fun isItemFullWidth(position: Int) = differ.currentList[position] is Header

    override val currentList: List<Item>
        get() = differ.currentList

    fun submitList(list: List<Item>, callback: () -> Unit) = differ.submitList(list, callback)

    companion object {
        private val DIFFER =
            object : SimpleItemCallback<Item>() {
                override fun areItemsTheSame(oldItem: Item, newItem: Item) =
                    when {
                        oldItem is Song && newItem is Song ->
                            SongViewHolder.DIFFER.areItemsTheSame(oldItem, newItem)
                        oldItem is Album && newItem is Album ->
                            AlbumViewHolder.DIFFER.areItemsTheSame(oldItem, newItem)
                        oldItem is Artist && newItem is Artist ->
                            ArtistViewHolder.DIFFER.areItemsTheSame(oldItem, newItem)
                        oldItem is Genre && newItem is Genre ->
                            GenreViewHolder.DIFFER.areItemsTheSame(oldItem, newItem)
                        oldItem is Header && newItem is Header ->
                            HeaderViewHolder.DIFFER.areItemsTheSame(oldItem, newItem)
                        else -> false
                    }
            }
    }
}
