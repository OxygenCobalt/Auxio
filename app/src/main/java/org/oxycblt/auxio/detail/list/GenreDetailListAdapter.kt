/*
 * Copyright (c) 2021 Auxio Project
 * GenreDetailListAdapter.kt is part of Auxio.
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
 
package org.oxycblt.auxio.detail.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.adapter.SimpleDiffCallback
import org.oxycblt.auxio.list.recycler.ArtistViewHolder
import org.oxycblt.auxio.list.recycler.SongViewHolder
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song

/**
 * A [DetailListAdapter] implementing the header and sub-items for the [Genre] detail view.
 *
 * @param listener A [DetailListAdapter.Listener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class GenreDetailListAdapter(private val listener: Listener<Music>) :
    DetailListAdapter(listener, DIFF_CALLBACK) {
    override fun getItemViewType(position: Int) =
        when (getItem(position)) {
            // Support generic Artist/Song items.
            is Artist -> ArtistViewHolder.VIEW_TYPE
            is Song -> SongViewHolder.VIEW_TYPE
            else -> super.getItemViewType(position)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            ArtistViewHolder.VIEW_TYPE -> ArtistViewHolder.from(parent)
            SongViewHolder.VIEW_TYPE -> SongViewHolder.from(parent)
            else -> super.onCreateViewHolder(parent, viewType)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (val item = getItem(position)) {
            is Artist -> (holder as ArtistViewHolder).bind(item, listener)
            is Song -> (holder as SongViewHolder).bind(item, listener)
        }
    }

    private companion object {
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Item>() {
                override fun areContentsTheSame(oldItem: Item, newItem: Item) =
                    when {
                        oldItem is Artist && newItem is Artist ->
                            ArtistViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        oldItem is Song && newItem is Song ->
                            SongViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        else -> DetailListAdapter.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                    }
            }
    }
}
