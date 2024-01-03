/*
 * Copyright (c) 2023 Auxio Project
 * PlaybackPagerAdapter.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.ui

import android.view.ViewGroup
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.jvm.internal.Intrinsics
import org.oxycblt.auxio.databinding.ItemPlaybackSongBinding
import org.oxycblt.auxio.list.adapter.FlexibleListAdapter
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.util.inflater

/** @author Koitharu, Alexander Capehart (OxygenCobalt) */
class PlaybackPagerAdapter(private val listener: Listener) :
    FlexibleListAdapter<Song, CoverViewHolder>(CoverViewHolder.DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoverViewHolder {
        return CoverViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CoverViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

    override fun onViewRecycled(holder: CoverViewHolder) {
        holder.recycle()
        super.onViewRecycled(holder)
    }

    interface Listener {
        fun navigateToCurrentArtist()

        fun navigateToCurrentAlbum()

        fun navigateToCurrentSong()

        fun navigateToMenu()
    }
}

class CoverViewHolder private constructor(private val binding: ItemPlaybackSongBinding) :
    RecyclerView.ViewHolder(binding.root), DefaultLifecycleObserver {
    init {
        binding.root.layoutParams =
            RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT)
    }

    /**
     * Bind new data to this instance.
     *
     * @param item The new [Song] to bind.
     */
    fun bind(item: Song, listener: PlaybackPagerAdapter.Listener) {
        val context = binding.root.context
        binding.playbackCover.bind(item)
        // binding.playbackCover.bind(item)
        binding.playbackSong.apply { text = item.name.resolve(context) }
        binding.playbackArtist.apply {
            text = item.artists.resolveNames(context)
            setOnClickListener { listener.navigateToCurrentArtist() }
        }
        binding.playbackAlbum.apply {
            text = item.album.name.resolve(context)
            setOnClickListener { listener.navigateToCurrentAlbum() }
        }
        setSelected(true)
    }

    fun recycle() {
        // Marquee elements leak if they are not disabled when the views are destroyed.
        // TODO: Move to TextView impl to avoid having to deal with lifecycle here
        setSelected(false)
    }

    private fun setSelected(value: Boolean) {
        binding.playbackSong.isSelected = value
        binding.playbackArtist.isSelected = value
        binding.playbackAlbum.isSelected = value
    }

    companion object {
        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: ViewGroup) =
            CoverViewHolder(ItemPlaybackSongBinding.inflate(parent.context.inflater))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Song>() {
                override fun areItemsTheSame(oldItem: Song, newItem: Song) =
                    oldItem.uid == newItem.uid

                override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
                    return Intrinsics.areEqual(oldItem, newItem)
                }
            }
    }
}
