/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.ui.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemHeaderBinding
import org.oxycblt.auxio.databinding.ItemParentBinding
import org.oxycblt.auxio.databinding.ItemSongBinding
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.inflater

/**
 * The shared ViewHolder for a [Song].
 * @author OxygenCobalt
 */
class SongViewHolder private constructor(private val binding: ItemSongBinding) :
    PlayingIndicatorAdapter.ViewHolder(binding.root) {
    fun bind(item: Song, listener: MenuItemListener) {
        binding.songAlbumCover.bind(item)
        binding.songName.text = item.resolveName(binding.context)
        binding.songInfo.text = item.resolveArtistContents(binding.context)
        binding.songMenu.setOnClickListener { listener.onOpenMenu(item, it) }
        binding.root.setOnClickListener { listener.onItemClick(item) }
    }

    override fun updatePlayingIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.root.isSelected = isActive
        binding.songAlbumCover.isPlaying = isPlaying
    }

    companion object {
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_SONG

        fun new(parent: View) = SongViewHolder(ItemSongBinding.inflate(parent.context.inflater))

        val DIFFER =
            object : SimpleItemCallback<Song>() {
                override fun areContentsTheSame(oldItem: Song, newItem: Song) =
                    oldItem.rawName == newItem.rawName && oldItem.areArtistContentsTheSame(newItem)
            }
    }
}

/**
 * The Shared ViewHolder for a [Album].
 * @author OxygenCobalt
 */
class AlbumViewHolder private constructor(private val binding: ItemParentBinding) :
    PlayingIndicatorAdapter.ViewHolder(binding.root) {

    fun bind(item: Album, listener: MenuItemListener) {
        binding.parentImage.bind(item)
        binding.parentName.text = item.resolveName(binding.context)
        binding.parentInfo.text = item.resolveArtistContents(binding.context)
        binding.parentMenu.setOnClickListener { listener.onOpenMenu(item, it) }
        binding.root.setOnClickListener { listener.onItemClick(item) }
    }

    override fun updatePlayingIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.root.isSelected = isActive
        binding.parentImage.isPlaying = isPlaying
    }

    companion object {
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_ALBUM

        fun new(parent: View) = AlbumViewHolder(ItemParentBinding.inflate(parent.context.inflater))

        val DIFFER =
            object : SimpleItemCallback<Album>() {
                override fun areContentsTheSame(oldItem: Album, newItem: Album) =
                    oldItem.rawName == newItem.rawName &&
                        oldItem.areArtistContentsTheSame(newItem) &&
                        oldItem.releaseType == newItem.releaseType
            }
    }
}

/**
 * The Shared ViewHolder for a [Artist].
 * @author OxygenCobalt
 */
class ArtistViewHolder private constructor(private val binding: ItemParentBinding) :
    PlayingIndicatorAdapter.ViewHolder(binding.root) {

    fun bind(item: Artist, listener: MenuItemListener) {
        binding.parentImage.bind(item)
        binding.parentName.text = item.resolveName(binding.context)

        binding.parentInfo.text =
            if (item.songs.isNotEmpty()) {
                binding.context.getString(
                    R.string.fmt_two,
                    binding.context.getPlural(R.plurals.fmt_album_count, item.albums.size),
                    binding.context.getPlural(R.plurals.fmt_song_count, item.songs.size))
            } else {
                // Artist has no songs, only display an album count.
                binding.context.getPlural(R.plurals.fmt_album_count, item.albums.size)
            }

        binding.parentMenu.setOnClickListener { listener.onOpenMenu(item, it) }
        binding.root.setOnClickListener { listener.onItemClick(item) }
    }

    override fun updatePlayingIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.root.isSelected = isActive
        binding.parentImage.isPlaying = isPlaying
    }

    companion object {
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_ARTIST

        fun new(parent: View) = ArtistViewHolder(ItemParentBinding.inflate(parent.context.inflater))

        val DIFFER =
            object : SimpleItemCallback<Artist>() {
                override fun areContentsTheSame(oldItem: Artist, newItem: Artist) =
                    oldItem.rawName == newItem.rawName &&
                        oldItem.albums.size == newItem.albums.size &&
                        oldItem.songs.size == newItem.songs.size
            }
    }
}

/**
 * The Shared ViewHolder for a [Genre].
 * @author OxygenCobalt
 */
class GenreViewHolder private constructor(private val binding: ItemParentBinding) :
    PlayingIndicatorAdapter.ViewHolder(binding.root) {

    fun bind(item: Genre, listener: MenuItemListener) {
        binding.parentImage.bind(item)
        binding.parentName.text = item.resolveName(binding.context)
        binding.parentInfo.text =
            binding.context.getString(
                R.string.fmt_two,
                binding.context.getPlural(R.plurals.fmt_artist_count, item.artists.size),
                binding.context.getPlural(R.plurals.fmt_song_count, item.songs.size))
        binding.parentMenu.setOnClickListener { listener.onOpenMenu(item, it) }
        binding.root.setOnClickListener { listener.onItemClick(item) }
    }

    override fun updatePlayingIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.root.isSelected = isActive
        binding.parentImage.isPlaying = isPlaying
    }

    companion object {
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_GENRE

        fun new(parent: View) = GenreViewHolder(ItemParentBinding.inflate(parent.context.inflater))

        val DIFFER =
            object : SimpleItemCallback<Genre>() {
                override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean =
                    oldItem.rawName == newItem.rawName && oldItem.songs.size == newItem.songs.size
            }
    }
}

/**
 * The Shared ViewHolder for a [Header].
 * @author OxygenCobalt
 */
class HeaderViewHolder private constructor(private val binding: ItemHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Header) {
        binding.title.text = binding.context.getString(item.string)
    }

    companion object {
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_HEADER

        fun new(parent: View) = HeaderViewHolder(ItemHeaderBinding.inflate(parent.context.inflater))

        val DIFFER =
            object : SimpleItemCallback<Header>() {
                override fun areContentsTheSame(oldItem: Header, newItem: Header): Boolean =
                    oldItem.string == newItem.string
            }
    }
}
