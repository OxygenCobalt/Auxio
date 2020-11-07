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

// Shared ViewHolders for each ViewModel, providing basic information
// All new instances should be created with from() instead of direct instantiation.

class GenreViewHolder private constructor(
    private val binding: ItemGenreBinding,
    doOnClick: (Genre) -> Unit
) : BaseViewHolder<Genre>(binding, doOnClick, null) {

    override fun onBind(data: Genre) {
        binding.genre = data
        binding.genreName.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 10

        fun from(context: Context, doOnClick: (Genre) -> Unit): GenreViewHolder {
            return GenreViewHolder(
                ItemGenreBinding.inflate(LayoutInflater.from(context)),
                doOnClick
            )
        }
    }
}

class ArtistViewHolder private constructor(
    private val binding: ItemArtistBinding,
    doOnClick: (Artist) -> Unit,
) : BaseViewHolder<Artist>(binding, doOnClick, null) {

    override fun onBind(data: Artist) {
        binding.artist = data
        binding.artistName.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 11

        fun from(context: Context, doOnClick: (Artist) -> Unit): ArtistViewHolder {
            return ArtistViewHolder(
                ItemArtistBinding.inflate(LayoutInflater.from(context)),
                doOnClick
            )
        }
    }
}

class AlbumViewHolder private constructor(
    private val binding: ItemAlbumBinding,
    doOnClick: (data: Album) -> Unit
) : BaseViewHolder<Album>(binding, doOnClick, null) {

    override fun onBind(data: Album) {
        binding.album = data
        binding.albumName.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 12

        fun from(context: Context, doOnClick: (data: Album) -> Unit): AlbumViewHolder {
            return AlbumViewHolder(
                ItemAlbumBinding.inflate(LayoutInflater.from(context)),
                doOnClick,
            )
        }
    }
}

// TODO: Add indicators to song recycler items when they're being played?

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
        const val ITEM_TYPE = 13

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

class HeaderViewHolder(
    private val binding: ItemHeaderBinding
) : BaseViewHolder<Header>(binding, null, null) {

    override fun onBind(data: Header) {
        binding.header = data
    }

    companion object {
        const val ITEM_TYPE = 14

        fun from(context: Context): HeaderViewHolder {
            return HeaderViewHolder(
                ItemHeaderBinding.inflate(LayoutInflater.from(context))
            )
        }
    }
}
