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
import org.oxycblt.auxio.list.*
import org.oxycblt.auxio.list.recycler.*
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song

/**
 * An adapter that displays search results.
 * @param listener An [SelectableListListener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class SearchAdapter(private val listener: SelectableListListener) :
    SelectionIndicatorAdapter<RecyclerView.ViewHolder>(), AuxioRecyclerView.SpanSizeLookup {
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    override val currentList: List<Item>
        get() = differ.currentList

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
            SongViewHolder.VIEW_TYPE -> SongViewHolder.from(parent)
            AlbumViewHolder.VIEW_TYPE -> AlbumViewHolder.from(parent)
            ArtistViewHolder.VIEW_TYPE -> ArtistViewHolder.from(parent)
            GenreViewHolder.VIEW_TYPE -> GenreViewHolder.from(parent)
            HeaderViewHolder.VIEW_TYPE -> HeaderViewHolder.from(parent)
            else -> error("Invalid item type $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = differ.currentList[position]) {
            is Song -> (holder as SongViewHolder).bind(item, listener)
            is Album -> (holder as AlbumViewHolder).bind(item, listener)
            is Artist -> (holder as ArtistViewHolder).bind(item, listener)
            is Genre -> (holder as GenreViewHolder).bind(item, listener)
            is Header -> (holder as HeaderViewHolder).bind(item)
        }
    }

    override fun isItemFullWidth(position: Int) = differ.currentList[position] is Header

    /**
     * Asynchronously update the list with new items. Assumes that the list only contains supported
     * data..
     * @param newList The new [Item]s for the adapter to display.
     * @param callback A block called when the asynchronous update is completed.
     */
    fun submitList(newList: List<Item>, callback: () -> Unit) {
        differ.submitList(newList, callback)
    }

    private companion object {
        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleItemCallback<Item>() {
                override fun areContentsTheSame(oldItem: Item, newItem: Item) =
                    when {
                        oldItem is Song && newItem is Song ->
                            SongViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        oldItem is Album && newItem is Album ->
                            AlbumViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        oldItem is Artist && newItem is Artist ->
                            ArtistViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        oldItem is Genre && newItem is Genre ->
                            GenreViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        oldItem is Header && newItem is Header ->
                            HeaderViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        else -> false
                    }
            }
    }
}
