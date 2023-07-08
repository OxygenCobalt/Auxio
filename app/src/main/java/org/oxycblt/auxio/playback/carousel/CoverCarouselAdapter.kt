/*
 * Copyright (c) 2023 Auxio Project
 * CoverCarouselAdapter.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.carousel

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemCoverBinding
import org.oxycblt.auxio.list.adapter.FlexibleListAdapter
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.inflater

class CoverCarouselAdapter :
    FlexibleListAdapter<Song, CoverViewHolder>(CoverViewHolder.DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoverViewHolder {
        return CoverViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CoverViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class CoverViewHolder private constructor(private val binding: ItemCoverBinding) :
    RecyclerView.ViewHolder(binding.root) {
    /**
     * Bind new data to this instance.
     *
     * @param item The new [Song] to bind.
     */
    fun bind(item: Song) {
        binding.playbackCover.bind(item)
    }

    companion object {
        /**
         * Create a new instance.
         *
         * @param parent The parent to inflate this instance from.
         * @return A new instance.
         */
        fun from(parent: ViewGroup) =
            CoverViewHolder(ItemCoverBinding.inflate(parent.context.inflater, parent, false))

        /** A comparator that can be used with DiffUtil. */
        val DIFF_CALLBACK =
            object : DiffUtil.ItemCallback<Song>() {
                override fun areItemsTheSame(oldItem: Song, newItem: Song) =
                    oldItem.uid == newItem.uid

                override fun areContentsTheSame(oldItem: Song, newItem: Song) =
                    oldItem.album.coverUri == newItem.album.coverUri
            }
    }
}
