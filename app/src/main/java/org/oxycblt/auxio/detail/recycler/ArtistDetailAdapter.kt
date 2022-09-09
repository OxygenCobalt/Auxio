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

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemDetailBinding
import org.oxycblt.auxio.databinding.ItemParentBinding
import org.oxycblt.auxio.databinding.ItemSongBinding
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.resolveYear
import org.oxycblt.auxio.ui.recycler.ArtistViewHolder
import org.oxycblt.auxio.ui.recycler.IndicatorAdapter
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.ui.recycler.MenuItemListener
import org.oxycblt.auxio.ui.recycler.SimpleItemCallback
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.inflater

/**
 * An adapter for displaying [Artist] information and it's children. Unlike the other adapters, this
 * one actually contains both album information and song information.
 * @author OxygenCobalt
 */
class ArtistDetailAdapter(private val listener: Listener) :
    DetailAdapter<DetailAdapter.Listener>(listener, DIFFER) {

    override fun getItemViewType(position: Int) =
        when (differ.currentList[position]) {
            is Artist -> ArtistDetailViewHolder.VIEW_TYPE
            is Album -> ArtistAlbumViewHolder.VIEW_TYPE
            is Song -> ArtistSongViewHolder.VIEW_TYPE
            else -> super.getItemViewType(position)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            ArtistDetailViewHolder.VIEW_TYPE -> ArtistDetailViewHolder.new(parent)
            ArtistAlbumViewHolder.VIEW_TYPE -> ArtistAlbumViewHolder.new(parent)
            ArtistSongViewHolder.VIEW_TYPE -> ArtistSongViewHolder.new(parent)
            else -> super.onCreateViewHolder(parent, viewType)
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: List<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)

        if (payloads.isEmpty()) {
            when (val item = differ.currentList[position]) {
                is Artist -> (holder as ArtistDetailViewHolder).bind(item, listener)
                is Album -> (holder as ArtistAlbumViewHolder).bind(item, listener)
                is Song -> (holder as ArtistSongViewHolder).bind(item, listener)
            }
        }
    }

    override fun isItemFullWidth(position: Int): Boolean {
        val item = differ.currentList[position]
        return super.isItemFullWidth(position) || item is Artist
    }

    companion object {
        private val DIFFER =
            object : SimpleItemCallback<Item>() {
                override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                    return when {
                        oldItem is Artist && newItem is Artist ->
                            ArtistDetailViewHolder.DIFFER.areContentsTheSame(oldItem, newItem)
                        oldItem is Album && newItem is Album ->
                            ArtistAlbumViewHolder.DIFFER.areContentsTheSame(oldItem, newItem)
                        oldItem is Song && newItem is Song ->
                            ArtistSongViewHolder.DIFFER.areContentsTheSame(oldItem, newItem)
                        else -> DetailAdapter.DIFFER.areContentsTheSame(oldItem, newItem)
                    }
                }
            }
    }
}

private class ArtistDetailViewHolder private constructor(private val binding: ItemDetailBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Artist, listener: DetailAdapter.Listener) {
        binding.detailCover.bind(item)
        binding.detailType.text = binding.context.getString(R.string.lbl_artist)
        binding.detailName.text = item.resolveName(binding.context)

        // Get the genre that corresponds to the most songs in this artist, which would be
        // the most "Prominent" genre.
        val genresByAmount = mutableMapOf<Genre, Int>()
        for (song in item.songs) {
            for (genre in song.genres) {
                genresByAmount[genre] = genresByAmount[genre]?.inc() ?: 1
            }
        }

        binding.detailSubhead.text =
            genresByAmount.maxByOrNull { it.value }?.key?.resolveName(binding.context)
                ?: binding.context.getString(R.string.def_genre)

        binding.detailInfo.text =
            binding.context.getString(
                R.string.fmt_two,
                binding.context.getPlural(R.plurals.fmt_album_count, item.albums.size),
                binding.context.getPlural(R.plurals.fmt_song_count, item.songs.size))

        binding.detailPlayButton.setOnClickListener { listener.onPlayParent() }
        binding.detailShuffleButton.setOnClickListener { listener.onShuffleParent() }
    }

    companion object {
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_ARTIST_DETAIL

        fun new(parent: View) =
            ArtistDetailViewHolder(ItemDetailBinding.inflate(parent.context.inflater))

        val DIFFER = ArtistViewHolder.DIFFER
    }
}

private class ArtistAlbumViewHolder private constructor(private val binding: ItemParentBinding) :
    IndicatorAdapter.ViewHolder(binding.root) {
    fun bind(item: Album, listener: MenuItemListener) {
        binding.parentImage.bind(item)
        binding.parentName.text = item.resolveName(binding.context)
        binding.parentInfo.text = item.date.resolveYear(binding.context)
        // binding.parentMenu.setOnClickListener { listener.onOpenMenu(item, it) }
        binding.root.setOnLongClickListener {
            listener.onOpenMenu(item, it)
            true
        }
        binding.root.setOnClickListener { listener.onItemClick(item) }
    }

    override fun updateIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.root.isActivated = isActive
        binding.parentImage.isPlaying = isPlaying
    }

    companion object {
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_ARTIST_ALBUM

        fun new(parent: View) =
            ArtistAlbumViewHolder(ItemParentBinding.inflate(parent.context.inflater))

        val DIFFER =
            object : SimpleItemCallback<Album>() {
                override fun areContentsTheSame(oldItem: Album, newItem: Album) =
                    oldItem.rawName == newItem.rawName && oldItem.date == newItem.date
            }
    }
}

private class ArtistSongViewHolder private constructor(private val binding: ItemSongBinding) :
    IndicatorAdapter.ViewHolder(binding.root) {
    fun bind(item: Song, listener: MenuItemListener) {
        binding.songAlbumCover.bind(item)
        binding.songName.text = item.resolveName(binding.context)
        binding.songInfo.text = item.album.resolveName(binding.context)
        // binding.songMenu.setOnClickListener { listener.onOpenMenu(item, it) }
        binding.root.setOnLongClickListener {
            listener.onOpenMenu(item, it)
            true
        }
        binding.root.setOnClickListener { listener.onItemClick(item) }
    }

    override fun updateIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.root.isActivated = isActive
        binding.songAlbumCover.isPlaying = isPlaying
    }

    companion object {
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_ARTIST_SONG

        fun new(parent: View) =
            ArtistSongViewHolder(ItemSongBinding.inflate(parent.context.inflater))

        val DIFFER =
            object : SimpleItemCallback<Song>() {
                override fun areContentsTheSame(oldItem: Song, newItem: Song) =
                    oldItem.rawName == newItem.rawName &&
                        oldItem.album.rawName == newItem.album.rawName
            }
    }
}
