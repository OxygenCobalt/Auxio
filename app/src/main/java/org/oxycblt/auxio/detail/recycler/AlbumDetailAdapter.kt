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
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemAlbumSongBinding
import org.oxycblt.auxio.databinding.ItemDetailBinding
import org.oxycblt.auxio.databinding.ItemDiscHeaderBinding
import org.oxycblt.auxio.detail.DiscHeader
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.SelectableListListener
import org.oxycblt.auxio.list.adapter.SelectionIndicatorAdapter
import org.oxycblt.auxio.list.adapter.SimpleDiffCallback
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.inflater

/**
 * An [DetailAdapter] implementing the header and sub-items for the [Album] detail view.
 * @param listener A [Listener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class AlbumDetailAdapter(private val listener: Listener) : DetailAdapter(listener, DIFF_CALLBACK) {
    /**
     * An extension to [DetailAdapter.Listener] that enables interactions specific to the album
     * detail view.
     */
    interface Listener : DetailAdapter.Listener<Song> {
        /**
         * Called when the artist name in the [Album] header was clicked, requesting navigation to
         * it's parent artist.
         */
        fun onNavigateToParentArtist()
    }

    override fun getItemViewType(position: Int) =
        when (getItem(position)) {
            // Support the Album header, sub-headers for each disc, and special album songs.
            is Album -> AlbumDetailViewHolder.VIEW_TYPE
            is DiscHeader -> DiscHeaderViewHolder.VIEW_TYPE
            is Song -> AlbumSongViewHolder.VIEW_TYPE
            else -> super.getItemViewType(position)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            AlbumDetailViewHolder.VIEW_TYPE -> AlbumDetailViewHolder.from(parent)
            DiscHeaderViewHolder.VIEW_TYPE -> DiscHeaderViewHolder.from(parent)
            AlbumSongViewHolder.VIEW_TYPE -> AlbumSongViewHolder.from(parent)
            else -> super.onCreateViewHolder(parent, viewType)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (val item = getItem(position)) {
            is Album -> (holder as AlbumDetailViewHolder).bind(item, listener)
            is DiscHeader -> (holder as DiscHeaderViewHolder).bind(item)
            is Song -> (holder as AlbumSongViewHolder).bind(item, listener)
        }
    }

    override fun isItemFullWidth(position: Int): Boolean {
        if (super.isItemFullWidth(position)) {
            return true
        }
        // The album and disc headers should be full-width in all configurations.
        val item = getItem(position)
        return item is Album || item is DiscHeader
    }

    private companion object {
        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Item>() {
                override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                    return when {
                        oldItem is Album && newItem is Album ->
                            AlbumDetailViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        oldItem is DiscHeader && newItem is DiscHeader ->
                            DiscHeaderViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        oldItem is Song && newItem is Song ->
                            AlbumSongViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)

                        // Fall back to DetailAdapter's differ to handle other headers.
                        else -> DetailAdapter.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                    }
                }
            }
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays the [Album] header in the detail view. Use [from] to
 * create an instance.
 * @author Alexander Capehart (OxygenCobalt)
 */
