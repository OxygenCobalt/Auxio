package org.oxycblt.auxio.library.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.AlbumViewHolder
import org.oxycblt.auxio.recycler.viewholders.ArtistViewHolder
import org.oxycblt.auxio.recycler.viewholders.GenreViewHolder
import org.oxycblt.auxio.recycler.viewholders.HeaderViewHolder
import org.oxycblt.auxio.recycler.viewholders.SongViewHolder

class SearchAdapter(
    private val doOnClick: (BaseModel) -> Unit
) : ListAdapter<BaseModel, RecyclerView.ViewHolder>(DiffCallback<BaseModel>()) {

    // Create separate listeners for each type, as a BaseModel ClickListener cant be
    // casted to a ClickListener of a class that inherits BaseModel.
    // FIXME: Maybe there's a way for this to be improved?
    private val genreListener = ClickListener<Genre> { doOnClick(it) }
    private val artistListener = ClickListener<Artist> { doOnClick(it) }
    private val albumListener = ClickListener<Album> { doOnClick(it) }
    private val songListener = ClickListener<Song> { doOnClick(it) }

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
            GenreViewHolder.ITEM_TYPE -> GenreViewHolder.from(parent.context, genreListener)
            ArtistViewHolder.ITEM_TYPE -> ArtistViewHolder.from(parent.context, artistListener)
            AlbumViewHolder.ITEM_TYPE -> AlbumViewHolder.from(parent.context, albumListener)
            SongViewHolder.ITEM_TYPE -> SongViewHolder.from(parent.context, songListener)
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
