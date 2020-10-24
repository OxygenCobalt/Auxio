package org.oxycblt.auxio.library.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.AlbumViewHolder
import org.oxycblt.auxio.recycler.viewholders.ArtistViewHolder
import org.oxycblt.auxio.recycler.viewholders.GenreViewHolder
import org.oxycblt.auxio.recycler.viewholders.HeaderViewHolder
import org.oxycblt.auxio.recycler.viewholders.SongViewHolder

class SearchAdapter(
    private val doOnClick: (data: BaseModel) -> Unit
) : ListAdapter<BaseModel, RecyclerView.ViewHolder>(DiffCallback<BaseModel>()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Genre -> GenreViewHolder.ITEM_TYPE
            is Artist -> ArtistViewHolder.ITEM_TYPE
            is Album -> AlbumViewHolder.ITEM_TYPE
            is Song -> SongViewHolder.ITEM_TYPE
            is Header -> HeaderViewHolder.ITEM_TYPE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            GenreViewHolder.ITEM_TYPE -> GenreViewHolder.from(parent.context, doOnClick)
            ArtistViewHolder.ITEM_TYPE -> ArtistViewHolder.from(parent.context, doOnClick)
            AlbumViewHolder.ITEM_TYPE -> AlbumViewHolder.from(parent.context, doOnClick)
            SongViewHolder.ITEM_TYPE -> SongViewHolder.from(parent.context, doOnClick)
            HeaderViewHolder.ITEM_TYPE -> HeaderViewHolder.from(parent.context)

            else -> HeaderViewHolder.from(parent.context)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is GenreViewHolder -> holder.bind(getItem(position) as Genre)
            is ArtistViewHolder -> holder.bind(getItem(position) as Artist)
            is AlbumViewHolder -> holder.bind(getItem(position) as Album)
            is SongViewHolder -> holder.bind(getItem(position) as Song)
            is HeaderViewHolder -> holder.bind(getItem(position) as Header)

            else -> return
        }
    }
}
