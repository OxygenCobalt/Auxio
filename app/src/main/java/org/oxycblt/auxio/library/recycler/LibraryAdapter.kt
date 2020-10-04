package org.oxycblt.auxio.library.recycler

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.recycler.viewholders.AlbumViewHolder
import org.oxycblt.auxio.recycler.viewholders.ArtistViewHolder
import org.oxycblt.auxio.recycler.viewholders.GenreViewHolder
import org.oxycblt.auxio.theme.SHOW_ALBUMS
import org.oxycblt.auxio.theme.SHOW_ARTISTS
import org.oxycblt.auxio.theme.SHOW_GENRES

// A ListAdapter that can contain three different types of ViewHolders depending
// the showmode given. It cannot display multiple types of viewholders *at once*.
class LibraryAdapter(
    private val showMode: Int,
    private val doOnClick: (BaseModel) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Create separate listeners for each type, as ClickListeners can be converted
    // to a type-specific form.
    private val genreListener = ClickListener<Genre> { doOnClick(it) }
    private val artistListener = ClickListener<Artist> { doOnClick(it) }
    private val albumListener = ClickListener<Album> { doOnClick(it) }

    private var data: List<BaseModel>

    init {
        // Assign the data on startup depending on the type
        data = when (showMode) {
            SHOW_GENRES -> listOf<Genre>()
            SHOW_ALBUMS -> listOf<Album>()
            SHOW_ARTISTS -> listOf<Artist>()

            else -> listOf<Artist>()
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Return a different View Holder depending on the show type
        return when (showMode) {
            SHOW_GENRES -> GenreViewHolder.from(parent.context, genreListener)
            SHOW_ARTISTS -> ArtistViewHolder.from(parent.context, artistListener)
            SHOW_ALBUMS -> AlbumViewHolder.from(parent.context, albumListener)
            else -> ArtistViewHolder.from(parent.context, artistListener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (showMode) {
            SHOW_GENRES -> (holder as GenreViewHolder).bind(data[position] as Genre)
            SHOW_ARTISTS -> (holder as ArtistViewHolder).bind(data[position] as Artist)
            SHOW_ALBUMS -> (holder as AlbumViewHolder).bind(data[position] as Album)

            else -> return
        }
    }

    // Update the data, as its an internal value.
    fun updateData(newData: List<BaseModel>) {
        data = newData

        notifyDataSetChanged()
    }
}
