package org.oxycblt.auxio.library.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.recycler.ShowMode
import org.oxycblt.auxio.recycler.viewholders.AlbumViewHolder
import org.oxycblt.auxio.recycler.viewholders.ArtistViewHolder
import org.oxycblt.auxio.recycler.viewholders.GenreViewHolder

// The primary RecyclerView adapter for the library. Displays genres, artists, and albums.
class LibraryAdapter(
    private val showMode: ShowMode,
    private val doOnClick: (data: BaseModel) -> Unit,
    private val doOnLongClick: (data: BaseModel, view: View) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data: List<BaseModel>

    init {
        // Assign the data on startup depending on the type
        data = when (showMode) {
            ShowMode.SHOW_GENRES -> listOf<Genre>()
            ShowMode.SHOW_ARTISTS -> listOf<Artist>()
            ShowMode.SHOW_ALBUMS -> listOf<Album>()

            else -> listOf<Artist>()
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Return a different View Holder depending on the show type
        return when (showMode) {
            ShowMode.SHOW_GENRES -> GenreViewHolder.from(parent.context, doOnClick, doOnLongClick)
            ShowMode.SHOW_ARTISTS -> ArtistViewHolder.from(parent.context, doOnClick, doOnLongClick)
            ShowMode.SHOW_ALBUMS -> AlbumViewHolder.from(parent.context, doOnClick, doOnLongClick)

            else -> error("Bad ShowMode given.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (showMode) {
            ShowMode.SHOW_GENRES -> (holder as GenreViewHolder).bind(data[position] as Genre)
            ShowMode.SHOW_ARTISTS -> (holder as ArtistViewHolder).bind(data[position] as Artist)
            ShowMode.SHOW_ALBUMS -> (holder as AlbumViewHolder).bind(data[position] as Album)

            else -> return
        }
    }

    // Update the data, as its an internal value.
    fun updateData(newData: List<BaseModel>) {
        data = newData

        notifyDataSetChanged()
    }
}
