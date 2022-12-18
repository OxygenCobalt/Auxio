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
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemDetailBinding
import org.oxycblt.auxio.databinding.ItemParentBinding
import org.oxycblt.auxio.databinding.ItemSongBinding
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.ItemMenuCallback
import org.oxycblt.auxio.list.ItemSelectCallback
import org.oxycblt.auxio.list.recycler.PlayingIndicatorAdapter
import org.oxycblt.auxio.list.recycler.SelectionIndicatorAdapter
import org.oxycblt.auxio.list.recycler.SimpleItemCallback
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.inflater

/**
 * An adapter for displaying [Artist] information and it's children. Unlike the other adapters, this
 * one actually contains both album information and song information.
 * @author OxygenCobalt
 */
class ArtistDetailAdapter(private val callback: Callback) : DetailAdapter(callback, DIFFER) {

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
                is Artist -> (holder as ArtistDetailViewHolder).bind(item, callback)
                is Album -> (holder as ArtistAlbumViewHolder).bind(item, callback)
                is Song -> (holder as ArtistSongViewHolder).bind(item, callback)
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

    fun bind(item: Artist, callback: DetailAdapter.Callback) {
        binding.detailCover.bind(item)
        binding.detailType.text = binding.context.getString(R.string.lbl_artist)
        binding.detailName.text = item.resolveName(binding.context)

        if (item.songs.isNotEmpty()) {
            binding.detailSubhead.apply {
                isVisible = true
                text = item.resolveGenreContents(binding.context)
            }

            binding.detailInfo.text =
                binding.context.getString(
                    R.string.fmt_two,
                    binding.context.getPlural(R.plurals.fmt_album_count, item.albums.size),
                    binding.context.getPlural(R.plurals.fmt_song_count, item.songs.size))

            binding.detailPlayButton.isVisible = true
            binding.detailShuffleButton.isVisible = true
        } else {
            // The artist does not have any songs, so playback, genre info, and song counts
            // make no sense.
            binding.detailSubhead.isVisible = false
            binding.detailInfo.text =
                binding.context.getPlural(R.plurals.fmt_album_count, item.albums.size)
            binding.detailPlayButton.isVisible = false
            binding.detailShuffleButton.isVisible = false
        }

        binding.detailPlayButton.setOnClickListener { callback.onPlay() }
        binding.detailShuffleButton.setOnClickListener { callback.onShuffle() }
    }

    companion object {
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_ARTIST_DETAIL

        fun new(parent: View) =
            ArtistDetailViewHolder(ItemDetailBinding.inflate(parent.context.inflater))

        val DIFFER =
            object : SimpleItemCallback<Artist>() {
                override fun areContentsTheSame(oldItem: Artist, newItem: Artist) =
                    oldItem.rawName == newItem.rawName &&
                        oldItem.areGenreContentsTheSame(newItem) &&
                        oldItem.albums.size == newItem.albums.size &&
                        oldItem.songs.size == newItem.songs.size
            }
    }
}

private class ArtistAlbumViewHolder private constructor(private val binding: ItemParentBinding) :
    SelectionIndicatorAdapter.ViewHolder(binding.root) {
    fun bind(item: Album, callback: ItemSelectCallback) {
        binding.parentImage.bind(item)
        binding.parentName.text = item.resolveName(binding.context)
        binding.parentInfo.text =
            item.date?.resolveDate(binding.context) ?: binding.context.getString(R.string.def_date)
        binding.parentMenu.setOnClickListener { callback.onOpenMenu(item, it) }
        binding.root.setOnClickListener { callback.onClick(item) }
        binding.root.apply {
            setOnClickListener { callback.onClick(item) }
            setOnLongClickListener {
                callback.onSelect(item)
                true
            }
        }
    }

    override fun updatePlayingIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.root.isSelected = isActive
        binding.parentImage.isPlaying = isPlaying
    }

    override fun updateSelectionIndicator(isSelected: Boolean) {
        binding.root.isActivated = isSelected
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
    SelectionIndicatorAdapter.ViewHolder(binding.root) {
    fun bind(item: Song, callback: ItemSelectCallback) {
        binding.songAlbumCover.bind(item)
        binding.songName.text = item.resolveName(binding.context)
        binding.songInfo.text = item.album.resolveName(binding.context)
        binding.songMenu.setOnClickListener { callback.onOpenMenu(item, it) }
        binding.root.setOnClickListener { callback.onClick(item) }
        binding.root.apply {
            setOnClickListener { callback.onClick(item) }
            setOnLongClickListener {
                callback.onSelect(item)
                true
            }
        }
    }

    override fun updatePlayingIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.root.isSelected = isActive
        binding.songAlbumCover.isPlaying = isPlaying
    }

    override fun updateSelectionIndicator(isSelected: Boolean) {
        binding.root.isActivated = isSelected
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
