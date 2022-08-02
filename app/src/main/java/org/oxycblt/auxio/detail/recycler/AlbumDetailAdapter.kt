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
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemAlbumSongBinding
import org.oxycblt.auxio.databinding.ItemDetailBinding
import org.oxycblt.auxio.databinding.ItemDiscHeaderBinding
import org.oxycblt.auxio.detail.DiscHeader
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.recycler.BindingViewHolder
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.ui.recycler.MenuItemListener
import org.oxycblt.auxio.ui.recycler.SimpleItemCallback
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.formatDuration
import org.oxycblt.auxio.util.getPluralSafe
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.textSafe

/**
 * An adapter for displaying [Album] information and it's children.
 * @author OxygenCobalt
 */
class AlbumDetailAdapter(listener: Listener) :
    DetailAdapter<AlbumDetailAdapter.Listener>(listener, DIFFER) {
    private var currentSong: Song? = null

    override fun getCreatorFromItem(item: Item) =
        super.getCreatorFromItem(item)
            ?: when (item) {
                is Album -> AlbumDetailViewHolder.CREATOR
                is DiscHeader -> DiscHeaderViewHolder.CREATOR
                is Song -> AlbumSongViewHolder.CREATOR
                else -> null
            }

    override fun getCreatorFromViewType(viewType: Int) =
        super.getCreatorFromViewType(viewType)
            ?: when (viewType) {
                AlbumDetailViewHolder.CREATOR.viewType -> AlbumDetailViewHolder.CREATOR
                DiscHeaderViewHolder.CREATOR.viewType -> DiscHeaderViewHolder.CREATOR
                AlbumSongViewHolder.CREATOR.viewType -> AlbumSongViewHolder.CREATOR
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
                is Album -> (viewHolder as AlbumDetailViewHolder).bind(item, listener)
                is DiscHeader -> (viewHolder as DiscHeaderViewHolder).bind(item, Unit)
                is Song -> (viewHolder as AlbumSongViewHolder).bind(item, listener)
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
        private val DIFFER =
            object : SimpleItemCallback<Item>() {
                override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
                    return when {
                        oldItem is Album && newItem is Album ->
                            AlbumDetailViewHolder.DIFFER.areItemsTheSame(oldItem, newItem)
                        oldItem is DiscHeader && newItem is DiscHeader ->
                            DiscHeaderViewHolder.DIFFER.areItemsTheSame(oldItem, newItem)
                        oldItem is Song && newItem is Song ->
                            AlbumSongViewHolder.DIFFER.areItemsTheSame(oldItem, newItem)
                        else -> DetailAdapter.DIFFER.areItemsTheSame(oldItem, newItem)
                    }
                }
            }
    }

    interface Listener : DetailAdapter.Listener {
        fun onNavigateToArtist()
    }
}

private class AlbumDetailViewHolder private constructor(private val binding: ItemDetailBinding) :
    BindingViewHolder<Album, AlbumDetailAdapter.Listener>(binding.root) {

    override fun bind(item: Album, listener: AlbumDetailAdapter.Listener) {
        binding.detailCover.bind(item)
        binding.detailName.textSafe = item.resolveName(binding.context)

        binding.detailSubhead.apply {
            textSafe = item.artist.resolveName(context)
            setOnClickListener { listener.onNavigateToArtist() }
        }

        binding.detailInfo.apply {
            val date =
                item.date?.let { context.getString(R.string.fmt_number, it.year) }
                    ?: context.getString(R.string.def_date)

            val songCount = context.getPluralSafe(R.plurals.fmt_song_count, item.songs.size)

            val duration = item.durationSecs.formatDuration(true)

            text =
                if (item.releaseType != null) {
                    context.getString(
                        R.string.fmt_four,
                        context.getString(item.releaseType.stringRes),
                        date,
                        songCount,
                        duration)
                } else {
                    context.getString(R.string.fmt_three, date, songCount, duration)
                }
        }

        binding.detailPlayButton.setOnClickListener { listener.onPlayParent() }
        binding.detailShuffleButton.setOnClickListener { listener.onShuffleParent() }
    }

    companion object {
        val CREATOR =
            object : Creator<AlbumDetailViewHolder> {
                override val viewType: Int
                    get() = IntegerTable.ITEM_TYPE_ALBUM_DETAIL

                override fun create(context: Context) =
                    AlbumDetailViewHolder(ItemDetailBinding.inflate(context.inflater))
            }

        val DIFFER =
            object : SimpleItemCallback<Album>() {
                override fun areItemsTheSame(oldItem: Album, newItem: Album) =
                    oldItem.rawName == newItem.rawName &&
                        oldItem.artist.rawName == newItem.artist.rawName &&
                        oldItem.date == newItem.date &&
                        oldItem.songs.size == newItem.songs.size &&
                        oldItem.durationSecs == newItem.durationSecs &&
                        oldItem.releaseType == newItem.releaseType
            }
    }
}

class DiscHeaderViewHolder(private val binding: ItemDiscHeaderBinding) :
    BindingViewHolder<DiscHeader, Unit>(binding.root) {

    override fun bind(item: DiscHeader, listener: Unit) {
        binding.discNo.textSafe = binding.context.getString(R.string.fmt_disc_no, item.disc)
    }

    companion object {
        val CREATOR =
            object : Creator<DiscHeaderViewHolder> {
                override val viewType: Int
                    get() = IntegerTable.ITEM_TYPE_DISC_HEADER

                override fun create(context: Context) =
                    DiscHeaderViewHolder(ItemDiscHeaderBinding.inflate(context.inflater))
            }

        val DIFFER =
            object : SimpleItemCallback<DiscHeader>() {
                override fun areItemsTheSame(oldItem: DiscHeader, newItem: DiscHeader) =
                    oldItem.disc == newItem.disc
            }
    }
}

private class AlbumSongViewHolder private constructor(private val binding: ItemAlbumSongBinding) :
    BindingViewHolder<Song, MenuItemListener>(binding.root) {
    override fun bind(item: Song, listener: MenuItemListener) {
        // Hide the track number view if the song does not have a track.
        if (item.track != null) {
            binding.songTrack.apply {
                textSafe = context.getString(R.string.fmt_number, item.track)
                isInvisible = false
                contentDescription = context.getString(R.string.desc_track_number, item.track)
            }
        } else {
            binding.songTrack.apply {
                textSafe = ""
                isInvisible = true
                contentDescription = context.getString(R.string.def_track)
            }
        }

        binding.songName.textSafe = item.resolveName(binding.context)
        binding.songDuration.textSafe = item.durationSecs.formatDuration(false)

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
            object : Creator<AlbumSongViewHolder> {
                override val viewType: Int
                    get() = IntegerTable.ITEM_TYPE_ALBUM_SONG

                override fun create(context: Context) =
                    AlbumSongViewHolder(ItemAlbumSongBinding.inflate(context.inflater))
            }

        val DIFFER =
            object : SimpleItemCallback<Song>() {
                override fun areItemsTheSame(oldItem: Song, newItem: Song) =
                    oldItem.rawName == newItem.rawName && oldItem.durationMs == newItem.durationMs
            }
    }
}
