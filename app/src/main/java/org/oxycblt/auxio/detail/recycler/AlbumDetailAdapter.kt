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
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.coil.bindAlbumCover
import org.oxycblt.auxio.databinding.ItemAlbumSongBinding
import org.oxycblt.auxio.databinding.ItemDetailBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.ActionHeader
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Item
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.ActionHeaderViewHolder
import org.oxycblt.auxio.ui.BaseViewHolder
import org.oxycblt.auxio.ui.DiffCallback
import org.oxycblt.auxio.util.getPluralSafe
import org.oxycblt.auxio.util.inflater

/**
 * An adapter for displaying the details and [Song]s of an [Album]
 * @author OxygenCobalt
 */
class AlbumDetailAdapter(
    private val playbackModel: PlaybackViewModel,
    private val detailModel: DetailViewModel,
    private val doOnClick: (data: Song) -> Unit,
    private val doOnLongClick: (view: View, data: Song) -> Unit
) : ListAdapter<Item, RecyclerView.ViewHolder>(DiffCallback()) {
    private var currentSong: Song? = null
    private var currentHolder: Highlightable? = null

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Album -> IntegerTable.ITEM_TYPE_ALBUM_DETAIL
            is ActionHeader -> IntegerTable.ITEM_TYPE_ACTION_HEADER
            is Song -> IntegerTable.ITEM_TYPE_ALBUM_SONG
            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            IntegerTable.ITEM_TYPE_ALBUM_DETAIL ->
                AlbumDetailViewHolder(ItemDetailBinding.inflate(parent.context.inflater))
            IntegerTable.ITEM_TYPE_ACTION_HEADER -> ActionHeaderViewHolder.from(parent.context)
            IntegerTable.ITEM_TYPE_ALBUM_SONG ->
                AlbumSongViewHolder(ItemAlbumSongBinding.inflate(parent.context.inflater))
            else -> error("Invalid ViewHolder item type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        when (item) {
            is Album -> (holder as AlbumDetailViewHolder).bind(item)
            is Song -> (holder as AlbumSongViewHolder).bind(item)
            is ActionHeader -> (holder as ActionHeaderViewHolder).bind(item)
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

    inner class AlbumDetailViewHolder(private val binding: ItemDetailBinding) :
        BaseViewHolder<Album>(binding) {

        override fun onBind(data: Album) {
            binding.detailCover.apply {
                bindAlbumCover(data)
                contentDescription = context.getString(R.string.desc_album_cover, data.resolvedName)
            }

            binding.detailName.text = data.resolvedName

            binding.detailSubhead.apply {
                text = data.artist.resolvedName
                setOnClickListener { detailModel.navToItem(data.artist) }
            }

            binding.detailInfo.apply {
                text =
                    context.getString(
                        R.string.fmt_three,
                        data.year?.toString() ?: context.getString(R.string.def_date),
                        context.getPluralSafe(R.plurals.fmt_song_count, data.songs.size),
                        data.totalDuration)
            }

            binding.detailPlayButton.setOnClickListener { playbackModel.playAlbum(data, false) }

            binding.detailShuffleButton.setOnClickListener { playbackModel.playAlbum(data, true) }
        }
    }

    inner class AlbumSongViewHolder(
        private val binding: ItemAlbumSongBinding,
    ) : BaseViewHolder<Song>(binding, doOnClick, doOnLongClick), Highlightable {
        override fun onBind(data: Song) {
            binding.song = data
            binding.songName.requestLayout()

            // Hide the track number view if the track is zero, as generally a track number of
            // zero implies that the song does not have a track number.
            val usePlaceholder = data.track == null
            binding.songTrack.isInvisible = usePlaceholder
            binding.songTrackPlaceholder.isInvisible = !usePlaceholder
        }

        override fun setHighlighted(isHighlighted: Boolean) {
            binding.songName.isActivated = isHighlighted
            binding.songTrack.isActivated = isHighlighted
            binding.songTrackPlaceholder.isActivated = isHighlighted
        }
    }
}
