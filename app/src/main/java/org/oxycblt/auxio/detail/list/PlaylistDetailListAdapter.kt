/*
 * Copyright (c) 2023 Auxio Project
 * PlaylistDetailListAdapter.kt is part of Auxio.
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
import org.oxycblt.auxio.list.recycler.SongViewHolder
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song

/**
 * A [DetailListAdapter] implementing the header and sub-items for the [Playlist] detail view.
 *
 * @param listener A [DetailListAdapter.Listener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class PlaylistDetailListAdapter(private val listener: Listener<Song>) :
    DetailListAdapter(listener, DIFF_CALLBACK) {
    override fun getItemViewType(position: Int) =
        when (getItem(position)) {
            // Support generic song items.
            is Song -> SongViewHolder.VIEW_TYPE
            else -> super.getItemViewType(position)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        if (viewType == SongViewHolder.VIEW_TYPE) {
            SongViewHolder.from(parent)
        } else {
            super.onCreateViewHolder(parent, viewType)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = getItem(position)
        if (item is Song) {
            (holder as SongViewHolder).bind(item, listener)
        }
    }

    override fun isItemFullWidth(position: Int): Boolean {
        if (super.isItemFullWidth(position)) {
            return true
        }
        // Playlist headers should be full-width in all configurations
        return getItem(position) is Playlist
    }

    companion object {
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Item>() {
                override fun areContentsTheSame(oldItem: Item, newItem: Item) =
                    when {
                        oldItem is Song && newItem is Song ->
                            SongViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        else -> DetailListAdapter.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                    }
            }
    }
}
