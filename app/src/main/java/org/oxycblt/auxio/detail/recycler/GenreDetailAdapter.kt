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
 
package org.oxycblt.auxio.detail.recycler

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemDetailBinding
import org.oxycblt.auxio.databinding.ItemSongBinding
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.recycler.BindingViewHolder
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.ui.recycler.MenuItemListener
import org.oxycblt.auxio.ui.recycler.SimpleItemCallback
import org.oxycblt.auxio.ui.recycler.SongViewHolder
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.formatDuration
import org.oxycblt.auxio.util.getPluralSafe
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.textSafe

/**
 * An adapter for displaying genre information and it's children.
 * @author OxygenCobalt
 */
class GenreDetailAdapter(listener: Listener) :
    DetailAdapter<DetailAdapter.Listener>(listener, DIFFER) {
    private var currentSong: Song? = null

    override fun getCreatorFromItem(item: Item) =
        super.getCreatorFromItem(item)
            ?: when (item) {
                is Genre -> GenreDetailViewHolder.CREATOR
                is Song -> GenreSongViewHolder.CREATOR
                else -> null
            }

    override fun getCreatorFromViewType(viewType: Int) =
        super.getCreatorFromViewType(viewType)
            ?: when (viewType) {
                GenreDetailViewHolder.CREATOR.viewType -> GenreDetailViewHolder.CREATOR
                GenreSongViewHolder.CREATOR.viewType -> GenreSongViewHolder.CREATOR
                else -> null
            }

    override fun onBind(
        viewHolder: RecyclerView.ViewHolder,
        item: Item,
        listener: Listener,
        payload: List<Any>
    ) {
        super.onBind(viewHolder, item, listener, payload)
        if (payload.isEmpty()) {
            when (item) {
                is Genre -> (viewHolder as GenreDetailViewHolder).bind(item, listener)
                is Song -> (viewHolder as GenreSongViewHolder).bind(item, listener)
                else -> {}
            }
        }
    }

    override fun shouldHighlightViewHolder(item: Item) = item is Song && item.id == currentSong?.id

    /** Update the [song] that this adapter should highlight */
    fun highlightSong(song: Song?) {
        if (song == currentSong) return
        highlightImpl(currentSong, song)
        currentSong = song
    }

    companion object {
        val DIFFER =
            object : SimpleItemCallback<Item>() {
                override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                    return when {
                        oldItem is Genre && newItem is Genre ->
                            GenreDetailViewHolder.DIFFER.areItemsTheSame(oldItem, newItem)
                        oldItem is Song && newItem is Song ->
                            GenreSongViewHolder.DIFFER.areItemsTheSame(oldItem, newItem)
                        else -> DetailAdapter.DIFFER.areContentsTheSame(oldItem, newItem)
                    }
                }
            }
    }
}

private class GenreDetailViewHolder private constructor(private val binding: ItemDetailBinding) :
    BindingViewHolder<Genre, DetailAdapter.Listener>(binding.root) {
    override fun bind(item: Genre, listener: DetailAdapter.Listener) {
        binding.detailCover.bind(item)
        binding.detailName.textSafe = item.resolveName(binding.context)
        binding.detailSubhead.textSafe =
            binding.context.getPluralSafe(R.plurals.fmt_song_count, item.songs.size)
        binding.detailInfo.textSafe = item.durationSecs.formatDuration(false)
        binding.detailPlayButton.setOnClickListener { listener.onPlayParent() }
        binding.detailShuffleButton.setOnClickListener { listener.onShuffleParent() }
    }

    companion object {
        val CREATOR =
            object : Creator<GenreDetailViewHolder> {
                override val viewType: Int
                    get() = IntegerTable.ITEM_TYPE_GENRE_DETAIL

                override fun create(context: Context) =
                    GenreDetailViewHolder(ItemDetailBinding.inflate(context.inflater))
            }

        val DIFFER =
            object : SimpleItemCallback<Genre>() {
                override fun areItemsTheSame(oldItem: Genre, newItem: Genre) =
                    oldItem.rawName == newItem.rawName &&
                        oldItem.songs.size == newItem.songs.size &&
                        oldItem.durationSecs == newItem.durationSecs
            }
    }
}

class GenreSongViewHolder private constructor(private val binding: ItemSongBinding) :
    BindingViewHolder<Song, MenuItemListener>(binding.root) {
    override fun bind(item: Song, listener: MenuItemListener) {
        binding.songAlbumCover.bind(item)
        binding.songName.textSafe = item.resolveName(binding.context)
        binding.songInfo.textSafe = item.resolveIndividualArtistName(binding.context)
        binding.root.apply {
            setOnClickListener { listener.onItemClick(item) }
            setOnLongClickListener { view ->
                listener.onOpenMenu(item, view)
                true
            }
        }
    }

    companion object {
        val CREATOR =
            object : Creator<GenreSongViewHolder> {
                override val viewType: Int
                    get() = IntegerTable.ITEM_TYPE_GENRE_SONG

                override fun create(context: Context) =
                    GenreSongViewHolder(ItemSongBinding.inflate(context.inflater))
            }

        val DIFFER = SongViewHolder.DIFFER
    }
}
