/*
 * Copyright (c) 2021 Auxio Project
 * ArtistDetailAdapter.kt is part of Auxio.
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
import org.oxycblt.auxio.coil.bindArtistImage
import org.oxycblt.auxio.databinding.ItemArtistAlbumBinding
import org.oxycblt.auxio.databinding.ItemArtistSongBinding
import org.oxycblt.auxio.databinding.ItemDetailBinding
import org.oxycblt.auxio.music.ActionHeader
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.ActionHeaderViewHolder
import org.oxycblt.auxio.ui.BaseViewHolder
import org.oxycblt.auxio.ui.DiffCallback
import org.oxycblt.auxio.ui.HeaderViewHolder
import org.oxycblt.auxio.util.getPlural
import org.oxycblt.auxio.util.inflater

/**
 * An adapter for displaying the [Album]s and [Song]s of an artist.
 * @author OxygenCobalt
 */
class ArtistDetailAdapter(
    private val playbackModel: PlaybackViewModel,
    private val doOnClick: (data: Album) -> Unit,
    private val doOnSongClick: (data: Song) -> Unit,
    private val doOnLongClick: (view: View, data: BaseModel) -> Unit,
) : ListAdapter<BaseModel, RecyclerView.ViewHolder>(DiffCallback()) {
    private var currentAlbum: Album? = null
    private var currentAlbumHolder: Highlightable? = null

    private var currentSong: Song? = null
    private var currentSongHolder: Highlightable? = null

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Artist -> ARTIST_DETAIL_ITEM_TYPE
            is Album -> ARTIST_ALBUM_ITEM_TYPE
            is Song -> ARTIST_SONG_ITEM_TYPE
            is Header -> HeaderViewHolder.ITEM_TYPE
            is ActionHeader -> ActionHeaderViewHolder.ITEM_TYPE

            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ARTIST_DETAIL_ITEM_TYPE -> ArtistDetailViewHolder(
                ItemDetailBinding.inflate(parent.context.inflater)
            )

            ARTIST_ALBUM_ITEM_TYPE -> ArtistAlbumViewHolder(
                ItemArtistAlbumBinding.inflate(parent.context.inflater)
            )

            ARTIST_SONG_ITEM_TYPE -> ArtistSongViewHolder(
                ItemArtistSongBinding.inflate(parent.context.inflater)
            )

            HeaderViewHolder.ITEM_TYPE -> HeaderViewHolder.from(parent.context)

            ActionHeaderViewHolder.ITEM_TYPE -> ActionHeaderViewHolder.from(parent.context)

            else -> error("Invalid ViewHolder item type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)

        when (item) {
            is Artist -> (holder as ArtistDetailViewHolder).bind(item)
            is Album -> (holder as ArtistAlbumViewHolder).bind(item)
            is Song -> (holder as ArtistSongViewHolder).bind(item)
            is Header -> (holder as HeaderViewHolder).bind(item)
            is ActionHeader -> (holder as ActionHeaderViewHolder).bind(item)
            else -> {
            }
        }

        if (holder is Highlightable) {
            // If the item corresponds to a currently playing song/album then highlight it
            if (item.id == currentAlbum?.id && item is Album) {
                currentAlbumHolder?.setHighlighted(false)
                currentAlbumHolder = holder
                holder.setHighlighted(true)
            } else if (item.id == currentSong?.id && item is Song) {
                currentSongHolder?.setHighlighted(false)
                currentSongHolder = holder
                holder.setHighlighted(true)
            } else {
                holder.setHighlighted(false)
            }
        }
    }

    /**
     * Update the current [album] that this adapter should highlight
     * @param recycler The recyclerview the highlighting should act on.
     */
    fun highlightAlbum(album: Album?, recycler: RecyclerView) {
        if (album == currentAlbum) return // Already highlighting this viewholder

        // Album is no longer valid, clear out this ViewHolder.
        currentAlbumHolder?.setHighlighted(false)
        currentAlbumHolder = null
        currentAlbum = album

        if (album != null) {
            // Use existing data instead of having to re-sort it.
            val pos = currentList.indexOfFirst { item ->
                item.id == album.id && item is Album
            }

            // Check if the ViewHolder if this album is visible, and highlight it if so.
            recycler.layoutManager?.findViewByPosition(pos)?.let { child ->
                recycler.getChildViewHolder(child)?.let {
                    currentAlbumHolder = it as Highlightable

                    currentAlbumHolder?.setHighlighted(true)
                }
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
        currentSongHolder?.setHighlighted(false)
        currentSongHolder = null
        currentSong = song

        if (song != null) {
            // Use existing data instead of having to re-sort it.
            // We also have to account for the album count when searching for the viewholder
            val pos = currentList.indexOfFirst { item ->
                item.id == song.id && item is Song
            }

            // Check if the ViewHolder for this song is visible, if it is then highlight it.
            // If the ViewHolder is not visible, then the adapter should take care of it if
            // it does become visible.
            recycler.layoutManager?.findViewByPosition(pos)?.let { child ->
                recycler.getChildViewHolder(child)?.let {
                    currentSongHolder = it as Highlightable

                    currentSongHolder?.setHighlighted(true)
                }
            }
        }
    }

    inner class ArtistDetailViewHolder(
        private val binding: ItemDetailBinding
    ) : BaseViewHolder<Artist>(binding) {

        override fun onBind(data: Artist) {
            val context = binding.root.context

            binding.detailCover.apply {
                bindArtistImage(data)
                contentDescription = context.getString(
                    R.string.desc_artist_image,
                    data.resolvedName
                )
            }

            binding.detailName.text = data.resolvedName

            binding.detailSubhead.text = data.genre?.resolvedName
                ?: context.getString(R.string.def_genre)

            binding.detailInfo.text = context.getString(
                R.string.fmt_counts,
                context.getPlural(R.plurals.fmt_album_count, data.albums.size),
                context.getPlural(R.plurals.fmt_song_count, data.songs.size)
            )

            binding.detailPlayButton.setOnClickListener {
                playbackModel.playArtist(data, false)
            }

            binding.detailShuffleButton.setOnClickListener {
                playbackModel.playArtist(data, true)
            }
        }
    }

    inner class ArtistAlbumViewHolder(
        private val binding: ItemArtistAlbumBinding,
    ) : BaseViewHolder<Album>(binding, doOnClick, doOnLongClick), Highlightable {
        override fun onBind(data: Album) {
            binding.album = data
            binding.albumName.requestLayout()
        }

        override fun setHighlighted(isHighlighted: Boolean) {
            binding.albumName.isActivated = isHighlighted
        }
    }

    inner class ArtistSongViewHolder(
        private val binding: ItemArtistSongBinding,
    ) : BaseViewHolder<Song>(binding, doOnSongClick, doOnLongClick), Highlightable {
        override fun onBind(data: Song) {
            binding.song = data
            binding.songName.requestLayout()
        }

        override fun setHighlighted(isHighlighted: Boolean) {
            binding.songName.isActivated = isHighlighted
        }
    }

    companion object {
        const val ARTIST_DETAIL_ITEM_TYPE = 0xA008
        const val ARTIST_ALBUM_ITEM_TYPE = 0xA009
        const val ARTIST_SONG_ITEM_TYPE = 0xA00A
    }
}
