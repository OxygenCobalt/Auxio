package org.oxycblt.auxio.library.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
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
import org.oxycblt.auxio.recycler.DiffCallback

class SearchAdapter(
    private val listener: ClickListener<BaseModel>
) : ListAdapter<BaseModel, RecyclerView.ViewHolder>(DiffCallback<BaseModel>()) {
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Genre -> ITEM_TYPE_GENRE
            is Artist -> ITEM_TYPE_ARTIST
            is Album -> ITEM_TYPE_ALBUM
            is Song -> ITEM_TYPE_SONG
            is Header -> ITEM_TYPE_HEADER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_GENRE -> GenreViewHolder(
                listener,
                ItemGenreBinding.inflate(
                    LayoutInflater.from(parent.context)
                )
            )

            ITEM_TYPE_ARTIST -> ArtistViewHolder(
                listener,
                ItemArtistBinding.inflate(
                    LayoutInflater.from(parent.context)
                )
            )

            ITEM_TYPE_ALBUM -> AlbumViewHolder(
                listener,
                ItemAlbumBinding.inflate(
                    LayoutInflater.from(parent.context)
                )
            )

            ITEM_TYPE_SONG -> SongViewHolder(
                listener,
                ItemSongBinding.inflate(
                    LayoutInflater.from(parent.context)
                )
            )

            ITEM_TYPE_HEADER -> HeaderViewHolder(
                ItemHeaderBinding.inflate(
                    LayoutInflater.from(parent.context)
                )
            )

            else -> ArtistViewHolder(
                listener,
                ItemArtistBinding.inflate(
                    LayoutInflater.from(parent.context)
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GenreViewHolder -> holder
            is ArtistViewHolder -> holder
            is AlbumViewHolder -> holder
            is SongViewHolder -> holder
            is HeaderViewHolder -> holder

            else -> return
        }.onBind(getItem(position))
    }
}
