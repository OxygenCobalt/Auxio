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
import org.oxycblt.auxio.coil.bindAlbumCover
import org.oxycblt.auxio.coil.bindGenreImage
import org.oxycblt.auxio.databinding.ItemDetailBinding
import org.oxycblt.auxio.databinding.ItemSongBinding
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.BindingViewHolder
import org.oxycblt.auxio.ui.Item
import org.oxycblt.auxio.ui.MenuItemListener
import org.oxycblt.auxio.ui.SimpleItemCallback
import org.oxycblt.auxio.ui.SongViewHolder
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getPluralSafe
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.textSafe

/**
 * An adapter for displaying genre information and it's children.
 * @author OxygenCobalt
 */
class GenreDetailAdapter(listener: DetailItemListener) :
    DetailAdapter<DetailItemListener>(listener, DIFFER) {
    private var currentSong: Song? = null
    private var currentHolder: Highlightable? = null

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
        listener: DetailItemListener
    ) {
        super.onBind(viewHolder, item, listener)
        when (item) {
            is Genre -> (viewHolder as GenreDetailViewHolder).bind(item, listener)
            is Song -> (viewHolder as GenreSongViewHolder).bind(item, listener)
            else -> {}
        }
    }

    override fun onHighlightViewHolder(viewHolder: Highlightable, item: Item) {
        // If the item corresponds to a currently playing song/album then highlight it
        if (item.id == currentSong?.id) {
            // Reset the last ViewHolder before assigning the new, correct one to be highlighted
            currentHolder?.setHighlighted(false)
            currentHolder = viewHolder
            viewHolder.setHighlighted(true)
        } else {
            viewHolder.setHighlighted(false)
        }
    }

    /**
     * Update the [song] that this adapter should highlight
     * @param recycler The recyclerview the highlighting should act on.
     */
    fun highlightSong(song: Song?, recycler: RecyclerView) {
        if (song == currentSong) return
        currentSong = song
        currentHolder?.setHighlighted(false)
        currentHolder = highlightItem(song, recycler)
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
    BindingViewHolder<Genre, DetailItemListener>(binding.root) {
    override fun bind(item: Genre, listener: DetailItemListener) {
        binding.detailCover.bindGenreImage(item)
        binding.detailName.textSafe = item.resolvedName
        binding.detailSubhead.textSafe =
            binding.context.getPluralSafe(R.plurals.fmt_song_count, item.songs.size)
        binding.detailInfo.textSafe = item.totalDuration
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
                    oldItem.resolvedName == newItem.resolvedName &&
                        oldItem.songs.size == newItem.songs.size &&
                        oldItem.totalDuration == newItem.totalDuration
            }
    }
}

class GenreSongViewHolder private constructor(private val binding: ItemSongBinding) :
    BindingViewHolder<Song, MenuItemListener>(binding.root), Highlightable {
    override fun bind(item: Song, listener: MenuItemListener) {
        binding.songAlbumCover.bindAlbumCover(item)
        binding.songName.textSafe = item.resolvedName
        binding.songInfo.textSafe = item.resolvedArtistName
        binding.root.apply {
            setOnClickListener { listener.onItemClick(item) }
            setOnLongClickListener { view ->
                listener.onOpenMenu(item, view)
                true
            }
        }
    }

    override fun setHighlighted(isHighlighted: Boolean) {
        binding.songName.isActivated = isHighlighted
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
