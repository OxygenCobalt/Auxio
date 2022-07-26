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

import android.content.Context
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
import org.oxycblt.auxio.util.getPluralSafe
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.textSafe

/**
 * The shared ViewHolder for a [Song].
 * @author OxygenCobalt
 */
class SongViewHolder private constructor(private val binding: ItemSongBinding) :
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
            object : Creator<SongViewHolder> {
                override val viewType: Int
                    get() = IntegerTable.ITEM_TYPE_SONG

                override fun create(context: Context) =
                    SongViewHolder(ItemSongBinding.inflate(context.inflater))
            }

        val DIFFER =
            object : SimpleItemCallback<Song>() {
                override fun areItemsTheSame(oldItem: Song, newItem: Song) =
                    oldItem.rawName == newItem.rawName &&
                        oldItem.individualArtistRawName == oldItem.individualArtistRawName
            }
    }
}

/**
 * The Shared ViewHolder for a [Album].
 * @author OxygenCobalt
 */
class AlbumViewHolder
private constructor(
    private val binding: ItemParentBinding,
) : BindingViewHolder<Album, MenuItemListener>(binding.root) {

    override fun bind(item: Album, listener: MenuItemListener) {
        binding.parentImage.bind(item)
        binding.parentName.textSafe = item.resolveName(binding.context)
        binding.parentInfo.textSafe = item.artist.resolveName(binding.context)
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
            object : Creator<AlbumViewHolder> {
                override val viewType: Int
                    get() = IntegerTable.ITEM_TYPE_ALBUM

                override fun create(context: Context) =
                    AlbumViewHolder(ItemParentBinding.inflate(context.inflater))
            }

        val DIFFER =
            object : SimpleItemCallback<Album>() {
                override fun areItemsTheSame(oldItem: Album, newItem: Album) =
                    oldItem.rawName == newItem.rawName &&
                        oldItem.artist.rawName == newItem.artist.rawName &&
                        oldItem.releaseType == newItem.releaseType
            }
    }
}

/**
 * The Shared ViewHolder for a [Artist].
 * @author OxygenCobalt
 */
class ArtistViewHolder private constructor(private val binding: ItemParentBinding) :
    BindingViewHolder<Artist, MenuItemListener>(binding.root) {

    override fun bind(item: Artist, listener: MenuItemListener) {
        binding.parentImage.bind(item)
        binding.parentName.textSafe = item.resolveName(binding.context)
        binding.parentInfo.textSafe =
            binding.context.getString(
                R.string.fmt_two,
                binding.context.getPluralSafe(R.plurals.fmt_album_count, item.albums.size),
                binding.context.getPluralSafe(R.plurals.fmt_song_count, item.songs.size))
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
            object : Creator<ArtistViewHolder> {
                override val viewType: Int
                    get() = IntegerTable.ITEM_TYPE_ARTIST

                override fun create(context: Context) =
                    ArtistViewHolder(ItemParentBinding.inflate(context.inflater))
            }

        val DIFFER =
            object : SimpleItemCallback<Artist>() {
                override fun areItemsTheSame(oldItem: Artist, newItem: Artist) =
                    oldItem.rawName == newItem.rawName &&
                        oldItem.albums.size == newItem.albums.size &&
                        newItem.songs.size == newItem.songs.size
            }
    }
}

/**
 * The Shared ViewHolder for a [Genre].
 * @author OxygenCobalt
 */
class GenreViewHolder
private constructor(
    private val binding: ItemParentBinding,
) : BindingViewHolder<Genre, MenuItemListener>(binding.root) {

    override fun bind(item: Genre, listener: MenuItemListener) {
        binding.parentImage.bind(item)
        binding.parentName.textSafe = item.resolveName(binding.context)
        binding.parentInfo.textSafe =
            binding.context.getPluralSafe(R.plurals.fmt_song_count, item.songs.size)
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
            object : Creator<GenreViewHolder> {
                override val viewType: Int
                    get() = IntegerTable.ITEM_TYPE_GENRE

                override fun create(context: Context) =
                    GenreViewHolder(ItemParentBinding.inflate(context.inflater))
            }

        val DIFFER =
            object : SimpleItemCallback<Genre>() {
                override fun areItemsTheSame(oldItem: Genre, newItem: Genre): Boolean =
                    oldItem.rawName == newItem.rawName && oldItem.songs.size == newItem.songs.size
            }
    }
}

/**
 * The Shared ViewHolder for a [Header].
 * @author OxygenCobalt
 */
class HeaderViewHolder private constructor(private val binding: ItemHeaderBinding) :
    BindingViewHolder<Header, Unit>(binding.root) {

    override fun bind(item: Header, listener: Unit) {
        binding.title.textSafe = binding.context.getString(item.string)
    }

    companion object {
        val CREATOR =
            object : Creator<HeaderViewHolder> {
                override val viewType: Int
                    get() = IntegerTable.ITEM_TYPE_HEADER

                override fun create(context: Context) =
                    HeaderViewHolder(ItemHeaderBinding.inflate(context.inflater))
            }

        val DIFFER =
            object : SimpleItemCallback<Header>() {
                override fun areItemsTheSame(oldItem: Header, newItem: Header): Boolean =
                    oldItem.string == newItem.string
            }
    }
}