private class AlbumDetailViewHolder private constructor(private val binding: ItemDetailBinding) :
    RecyclerView.ViewHolder(binding.root) {

    /**
     * Bind new data to this instance.
     * @param album The new [Album] to bind.
     * @param listener A [AlbumDetailAdapter.Listener] to bind interactions to.
     */
    fun bind(album: Album, listener: AlbumDetailAdapter.Listener) {
        binding.detailCover.bind(album)

        // The type text depends on the release type (Album, EP, Single, etc.)
        binding.detailType.text = binding.context.getString(album.releaseType.stringRes)

        binding.detailName.text = album.resolveName(binding.context)

        // Artist name maps to the subhead text
        binding.detailSubhead.apply {
            text = album.resolveArtistContents(context)

            // Add a QoL behavior where navigation to the artist will occur if the artist
            // name is pressed.
            setOnClickListener { listener.onNavigateToParentArtist() }
        }

        // Date, song count, and duration map to the info text
        binding.detailInfo.apply {
            // Fall back to a friendlier "No date" text if the album doesn't have date information
            val date = album.dates?.resolveDate(context) ?: context.getString(R.string.def_date)
            val songCount = context.getPlural(R.plurals.fmt_song_count, album.songs.size)
            val duration = album.durationMs.formatDurationMs(true)
            text = context.getString(R.string.fmt_three, date, songCount, duration)
        }

        binding.detailPlayButton.setOnClickListener { listener.onPlay() }
        binding.detailShuffleButton.setOnClickListener { listener.onShuffle() }
    }

    companion object {
        /** A unique ID for this [RecyclerView.ViewHolder] type. */
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_ALBUM_DETAIL

        /**
         * Create a new instance.
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            AlbumDetailViewHolder(ItemDetailBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Album>() {
                override fun areContentsTheSame(oldItem: Album, newItem: Album) =
                    oldItem.rawName == newItem.rawName &&
                        oldItem.areArtistContentsTheSame(newItem) &&
                        oldItem.dates == newItem.dates &&
                        oldItem.songs.size == newItem.songs.size &&
                        oldItem.durationMs == newItem.durationMs &&
                        oldItem.releaseType == newItem.releaseType
            }
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays a [DiscHeader] to delimit different disc groups. Use
 * [from] to create an instance.
 * @author Alexander Capehart (OxygenCobalt)
 */
private class DiscHeaderViewHolder(private val binding: ItemDiscHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     * @param discHeader The new [DiscHeader] to bind.
     */
    fun bind(discHeader: DiscHeader) {
        binding.discNo.text = binding.context.getString(R.string.fmt_disc_no, discHeader.disc)
    }

    companion object {
        /** A unique ID for this [RecyclerView.ViewHolder] type. */
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_DISC_HEADER

        /**
         * Create a new instance.
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            DiscHeaderViewHolder(ItemDiscHeaderBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<DiscHeader>() {
                override fun areContentsTheSame(oldItem: DiscHeader, newItem: DiscHeader) =
                    oldItem.disc == newItem.disc
            }
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays a [Song] in the context of an [Album]. Use [from] to
 * create an instance.
 * @author Alexander Capehart (OxygenCobalt)
 */
private class AlbumSongViewHolder private constructor(private val binding: ItemAlbumSongBinding) :
    SelectionIndicatorAdapter.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     * @param song The new [Song] to bind.
     * @param listener A [SelectableListListener] to bind interactions to.
     */
    fun bind(song: Song, listener: SelectableListListener<Song>) {
        listener.bind(song, this, menuButton = binding.songMenu)

        binding.songTrack.apply {
            if (song.track != null) {
                // Instead of an album cover, we show the track number, as the song list
                // within the album detail view would have homogeneous album covers otherwise.
                text = context.getString(R.string.fmt_number, song.track)
                isInvisible = false
                contentDescription = context.getString(R.string.desc_track_number, song.track)
            } else {
                // No track, do not show a number, instead showing a generic icon.
                text = ""
                isInvisible = true
                contentDescription = context.getString(R.string.def_track)
            }
        }

        binding.songName.text = song.resolveName(binding.context)

        // Use duration instead of album or artist for each song, as this text would
        // be homogenous otherwise.
        binding.songDuration.text = song.durationMs.formatDurationMs(false)
    }

    override fun updatePlayingIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.root.isSelected = isActive
        binding.songTrackBg.isPlaying = isPlaying
    }

    override fun updateSelectionIndicator(isSelected: Boolean) {
        binding.root.isActivated = isSelected
    }

    companion object {
        /** A unique ID for this [RecyclerView.ViewHolder] type. */
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_ALBUM_SONG

        /**
         * Create a new instance.
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            AlbumSongViewHolder(ItemAlbumSongBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Song>() {
                override fun areContentsTheSame(oldItem: Song, newItem: Song) =
                    oldItem.rawName == newItem.rawName && oldItem.durationMs == newItem.durationMs
            }
    }
}
