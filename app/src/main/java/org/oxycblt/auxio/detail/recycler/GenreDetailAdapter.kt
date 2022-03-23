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
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.coil.bindAlbumCover
import org.oxycblt.auxio.coil.bindGenreImage
import org.oxycblt.auxio.databinding.ItemDetailBinding
import org.oxycblt.auxio.databinding.ItemSongBinding
import org.oxycblt.auxio.music.ActionHeader
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Item
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.ActionHeaderViewHolder
import org.oxycblt.auxio.ui.BaseViewHolder
import org.oxycblt.auxio.ui.DiffCallback
import org.oxycblt.auxio.util.context
import org.oxycblt.auxio.util.getPluralSafe
import org.oxycblt.auxio.util.inflater
import org.oxycblt.auxio.util.textSafe

/**
 * An adapter for displaying the [Song]s of a genre.
 * @author OxygenCobalt
 */
class GenreDetailAdapter(
    private val playbackModel: PlaybackViewModel,
    private val doOnClick: (data: Song) -> Unit,
    private val doOnLongClick: (view: View, data: Song) -> Unit
) : ListAdapter<Item, RecyclerView.ViewHolder>(DiffCallback()) {
    private var currentSong: Song? = null
    private var currentHolder: Highlightable? = null

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Genre -> IntegerTable.ITEM_TYPE_GENRE_DETAIL
            is ActionHeader -> IntegerTable.ITEM_TYPE_ACTION_HEADER
            is Song -> IntegerTable.ITEM_TYPE_GENRE_SONG
            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            IntegerTable.ITEM_TYPE_GENRE_DETAIL ->
                GenreDetailViewHolder(ItemDetailBinding.inflate(parent.context.inflater))
            IntegerTable.ITEM_TYPE_ACTION_HEADER -> ActionHeaderViewHolder.from(parent.context)
            IntegerTable.ITEM_TYPE_GENRE_SONG ->
                GenreSongViewHolder(ItemSongBinding.inflate(parent.context.inflater))
            else -> error("Bad ViewHolder item type $viewType")
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
        if (song == currentSong) return // Already highlighting this ViewHolder

        // Clear the current ViewHolder since it's invalid
        currentHolder?.setHighlighted(false)
        currentHolder = null
        currentSong = song

        if (song != null) {
            // Use existing data instead of having to re-sort it.
            val pos = currentList.indexOfFirst { item -> item.id == song.id && item is Song }

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

    inner class GenreDetailViewHolder(private val binding: ItemDetailBinding) :
        BaseViewHolder<Genre>(binding) {
        override fun onBind(data: Genre) {
            val context = binding.root.context

            binding.detailCover.apply {
                bindGenreImage(data)
                contentDescription = context.getString(R.string.desc_genre_image, data.resolvedName)
            }

            binding.detailName.textSafe = data.resolvedName
            binding.detailSubhead.textSafe =
                binding.context.getPluralSafe(R.plurals.fmt_song_count, data.songs.size)
            binding.detailInfo.textSafe = data.totalDuration

            binding.detailPlayButton.setOnClickListener { playbackModel.playGenre(data, false) }

            binding.detailShuffleButton.setOnClickListener { playbackModel.playGenre(data, true) }
        }
    }

    /** The Shared ViewHolder for a [Song]. Instantiation should be done with [from]. */
    inner class GenreSongViewHolder
    constructor(
        private val binding: ItemSongBinding,
    ) : BaseViewHolder<Song>(binding, doOnClick, doOnLongClick), Highlightable {

        override fun onBind(data: Song) {
            binding.songAlbumCover.bindAlbumCover(data)
            binding.songName.textSafe = data.resolvedName
            binding.songInfo.textSafe = data.resolvedArtistName
        }

        override fun setHighlighted(isHighlighted: Boolean) {
            binding.songName.isActivated = isHighlighted
        }
    }
}
