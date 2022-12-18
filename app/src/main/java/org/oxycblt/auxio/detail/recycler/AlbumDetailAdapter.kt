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
import org.oxycblt.auxio.list.recycler.PlayingIndicatorAdapter
import org.oxycblt.auxio.list.recycler.SimpleItemCallback
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.inflater

/**
 * An adapter for displaying [Album] information and it's children.
 * @author OxygenCobalt
 */
class AlbumDetailAdapter(private val callback: AlbumDetailAdapter.Callback) :
    DetailAdapter(callback, DIFFER) {

    override fun getItemViewType(position: Int) =
        when (differ.currentList[position]) {
            is Album -> AlbumDetailViewHolder.VIEW_TYPE
            is DiscHeader -> DiscHeaderViewHolder.VIEW_TYPE
            is Song -> AlbumSongViewHolder.VIEW_TYPE
            else -> super.getItemViewType(position)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            AlbumDetailViewHolder.VIEW_TYPE -> AlbumDetailViewHolder.new(parent)
            DiscHeaderViewHolder.VIEW_TYPE -> DiscHeaderViewHolder.new(parent)
            AlbumSongViewHolder.VIEW_TYPE -> AlbumSongViewHolder.new(parent)
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
                is Album -> (holder as AlbumDetailViewHolder).bind(item, callback)
                is DiscHeader -> (holder as DiscHeaderViewHolder).bind(item)
                is Song -> (holder as AlbumSongViewHolder).bind(item, callback)
            }
        }
    }

    override fun isItemFullWidth(position: Int): Boolean {
        val item = differ.currentList[position]
        return super.isItemFullWidth(position) || item is Album || item is DiscHeader
    }

    companion object {
        private val DIFFER =
            object : SimpleItemCallback<Item>() {
                override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
                    return when {
                        oldItem is Album && newItem is Album ->
                            AlbumDetailViewHolder.DIFFER.areContentsTheSame(oldItem, newItem)
                        oldItem is DiscHeader && newItem is DiscHeader ->
                            DiscHeaderViewHolder.DIFFER.areContentsTheSame(oldItem, newItem)
                        oldItem is Song && newItem is Song ->
                            AlbumSongViewHolder.DIFFER.areContentsTheSame(oldItem, newItem)
                        else -> DetailAdapter.DIFFER.areContentsTheSame(oldItem, newItem)
                    }
                }
            }
    }

    class Callback(
        onClick: (Item) -> Unit,
        onOpenItemMenu: (Item, View) -> Unit,
        onSelect: (Item) -> Unit,
        onPlay: () -> Unit,
        onShuffle: () -> Unit,
        onOpenSortMenu: (View) -> Unit,
        val onNavigateToArtist: () -> Unit
    ) :
        DetailAdapter.Callback(
            onClick, onOpenItemMenu, onSelect, onPlay, onShuffle, onOpenSortMenu)
}

private class AlbumDetailViewHolder private constructor(private val binding: ItemDetailBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Album, callback: AlbumDetailAdapter.Callback) {
        binding.detailCover.bind(item)
        binding.detailType.text = binding.context.getString(item.releaseType.stringRes)

        binding.detailName.text = item.resolveName(binding.context)

        binding.detailSubhead.apply {
            text = item.resolveArtistContents(context)
            setOnClickListener { callback.onNavigateToArtist() }
        }

        binding.detailInfo.apply {
            val date = item.date?.resolveDate(context) ?: context.getString(R.string.def_date)

            val songCount = context.getPlural(R.plurals.fmt_song_count, item.songs.size)

            val duration = item.durationMs.formatDurationMs(true)

            text = context.getString(R.string.fmt_three, date, songCount, duration)
        }

        binding.detailPlayButton.setOnClickListener { callback.onPlay() }
        binding.detailShuffleButton.setOnClickListener { callback.onShuffle() }
    }

    companion object {
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_ALBUM_DETAIL

        fun new(parent: View) =
            AlbumDetailViewHolder(ItemDetailBinding.inflate(parent.context.inflater))

        val DIFFER =
            object : SimpleItemCallback<Album>() {
                override fun areContentsTheSame(oldItem: Album, newItem: Album) =
                    oldItem.rawName == newItem.rawName &&
                        oldItem.areArtistContentsTheSame(newItem) &&
                        oldItem.date == newItem.date &&
                        oldItem.songs.size == newItem.songs.size &&
                        oldItem.durationMs == newItem.durationMs &&
                        oldItem.releaseType == newItem.releaseType
            }
    }
}

class DiscHeaderViewHolder(private val binding: ItemDiscHeaderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DiscHeader) {
        binding.discNo.text = binding.context.getString(R.string.fmt_disc_no, item.disc)
    }

    companion object {
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_DISC_HEADER

        fun new(parent: View) =
            DiscHeaderViewHolder(ItemDiscHeaderBinding.inflate(parent.context.inflater))

        val DIFFER =
            object : SimpleItemCallback<DiscHeader>() {
                override fun areContentsTheSame(oldItem: DiscHeader, newItem: DiscHeader) =
                    oldItem.disc == newItem.disc
            }
    }
}

private class AlbumSongViewHolder private constructor(private val binding: ItemAlbumSongBinding) :
    PlayingIndicatorAdapter.ViewHolder(binding.root) {
    fun bind(item: Song, callback: AlbumDetailAdapter.Callback) {
        // Hide the track number view if the song does not have a track.
        if (item.track != null) {
            binding.songTrack.apply {
                text = context.getString(R.string.fmt_number, item.track)
                isInvisible = false
                contentDescription = context.getString(R.string.desc_track_number, item.track)
            }
        } else {
            binding.songTrack.apply {
                text = ""
                isInvisible = true
                contentDescription = context.getString(R.string.def_track)
            }
        }

        binding.songName.text = item.resolveName(binding.context)
        binding.songDuration.text = item.durationMs.formatDurationMs(false)

        binding.songMenu.setOnClickListener { callback.onOpenMenu(item, it) }
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
        binding.songTrackBg.isPlaying = isPlaying
    }

    companion object {
        const val VIEW_TYPE = IntegerTable.VIEW_TYPE_ALBUM_SONG

        fun new(parent: View) =
            AlbumSongViewHolder(ItemAlbumSongBinding.inflate(parent.context.inflater))

        val DIFFER =
            object : SimpleItemCallback<Song>() {
                override fun areContentsTheSame(oldItem: Song, newItem: Song) =
                    oldItem.rawName == newItem.rawName && oldItem.durationMs == newItem.durationMs
            }
    }
}
