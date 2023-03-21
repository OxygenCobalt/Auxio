/*
 * Copyright (c) 2022 Auxio Project
 * ViewHolders.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemHeaderBinding
import org.oxycblt.auxio.databinding.ItemParentBinding
import org.oxycblt.auxio.databinding.ItemSongBinding
import org.oxycblt.auxio.list.BasicHeader
import org.oxycblt.auxio.list.SelectableListListener
import org.oxycblt.auxio.list.adapter.SelectionIndicatorAdapter
import org.oxycblt.auxio.list.adapter.SimpleDiffCallback
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.logD

/**
 * A [RecyclerView.ViewHolder] that displays a [Song]. Use [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class SongViewHolder private constructor(private val binding: ItemSongBinding) :
    SelectionIndicatorAdapter.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param song The new [Song] to bind.
     * @param listener An [SelectableListListener] to bind interactions to.
     */
    fun bind(song: Song, listener: SelectableListListener<Song>) {
        listener.bind(song, this, menuButton = binding.songMenu)
        binding.songAlbumCover.bind(song)
        binding.songName.text = song.resolveName(binding.context)
        binding.songInfo.text = song.artists.resolveNames(binding.context)
    }

    override fun updatePlayingIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.root.isSelected = isActive
        binding.songAlbumCover.isPlaying = isPlaying
    }

    override fun updateSelectionIndicator(isSelected: Boolean) {
        binding.root.isActivated = isSelected
    }

    companion object {
        /** Unique ID for this ViewHolder type. */
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_SONG

        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) = SongViewHolder(ItemSongBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Song>() {
                override fun areContentsTheSame(oldItem: Song, newItem: Song) =
                    oldItem.rawName == newItem.rawName &&
                        oldItem.artists.areRawNamesTheSame(newItem.artists)
            }
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays a [Album]. Use [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class AlbumViewHolder private constructor(private val binding: ItemParentBinding) :
    SelectionIndicatorAdapter.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param album The new [Album] to bind.
     * @param listener An [SelectableListListener] to bind interactions to.
     */
    fun bind(album: Album, listener: SelectableListListener<Album>) {
        listener.bind(album, this, menuButton = binding.parentMenu)
        binding.parentImage.bind(album)
        binding.parentName.text = album.resolveName(binding.context)
        binding.parentInfo.text = album.artists.resolveNames(binding.context)
    }

    override fun updatePlayingIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.root.isSelected = isActive
        binding.parentImage.isPlaying = isPlaying
    }

    override fun updateSelectionIndicator(isSelected: Boolean) {
        binding.root.isActivated = isSelected
    }

    companion object {
        /** Unique ID for this ViewHolder type. */
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_ALBUM

        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) = AlbumViewHolder(ItemParentBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Album>() {
                override fun areContentsTheSame(oldItem: Album, newItem: Album) =
                    oldItem.rawName == newItem.rawName &&
                        oldItem.artists.areRawNamesTheSame(newItem.artists) &&
                        oldItem.releaseType == newItem.releaseType
            }
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays a [Artist]. Use [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class ArtistViewHolder private constructor(private val binding: ItemParentBinding) :
    SelectionIndicatorAdapter.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param artist The new [Artist] to bind.
     * @param listener An [SelectableListListener] to bind interactions to.
     */
    fun bind(artist: Artist, listener: SelectableListListener<Artist>) {
        listener.bind(artist, this, menuButton = binding.parentMenu)
        binding.parentImage.bind(artist)
        binding.parentName.text = artist.resolveName(binding.context)
        binding.parentInfo.text =
            if (artist.songs.isNotEmpty()) {
                binding.context.getString(
                    R.string.fmt_two,
                    binding.context.getPlural(R.plurals.fmt_album_count, artist.albums.size),
                    binding.context.getPlural(R.plurals.fmt_song_count, artist.songs.size))
            } else {
                // Artist has no songs, only display an album count.
                binding.context.getPlural(R.plurals.fmt_album_count, artist.albums.size)
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
        /** Unique ID for this ViewHolder type. */
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_ARTIST

        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            ArtistViewHolder(ItemParentBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Artist>() {
                override fun areContentsTheSame(oldItem: Artist, newItem: Artist) =
                    oldItem.rawName == newItem.rawName &&
                        oldItem.albums.size == newItem.albums.size &&
                        oldItem.songs.size == newItem.songs.size
            }
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays a [Genre]. Use [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class GenreViewHolder private constructor(private val binding: ItemParentBinding) :
    SelectionIndicatorAdapter.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param genre The new [Genre] to bind.
     * @param listener An [SelectableListListener] to bind interactions to.
     */
    fun bind(genre: Genre, listener: SelectableListListener<Genre>) {
        listener.bind(genre, this, menuButton = binding.parentMenu)
        binding.parentImage.bind(genre)
        binding.parentName.text = genre.resolveName(binding.context)
        binding.parentInfo.text =
            binding.context.getString(
                R.string.fmt_two,
                binding.context.getPlural(R.plurals.fmt_artist_count, genre.artists.size),
                binding.context.getPlural(R.plurals.fmt_song_count, genre.songs.size))
    }

    override fun updatePlayingIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.root.isSelected = isActive
        binding.parentImage.isPlaying = isPlaying
    }

    override fun updateSelectionIndicator(isSelected: Boolean) {
        binding.root.isActivated = isSelected
    }

    companion object {
        /** Unique ID for this ViewHolder type. */
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_GENRE

        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) = GenreViewHolder(ItemParentBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Genre>() {
                override fun areContentsTheSame(oldItem: Genre, newItem: Genre): Boolean =
                    oldItem.rawName == newItem.rawName &&
                        oldItem.artists.size == newItem.artists.size &&
                        oldItem.songs.size == newItem.songs.size
            }
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays a [Playlist]. Use [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class PlaylistViewHolder private constructor(private val binding: ItemParentBinding) :
    SelectionIndicatorAdapter.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param playlist The new [Playlist] to bind.
     * @param listener An [SelectableListListener] to bind interactions to.
     */
    fun bind(playlist: Playlist, listener: SelectableListListener<Playlist>) {
        listener.bind(playlist, this, menuButton = binding.parentMenu)
        binding.parentImage.bind(playlist)
        binding.parentName.text = playlist.resolveName(binding.context)
        binding.parentInfo.text =
            binding.context.getPlural(R.plurals.fmt_song_count, playlist.songs.size)
    }

    override fun updatePlayingIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.root.isSelected = isActive
        binding.parentImage.isPlaying = isPlaying
    }

    override fun updateSelectionIndicator(isSelected: Boolean) {
        binding.root.isActivated = isSelected
    }

    companion object {
        /** Unique ID for this ViewHolder type. */
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_PLAYLIST

        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            PlaylistViewHolder(ItemParentBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Playlist>() {
                override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean =
                    oldItem.rawName == newItem.rawName && oldItem.songs.size == newItem.songs.size
            }
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays a [BasicHeader]. Use [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class BasicHeaderViewHolder private constructor(private val binding: ItemHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param basicHeader The new [BasicHeader] to bind.
     */
    fun bind(basicHeader: BasicHeader) {
        logD(binding.context.getString(basicHeader.titleRes))
        binding.title.text = binding.context.getString(basicHeader.titleRes)
    }

    companion object {
        /** Unique ID for this ViewHolder type. */
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_BASIC_HEADER

        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            BasicHeaderViewHolder(ItemHeaderBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<BasicHeader>() {
                override fun areContentsTheSame(
                    oldItem: BasicHeader,
                    newItem: BasicHeader
                ): Boolean = oldItem.titleRes == newItem.titleRes
            }
    }
}
