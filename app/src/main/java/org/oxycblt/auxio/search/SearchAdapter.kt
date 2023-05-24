/*
 * Copyright (c) 2021 Auxio Project
 * SearchAdapter.kt is part of Auxio.
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
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.list.BasicHeader
import org.oxycblt.auxio.list.Divider
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.SelectableListListener
import org.oxycblt.auxio.list.adapter.SelectionIndicatorAdapter
import org.oxycblt.auxio.list.adapter.SimpleDiffCallback
import org.oxycblt.auxio.list.recycler.AlbumViewHolder
import org.oxycblt.auxio.list.recycler.ArtistViewHolder
import org.oxycblt.auxio.list.recycler.BasicHeaderViewHolder
import org.oxycblt.auxio.list.recycler.DividerViewHolder
import org.oxycblt.auxio.list.recycler.GenreViewHolder
import org.oxycblt.auxio.list.recycler.PlaylistViewHolder
import org.oxycblt.auxio.list.recycler.SongViewHolder
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.logD

/**
 * An adapter that displays search results.
 *
 * @param listener An [SelectableListListener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class SearchAdapter(private val listener: SelectableListListener<Music>) :
    SelectionIndicatorAdapter<Item, RecyclerView.ViewHolder>(DIFF_CALLBACK) {

    override fun getItemViewType(position: Int) =
        when (getItem(position)) {
            is Song -> SongViewHolder.VIEW_TYPE
            is Album -> AlbumViewHolder.VIEW_TYPE
            is Artist -> ArtistViewHolder.VIEW_TYPE
            is Genre -> GenreViewHolder.VIEW_TYPE
            is Playlist -> PlaylistViewHolder.VIEW_TYPE
            is Divider -> DividerViewHolder.VIEW_TYPE
            is BasicHeader -> BasicHeaderViewHolder.VIEW_TYPE
            else -> super.getItemViewType(position)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            SongViewHolder.VIEW_TYPE -> SongViewHolder.from(parent)
            AlbumViewHolder.VIEW_TYPE -> AlbumViewHolder.from(parent)
            ArtistViewHolder.VIEW_TYPE -> ArtistViewHolder.from(parent)
            GenreViewHolder.VIEW_TYPE -> GenreViewHolder.from(parent)
            PlaylistViewHolder.VIEW_TYPE -> PlaylistViewHolder.from(parent)
            DividerViewHolder.VIEW_TYPE -> DividerViewHolder.from(parent)
            BasicHeaderViewHolder.VIEW_TYPE -> BasicHeaderViewHolder.from(parent)
            else -> error("Invalid item type $viewType")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        logD(position)
        when (val item = getItem(position)) {
            is Song -> (holder as SongViewHolder).bind(item, listener)
            is Album -> (holder as AlbumViewHolder).bind(item, listener)
            is Artist -> (holder as ArtistViewHolder).bind(item, listener)
            is Genre -> (holder as GenreViewHolder).bind(item, listener)
            is Playlist -> (holder as PlaylistViewHolder).bind(item, listener)
            is BasicHeader -> (holder as BasicHeaderViewHolder).bind(item)
        }
    }

    private companion object {
        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Item>() {
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
                        oldItem is Playlist && newItem is Playlist ->
                            PlaylistViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        oldItem is Divider && newItem is Divider ->
                            DividerViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        oldItem is BasicHeader && newItem is BasicHeader ->
                            BasicHeaderViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        else -> false
                    }
            }
    }
}
