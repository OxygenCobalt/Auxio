/*
 * Copyright (c) 2021 Auxio Project
 * HomeAdapter.kt is part of Auxio.
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

package org.oxycblt.auxio.home.recycler

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemPlayShuffleBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.SongViewHolder
import org.oxycblt.auxio.util.inflater

/**
 * An adapter for displaying a song list with a special play/shuffle header.
 * Note that the data for the play/pause icon does not need to be included with the data
 * you are submitting. It is automatically handled by the adapter.
 * TODO: Maybe extend play/shuffle to all items?
 */
class SongsAdapter(
    private val doOnClick: (data: Song) -> Unit,
    private val doOnLongClick: (view: View, data: Song) -> Unit,
    private val playbackModel: PlaybackViewModel
) : HomeAdapter<Song>() {
    override fun getItemCount(): Int = if
    (data.isEmpty()) 0 // Account for the play/shuffle viewholder
    else
        data.size + 1

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            PLAY_ITEM_TYPE
        } else {
            SongViewHolder.ITEM_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            PLAY_ITEM_TYPE -> PlayViewHolder(
                ItemPlayShuffleBinding.inflate(parent.context.inflater)
            )

            SongViewHolder.ITEM_TYPE -> SongViewHolder.from(
                parent.context, doOnClick, doOnLongClick
            )

            else -> error("Invalid viewholder item type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SongViewHolder) {
            holder.bind(data[position - 1] as Song)
        }
    }

    private inner class PlayViewHolder(
        binding: ItemPlayShuffleBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            // Force the layout to *actually* be the screen width.
            // We can't inherit BaseViewHolder here since this ViewHolder isn't really connected
            // to an item.
            binding.root.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
            )

            binding.playButton.setOnClickListener {
                playbackModel.playAll()
            }

            binding.shuffleButton.setOnClickListener {
                playbackModel.shuffleAll()
            }
        }
    }

    companion object {
        const val PLAY_ITEM_TYPE = 0xA00E
    }
}
