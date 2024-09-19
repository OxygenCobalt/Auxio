/*
 * Copyright (c) 2021 Auxio Project
 * AlbumDetailListAdapter.kt is part of Auxio.
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

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemAlbumSongBinding
import org.oxycblt.auxio.databinding.ItemDiscHeaderBinding
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.SelectableListListener
import org.oxycblt.auxio.list.adapter.SelectionIndicatorAdapter
import org.oxycblt.auxio.list.adapter.SimpleDiffCallback
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.info.Disc
import org.oxycblt.auxio.music.info.resolveNumber
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.inflater

/**
 * An [DetailListAdapter] implementing the header and sub-items for the [Album] detail view.
 *
 * @param listener A [DetailListAdapter.Listener] to bind interactions to.
 * @author Alexander Capehart (OxygenCobalt)
 */
class AlbumDetailListAdapter(private val listener: Listener<Song>) :
    DetailListAdapter(listener, DIFF_CALLBACK) {
    override fun getItemViewType(position: Int) =
        when (getItem(position)) {
            // Support sub-headers for each disc, and special album songs.
            is DiscHeader -> DiscHeaderViewHolder.VIEW_TYPE
            is Song -> AlbumSongViewHolder.VIEW_TYPE
            else -> super.getItemViewType(position)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            DiscHeaderViewHolder.VIEW_TYPE -> DiscHeaderViewHolder.from(parent)
            AlbumSongViewHolder.VIEW_TYPE -> AlbumSongViewHolder.from(parent)
            else -> super.onCreateViewHolder(parent, viewType)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (val item = getItem(position)) {
            is DiscHeader -> (holder as DiscHeaderViewHolder).bind(item)
            is Song -> (holder as AlbumSongViewHolder).bind(item, listener)
        }
    }

    private companion object {
        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Item>() {
                override fun areContentsTheSame(oldItem: Item, newItem: Item) =
                    when {
                        oldItem is Disc && newItem is Disc ->
                            DiscHeaderViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                        oldItem is Song && newItem is Song ->
                            AlbumSongViewHolder.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)

                        // Fall back to DetailAdapter's differ to handle other headers.
                        else -> DetailListAdapter.DIFF_CALLBACK.areContentsTheSame(oldItem, newItem)
                    }
            }
    }
}

/**
 * A wrapper around [Disc] signifying that a header should be shown for a disc group.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
data class DiscHeader(val inner: Disc?) : Item

/**
 * A [RecyclerView.ViewHolder] that displays a [DiscHeader] to delimit different disc groups. Use
 * [from] to create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
private class DiscHeaderViewHolder(private val binding: ItemDiscHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param discHeader The new [DiscHeader] to bind.
     */
    fun bind(discHeader: DiscHeader) {
        val disc = discHeader.inner
        binding.discNumber.text = disc.resolveNumber(binding.context)
        binding.discName.apply {
            text = disc?.name
            isGone = disc?.name == null
        }
    }

    companion object {
        /** A unique ID for this [RecyclerView.ViewHolder] type. */
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_DISC_HEADER

        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            DiscHeaderViewHolder(ItemDiscHeaderBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Disc>() {
                override fun areContentsTheSame(oldItem: Disc, newItem: Disc) =
                    oldItem.number == newItem.number && oldItem.name == newItem.name
            }
    }
}

/**
 * A [RecyclerView.ViewHolder] that displays a [Song] in the context of an [Album]. Use [from] to
 * create an instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
private class AlbumSongViewHolder private constructor(private val binding: ItemAlbumSongBinding) :
    SelectionIndicatorAdapter.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param song The new [Song] to bind.
     * @param listener A [SelectableListListener] to bind interactions to.
     */
    fun bind(song: Song, listener: SelectableListListener<Song>) {
        listener.bind(song, this, menuButton = binding.songMenu)

        val track = song.track
        if (track != null) {
            binding.songTrackCover.contentDescription =
                binding.context.getString(R.string.desc_track_number, track)
            binding.songTrackText.apply {
                isVisible = true
                text = context.getString(R.string.fmt_number, song.track)
            }
            binding.songTrackPlaceholder.isInvisible = true
        } else {
            binding.songTrackCover.contentDescription =
                binding.context.getString(R.string.def_track)
            binding.songTrackText.apply {
                isInvisible = true
                text = null
            }
            binding.songTrackPlaceholder.isVisible = true
        }

        binding.songName.text = song.name.resolve(binding.context)
        // Use duration instead of album or artist for each song to be more contextually relevant.
        binding.songDuration.text = song.durationMs.formatDurationMs(false)
    }

    override fun updatePlayingIndicator(isActive: Boolean, isPlaying: Boolean) {
        binding.root.isSelected = isActive
        binding.songTrackCover.setPlaying(isPlaying)
    }

    override fun updateSelectionIndicator(isSelected: Boolean) {
        binding.root.isActivated = isSelected
    }

    companion object {
        /** A unique ID for this [RecyclerView.ViewHolder] type. */
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_ALBUM_SONG

        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: View) =
            AlbumSongViewHolder(ItemAlbumSongBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : SimpleDiffCallback<Song>() {
                override fun areContentsTheSame(oldItem: Song, newItem: Song) =
                    oldItem.name == newItem.name && oldItem.durationMs == newItem.durationMs
            }
    }
}
