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
import org.oxycblt.auxio.recycler.ClickListener

// Basic ViewHolders for each music model.

class GenreViewHolder private constructor(
    listener: ClickListener<Genre>,
    private val binding: ItemGenreBinding
) : BaseViewHolder<Genre>(binding, listener) {

    override fun onBind(model: Genre) {
        binding.genre = model
        binding.genreName.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 10

        fun from(context: Context, listener: ClickListener<Genre>): GenreViewHolder {
            return GenreViewHolder(
                ClickListener { listener.onClick(it) },
                ItemGenreBinding.inflate(LayoutInflater.from(context))
            )
        }
    }
}

class ArtistViewHolder private constructor(
    listener: ClickListener<Artist>,
    private val binding: ItemArtistBinding
) : BaseViewHolder<Artist>(binding, listener) {

    override fun onBind(model: Artist) {
        binding.artist = model
        binding.artistName.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 11

        fun from(context: Context, listener: ClickListener<Artist>): ArtistViewHolder {
            return ArtistViewHolder(
                ClickListener { listener.onClick(it) },
                ItemArtistBinding.inflate(LayoutInflater.from(context))
            )
        }
    }
}

class AlbumViewHolder private constructor(
    listener: ClickListener<Album>,
    private val binding: ItemAlbumBinding
) : BaseViewHolder<Album>(binding, listener) {

    override fun onBind(model: Album) {
        binding.album = model
        binding.albumName.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 12

        fun from(context: Context, listener: ClickListener<Album>): AlbumViewHolder {
            return AlbumViewHolder(
                listener,
                ItemAlbumBinding.inflate(LayoutInflater.from(context))
            )
        }
    }
}

class SongViewHolder private constructor(
    listener: ClickListener<Song>,
    private val binding: ItemSongBinding
) : BaseViewHolder<Song>(binding, listener) {

    override fun onBind(model: Song) {
        binding.song = model

        binding.songName.requestLayout()
        binding.songInfo.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 13

        fun from(context: Context, listener: ClickListener<Song>): SongViewHolder {
            return SongViewHolder(
                listener,
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
