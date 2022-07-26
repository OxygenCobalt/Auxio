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

import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.recycler.AlbumViewHolder
import org.oxycblt.auxio.ui.recycler.ArtistViewHolder
import org.oxycblt.auxio.ui.recycler.AsyncBackingData
import org.oxycblt.auxio.ui.recycler.GenreViewHolder
import org.oxycblt.auxio.ui.recycler.Header
import org.oxycblt.auxio.ui.recycler.HeaderViewHolder
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.ui.recycler.MenuItemListener
import org.oxycblt.auxio.ui.recycler.MultiAdapter
import org.oxycblt.auxio.ui.recycler.SimpleItemCallback
import org.oxycblt.auxio.ui.recycler.SongViewHolder

class SearchAdapter(listener: MenuItemListener) : MultiAdapter<MenuItemListener>(listener) {
    override val data = AsyncBackingData(this, DIFFER)

    override fun getCreatorFromItem(item: Item) =
        when (item) {
            is Song -> SongViewHolder.CREATOR
            is Album -> AlbumViewHolder.CREATOR
            is Artist -> ArtistViewHolder.CREATOR
            is Genre -> GenreViewHolder.CREATOR
            is Header -> HeaderViewHolder.CREATOR
            else -> null
        }

    override fun getCreatorFromViewType(viewType: Int) =
        when (viewType) {
            SongViewHolder.CREATOR.viewType -> SongViewHolder.CREATOR
            AlbumViewHolder.CREATOR.viewType -> AlbumViewHolder.CREATOR
            ArtistViewHolder.CREATOR.viewType -> ArtistViewHolder.CREATOR
            GenreViewHolder.CREATOR.viewType -> GenreViewHolder.CREATOR
            HeaderViewHolder.CREATOR.viewType -> HeaderViewHolder.CREATOR
            else -> null
        }

    override fun onBind(
        viewHolder: RecyclerView.ViewHolder,
        item: Item,
        listener: MenuItemListener,
        payload: List<Any>
    ) {
        when (item) {
            is Song -> (viewHolder as SongViewHolder).bind(item, listener)
            is Album -> (viewHolder as AlbumViewHolder).bind(item, listener)
            is Artist -> (viewHolder as ArtistViewHolder).bind(item, listener)
            is Genre -> (viewHolder as GenreViewHolder).bind(item, listener)
            is Header -> (viewHolder as HeaderViewHolder).bind(item, Unit)
            else -> {}
        }
    }

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
