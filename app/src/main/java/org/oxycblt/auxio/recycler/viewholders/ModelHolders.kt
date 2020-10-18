package org.oxycblt.auxio.recycler.viewholders

import android.content.Context
import android.view.LayoutInflater
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
    doOnClick: (Genre) -> Unit,
    private val binding: ItemGenreBinding
) : BaseViewHolder<Genre>(binding, doOnClick) {

    override fun onBind(model: Genre) {
        binding.genre = model
        binding.genreName.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 10

        fun from(context: Context, doOnClick: (Genre) -> Unit): GenreViewHolder {
            return GenreViewHolder(
                doOnClick,
                ItemGenreBinding.inflate(LayoutInflater.from(context))
            )
        }
    }
}

class ArtistViewHolder private constructor(
    doOnClick: (Artist) -> Unit,
    private val binding: ItemArtistBinding
) : BaseViewHolder<Artist>(binding, doOnClick) {

    override fun onBind(model: Artist) {
        binding.artist = model
        binding.artistName.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 11

        fun from(context: Context, doOnClick: (Artist) -> Unit): ArtistViewHolder {
            return ArtistViewHolder(
                doOnClick,
                ItemArtistBinding.inflate(LayoutInflater.from(context))
            )
        }
    }
}

class AlbumViewHolder private constructor(
    doOnClick: (Album) -> Unit,
    private val binding: ItemAlbumBinding
) : BaseViewHolder<Album>(binding, doOnClick) {

    override fun onBind(model: Album) {
        binding.album = model
        binding.albumName.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 12

        fun from(context: Context, doOnClick: (Album) -> Unit): AlbumViewHolder {
            return AlbumViewHolder(
                doOnClick,
                ItemAlbumBinding.inflate(LayoutInflater.from(context))
            )
        }
    }
}

// TODO: Add indicators to song recycler items when they're being played?

class SongViewHolder private constructor(
    doOnClick: (Song) -> Unit,
    private val binding: ItemSongBinding
) : BaseViewHolder<Song>(binding, doOnClick) {

    override fun onBind(model: Song) {
        binding.song = model

        binding.songName.requestLayout()
        binding.songInfo.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 13

        fun from(context: Context, doOnClick: (Song) -> Unit): SongViewHolder {
            return SongViewHolder(
                doOnClick,
                ItemSongBinding.inflate(LayoutInflater.from(context))
            )
        }
    }
}

class HeaderViewHolder(
    private val binding: ItemHeaderBinding
) : BaseViewHolder<Header>(binding, null) {
    override fun onBind(model: Header) {
        binding.header = model
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
