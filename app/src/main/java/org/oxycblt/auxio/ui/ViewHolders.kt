/*
 * Copyright (c) 2021 Auxio Project
 * SortHeaderViewHolder.kt is part of Auxio.
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

package org.oxycblt.auxio.ui

import android.content.Context
import android.view.View
import androidx.appcompat.widget.TooltipCompat
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemActionHeaderBinding
import org.oxycblt.auxio.databinding.ItemAlbumBinding
import org.oxycblt.auxio.databinding.ItemArtistBinding
import org.oxycblt.auxio.databinding.ItemGenreBinding
import org.oxycblt.auxio.databinding.ItemHeaderBinding
import org.oxycblt.auxio.databinding.ItemSongBinding
import org.oxycblt.auxio.music.ActionHeader
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.util.inflater

/**
 * A [RecyclerView.ViewHolder] that streamlines a lot of the common things across all viewholders.
 * @param T The datatype, inheriting [BaseModel] for this ViewHolder.
 * @param binding Basic [ViewDataBinding] required to set up click listeners & sizing.
 * @param doOnClick (Optional) Function that calls on a click.
 * @param doOnLongClick (Optional) Functions that calls on a long-click.
 * @author OxygenCobalt
 */
abstract class BaseViewHolder<T : BaseModel>(
    private val binding: ViewDataBinding,
    private val doOnClick: ((data: T) -> Unit)? = null,
    private val doOnLongClick: ((view: View, data: T) -> Unit)? = null
) : RecyclerView.ViewHolder(binding.root) {
    init {
        // Force the layout to *actually* be the screen width
        binding.root.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    /**
     * Bind the viewholder with whatever [BaseModel] instance that has been specified.
     * Will call [onBind] on the inheriting ViewHolder.
     * @param data Data that the viewholder should be bound with
     */
    fun bind(data: T) {
        doOnClick?.let { onClick ->
            binding.root.setOnClickListener {
                onClick(data)
            }
        }

        doOnLongClick?.let { onLongClick ->
            binding.root.setOnLongClickListener { view ->
                onLongClick(view, data)

                true
            }
        }

        onBind(data)

        binding.executePendingBindings()
    }

    /**
     * Function that performs binding operations unique to the inheriting viewholder.
     * Add any specialized code to an override of this instead of [BaseViewHolder] itself.
     */
    protected abstract fun onBind(data: T)
}

/**
 * The Shared ViewHolder for a [Song]. Instantiation should be done with [from].
 */
class SongViewHolder private constructor(
    private val binding: ItemSongBinding,
    doOnClick: (data: Song) -> Unit,
    doOnLongClick: (view: View, data: Song) -> Unit
) : BaseViewHolder<Song>(binding, doOnClick, doOnLongClick) {

    override fun onBind(data: Song) {
        binding.song = data

        binding.songName.requestLayout()
        binding.songInfo.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 0xA000

        /**
         * Create an instance of [SongViewHolder]
         */
        fun from(
            context: Context,
            doOnClick: (data: Song) -> Unit,
            doOnLongClick: (view: View, data: Song) -> Unit
        ): SongViewHolder {
            return SongViewHolder(
                ItemSongBinding.inflate(context.inflater),
                doOnClick, doOnLongClick
            )
        }
    }
}

/**
 * The Shared ViewHolder for a [Album]. Instantiation should be done with [from].
 */
class AlbumViewHolder private constructor(
    private val binding: ItemAlbumBinding,
    doOnClick: (data: Album) -> Unit,
    doOnLongClick: (view: View, data: Album) -> Unit
) : BaseViewHolder<Album>(binding, doOnClick, doOnLongClick) {

    override fun onBind(data: Album) {
        binding.album = data
        binding.albumName.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 0xA001

        /**
         * Create an instance of [AlbumViewHolder]
         */
        fun from(
            context: Context,
            doOnClick: (data: Album) -> Unit,
            doOnLongClick: (view: View, data: Album) -> Unit
        ): AlbumViewHolder {
            return AlbumViewHolder(
                ItemAlbumBinding.inflate(context.inflater),
                doOnClick, doOnLongClick
            )
        }
    }
}

/**
 * The Shared ViewHolder for a [Artist]. Instantiation should be done with [from].
 */
class ArtistViewHolder private constructor(
    private val binding: ItemArtistBinding,
    doOnClick: (Artist) -> Unit,
    doOnLongClick: (view: View, data: Artist) -> Unit
) : BaseViewHolder<Artist>(binding, doOnClick, doOnLongClick) {

    override fun onBind(data: Artist) {
        binding.artist = data
        binding.artistName.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 0xA002

        /**
         * Create an instance of [ArtistViewHolder]
         */
        fun from(
            context: Context,
            doOnClick: (Artist) -> Unit,
            doOnLongClick: (view: View, data: Artist) -> Unit
        ): ArtistViewHolder {
            return ArtistViewHolder(
                ItemArtistBinding.inflate(context.inflater),
                doOnClick, doOnLongClick
            )
        }
    }
}

/**
 * The Shared ViewHolder for a [Genre]. Instantiation should be done with [from].
 */
class GenreViewHolder private constructor(
    private val binding: ItemGenreBinding,
    doOnClick: (Genre) -> Unit,
    doOnLongClick: (view: View, data: Genre) -> Unit
) : BaseViewHolder<Genre>(binding, doOnClick, doOnLongClick) {

    override fun onBind(data: Genre) {
        binding.genre = data
        binding.genreName.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 0xA003

        /**
         * Create an instance of [GenreViewHolder]
         */
        fun from(
            context: Context,
            doOnClick: (Genre) -> Unit,
            doOnLongClick: (view: View, data: Genre) -> Unit
        ): GenreViewHolder {
            return GenreViewHolder(
                ItemGenreBinding.inflate(context.inflater),
                doOnClick, doOnLongClick
            )
        }
    }
}

/**
 * The Shared ViewHolder for a [Header]. Instantiation should be done with [from]
 */
class HeaderViewHolder private constructor(
    private val binding: ItemHeaderBinding
) : BaseViewHolder<Header>(binding) {

    override fun onBind(data: Header) {
        binding.header = data
    }

    companion object {
        const val ITEM_TYPE = 0xA004

        /**
         * Create an instance of [HeaderViewHolder]
         */
        fun from(context: Context): HeaderViewHolder {
            return HeaderViewHolder(
                ItemHeaderBinding.inflate(context.inflater)
            )
        }
    }
}

/**
 * The Shared ViewHolder for an [ActionHeader]. Instantiation should be done with [from]
 */
class ActionHeaderViewHolder private constructor(
    private val binding: ItemActionHeaderBinding
) : BaseViewHolder<ActionHeader>(binding) {

    override fun onBind(data: ActionHeader) {
        binding.header = data

        binding.executePendingBindings()

        binding.headerButton.apply {
            TooltipCompat.setTooltipText(this, contentDescription)

            setOnClickListener(data.onClick)
        }
    }

    companion object {
        const val ITEM_TYPE = 0xA005

        /**
         * Create an instance of [ActionHeaderViewHolder]
         */
        fun from(context: Context): ActionHeaderViewHolder {
            return ActionHeaderViewHolder(ItemActionHeaderBinding.inflate(context.inflater))
        }
    }
}
