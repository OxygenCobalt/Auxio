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
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.ClickListener

// Basic ViewHolders for each music model.
// FIXME: Mode these type signaturs to something sensible.

class GenreViewHolder private constructor(
    listener: ClickListener<BaseModel>,
    private val binding: ItemGenreBinding
) : BaseViewHolder<BaseModel>(binding, listener) {

    override fun onBind(model: BaseModel) {
        binding.genre = model as Genre
        binding.genreName.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 10

        fun from(context: Context, listener: ClickListener<BaseModel>): GenreViewHolder {
            return GenreViewHolder(
                ClickListener { listener.onClick(it) },
                ItemGenreBinding.inflate(LayoutInflater.from(context))
            )
        }
    }
}

class ArtistViewHolder private constructor(
    listener: ClickListener<BaseModel>,
    private val binding: ItemArtistBinding
) : BaseViewHolder<BaseModel>(binding, listener) {

    override fun onBind(model: BaseModel) {
        binding.artist = model as Artist
        binding.artistName.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 11

        fun from(context: Context, listener: ClickListener<BaseModel>): ArtistViewHolder {
            return ArtistViewHolder(
                ClickListener { listener.onClick(it) },
                ItemArtistBinding.inflate(LayoutInflater.from(context))
            )
        }
    }
}

class AlbumViewHolder private constructor(
    listener: ClickListener<BaseModel>,
    private val binding: ItemAlbumBinding
) : BaseViewHolder<BaseModel>(binding, listener) {

    override fun onBind(model: BaseModel) {
        binding.album = model as Album
        binding.albumName.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 12

        fun from(context: Context, listener: ClickListener<BaseModel>): AlbumViewHolder {
            return AlbumViewHolder(
                listener,
                ItemAlbumBinding.inflate(LayoutInflater.from(context))
            )
        }
    }
}

class SongViewHolder private constructor(
    listener: ClickListener<BaseModel>,
    private val binding: ItemSongBinding
) : BaseViewHolder<BaseModel>(binding, listener) {

    override fun onBind(model: BaseModel) {
        binding.song = model as Song

        binding.songName.requestLayout()
        binding.songInfo.requestLayout()
    }

    companion object {
        const val ITEM_TYPE = 13

        fun from(context: Context, listener: ClickListener<BaseModel>): SongViewHolder {
            return SongViewHolder(
                listener,
                ItemSongBinding.inflate(LayoutInflater.from(context))
            )
        }
    }
}

class HeaderViewHolder(
    private val binding: ItemHeaderBinding
) : BaseViewHolder<BaseModel>(binding, null) {
    override fun onBind(model: BaseModel) {
        binding.header = model as Header
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
