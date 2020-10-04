package org.oxycblt.auxio.library.recycler

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
import org.oxycblt.auxio.recycler.BaseViewHolder
import org.oxycblt.auxio.recycler.ClickListener

const val ITEM_TYPE_GENRE = 10
const val ITEM_TYPE_ARTIST = 11
const val ITEM_TYPE_ALBUM = 12
const val ITEM_TYPE_SONG = 13
const val ITEM_TYPE_HEADER = 14

class GenreViewHolder(
    listener: ClickListener<BaseModel>,
    private val binding: ItemGenreBinding
) : BaseViewHolder<BaseModel>(binding, listener) {

    override fun onBind(model: BaseModel) {
        binding.genre = model as Genre
        binding.genreName.requestLayout()
    }
}

class ArtistViewHolder(
    listener: ClickListener<BaseModel>,
    private val binding: ItemArtistBinding
) : BaseViewHolder<BaseModel>(binding, listener) {

    override fun onBind(model: BaseModel) {
        binding.artist = model as Artist
        binding.artistName.requestLayout()
    }
}

class AlbumViewHolder(
    listener: ClickListener<BaseModel>,
    private val binding: ItemAlbumBinding
) : BaseViewHolder<BaseModel>(binding, listener) {

    override fun onBind(model: BaseModel) {
        binding.album = model as Album
        binding.albumName.requestLayout()
    }
}

class SongViewHolder(
    listener: ClickListener<BaseModel>,
    private val binding: ItemSongBinding
) : BaseViewHolder<BaseModel>(binding, listener) {

    override fun onBind(model: BaseModel) {
        binding.song = model as Song

        binding.songName.requestLayout()
        binding.songInfo.requestLayout()
    }
}

class HeaderViewHolder(
    private val binding: ItemHeaderBinding
) : BaseViewHolder<BaseModel>(binding, null) {
    override fun onBind(model: BaseModel) {
        binding.header = model as Header
    }
}
