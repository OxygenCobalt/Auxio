/*
 * Copyright (c) 2021 Auxio Project
 * GenreDetailAdapter.kt is part of Auxio.
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
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.coil.bindGenreImage
import org.oxycblt.auxio.databinding.ItemDetailBinding
import org.oxycblt.auxio.databinding.ItemGenreSongBinding
import org.oxycblt.auxio.music.ActionHeader
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.ActionHeaderViewHolder
import org.oxycblt.auxio.ui.BaseViewHolder
import org.oxycblt.auxio.ui.DiffCallback
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.inflater

/**
 * An adapter for displaying the [Song]s of a genre.
 * @author OxygenCobalt
 */
class GenreDetailAdapter(
    private val playbackModel: PlaybackViewModel,
    private val doOnClick: (data: Song) -> Unit,
    private val doOnLongClick: (view: View, data: Song) -> Unit
) : ListAdapter<BaseModel, RecyclerView.ViewHolder>(DiffCallback()) {
    private var currentSong: Song? = null
    private var currentHolder: Highlightable? = null

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Genre -> GENRE_DETAIL_ITEM_TYPE
            is ActionHeader -> ActionHeaderViewHolder.ITEM_TYPE
            is Song -> GENRE_SONG_ITEM_TYPE

            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            GENRE_DETAIL_ITEM_TYPE -> GenreDetailViewHolder(
                ItemDetailBinding.inflate(parent.context.inflater)
            )

            GENRE_SONG_ITEM_TYPE -> GenreSongViewHolder(
                ItemGenreSongBinding.inflate(parent.context.inflater),
            )

            ActionHeaderViewHolder.ITEM_TYPE -> ActionHeaderViewHolder.from(parent.context)

            else -> error("Bad viewholder item type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        when (item) {
            is Genre -> (holder as GenreDetailViewHolder).bind(item)
            is ActionHeader -> (holder as ActionHeaderViewHolder).bind(item)
            is Song -> (holder as GenreSongViewHolder).bind(item)
            else -> {}
        }

        if (holder is Highlightable) {
            if (item.id == currentSong?.id) {
                // Reset the last ViewHolder before assigning the new, correct one to be highlighted
                currentHolder?.setHighlighted(false)
                currentHolder = holder
                holder.setHighlighted(true)
            } else {
                holder.setHighlighted(false)
            }
        }
    }

    /**
     * Update the [song] that this adapter should highlight
     * @param recycler The recyclerview the highlighting should act on.
     */
    fun highlightSong(song: Song?, recycler: RecyclerView) {
        if (song == currentSong) return // Already highlighting this viewholder

        // Clear the current viewholder since it's invalid
        currentHolder?.setHighlighted(false)
        currentHolder = null
        currentSong = song

        if (song != null) {
            // Use existing data instead of having to re-sort it.
            val pos = currentList.indexOfFirst { item ->
                item.id == song.id && item is Song
            }

            // Check if the ViewHolder for this song is visible, if it is then highlight it.
            // If the ViewHolder is not visible, then the adapter should take care of it if
            // it does become visible.
            recycler.layoutManager?.findViewByPosition(pos)?.let { child ->
                recycler.getChildViewHolder(child)?.let {
                    currentHolder = it as Highlightable

                    currentHolder?.setHighlighted(true)
                }
            }
        }
    }

    inner class GenreDetailViewHolder(
        private val binding: ItemDetailBinding
    ) : BaseViewHolder<Genre>(binding) {
        override fun onBind(data: Genre) {
            val context = binding.root.context

            binding.detailCover.apply {
                bindGenreImage(data)
                contentDescription = context.getString(
                    R.string.desc_genre_image,
                    data.resolvedName
                )
            }

            binding.detailName.text = data.resolvedName

            binding.detailSubhead.apply {
                text = context.getPlural(R.plurals.fmt_song_count, data.songs.size)
            }

            binding.detailInfo.text = data.totalDuration

            binding.detailPlayButton.setOnClickListener {
                playbackModel.playGenre(data, false)
            }

            binding.detailShuffleButton.setOnClickListener {
                playbackModel.playGenre(data, true)
            }
        }
    }

    inner class GenreSongViewHolder(
        private val binding: ItemGenreSongBinding,
    ) : BaseViewHolder<Song>(binding, doOnClick, doOnLongClick), Highlightable {
        override fun onBind(data: Song) {
            binding.song = data

            binding.songName.requestLayout()
            binding.songInfo.requestLayout()
        }

        override fun setHighlighted(isHighlighted: Boolean) {
            binding.songName.isActivated = isHighlighted
        }
    }

    companion object {
        const val GENRE_DETAIL_ITEM_TYPE = 0xA00B
        const val GENRE_SONG_ITEM_TYPE = 0xA00C
    }
}
