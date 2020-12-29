package org.oxycblt.auxio.recycler.viewholders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import org.oxycblt.auxio.databinding.ItemAlbumBinding
import org.oxycblt.auxio.databinding.ItemArtistBinding
import org.oxycblt.auxio.databinding.ItemGenreBinding
import org.oxycblt.auxio.databinding.ItemHeaderBinding
import org.oxycblt.auxio.databinding.ItemSongBinding
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.viewholders.AlbumViewHolder.Companion.from
import org.oxycblt.auxio.recycler.viewholders.ArtistViewHolder.Companion.from
import org.oxycblt.auxio.recycler.viewholders.GenreViewHolder.Companion.from
import org.oxycblt.auxio.recycler.viewholders.HeaderViewHolder.Companion.from
import org.oxycblt.auxio.recycler.viewholders.SongViewHolder.Companion.from

/*
 * A table of all ViewHolder codes. Please add to these so that all viewholder codes are unique.
 * SongViewHolder -> 0xA000
 * AlbumViewHolder -> 0xA001
 * ArtistViewHolder -> 0xA002
 * GenreViewHolder -> 0xA003
 * HeaderViewHolder -> 0xA004
 * QueueSongViewHolder -> 0xA005
 * UserQueueHeaderViewHolder -> 0xA006
 * AlbumHeaderViewHolder -> 0xA007
 * AlbumSongViewHolder -> 0xA008
 * ArtistHeaderViewHolder -> 0xA009
 * ArtistAlbumViewHolder -> 0xA00A
 * GenreHeaderViewHolder -> 0xA00B
 * GenreSongViewHolder -> 0xA00C
 */

/**
 * The Shared ViewHolder for a [Song]. Instantiation should be done with [from].
 */
class SongViewHolder private constructor(
    private val binding: ItemSongBinding,
    doOnClick: (data: Song) -> Unit,
    doOnLongClick: (data: Song, view: View) -> Unit
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
            doOnLongClick: (data: Song, view: View) -> Unit
        ): SongViewHolder {
            return SongViewHolder(
                ItemSongBinding.inflate(LayoutInflater.from(context)),
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
    doOnLongClick: (data: Album, view: View) -> Unit
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
            doOnLongClick: (data: Album, view: View) -> Unit
        ): AlbumViewHolder {
            return AlbumViewHolder(
                ItemAlbumBinding.inflate(LayoutInflater.from(context)),
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
    doOnLongClick: (data: Artist, view: View) -> Unit
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
            doOnLongClick: (data: Artist, view: View) -> Unit
        ): ArtistViewHolder {
            return ArtistViewHolder(
                ItemArtistBinding.inflate(LayoutInflater.from(context)),
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
    doOnLongClick: (data: Genre, view: View) -> Unit
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
            doOnLongClick: (data: Genre, view: View) -> Unit
        ): GenreViewHolder {
            return GenreViewHolder(
                ItemGenreBinding.inflate(LayoutInflater.from(context)),
                doOnClick, doOnLongClick
            )
        }
    }
}

/**
 * The Shared ViewHolder for a [Header]. Instantiation should be done with [from]
 */
class HeaderViewHolder(
    private val binding: ItemHeaderBinding
) : BaseViewHolder<Header>(binding, null, null) {

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
                ItemHeaderBinding.inflate(LayoutInflater.from(context))
            )
        }
    }
}
