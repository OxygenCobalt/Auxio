/*
 * Copyright (c) 2021 Auxio Project
 * SongListFragment.kt is part of Auxio.
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

package org.oxycblt.auxio.home.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.zhanghai.android.fastscroll.PopupTextProvider
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemPlayShuffleBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.SongViewHolder
import org.oxycblt.auxio.ui.SortMode
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.ui.sliceArticle
import org.oxycblt.auxio.util.applySpans
import org.oxycblt.auxio.util.inflater

class SongListFragment : HomeListFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = SongsAdapter(
            doOnClick = { song ->
                playbackModel.playSong(song)
            },
            ::newMenu
        )

        setupRecycler(R.id.home_song_list, adapter, homeModel.songs)
        binding.homeRecycler.applySpans { it == 0 }

        return binding.root
    }

    override val popupProvider: PopupTextProvider
        get() = PopupTextProvider { idx ->
            if (idx == 0) {
                return@PopupTextProvider ""
            }

            val song = homeModel.songs.value!![idx]

            when (homeModel.getSortForDisplay(DisplayMode.SHOW_SONGS)) {
                SortMode.ASCENDING, SortMode.DESCENDING -> song.name.sliceArticle()
                    .first().uppercase()

                SortMode.ARTIST -> song.album.artist.name.sliceArticle()
                    .first().uppercase()

                SortMode.ALBUM -> song.album.name.sliceArticle()
                    .first().uppercase()

                SortMode.YEAR -> song.album.year.toString()
            }
        }

    inner class SongsAdapter(
        private val doOnClick: (data: Song) -> Unit,
        private val doOnLongClick: (view: View, data: Song) -> Unit,
    ) : HomeAdapter<Song, RecyclerView.ViewHolder>() {
        override fun getItemCount(): Int {
            return if (data.isNotEmpty()) {
                data.size + 1 // Make space for the play/shuffle header
            } else {
                data.size
            }
        }

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
                holder.bind(data[position - 1])
            }
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
