package org.oxycblt.auxio.library.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemAlbumBinding
import org.oxycblt.auxio.databinding.ItemArtistBinding
import org.oxycblt.auxio.databinding.ItemGenreBinding
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.theme.SHOW_ALBUMS
import org.oxycblt.auxio.theme.SHOW_ARTISTS
import org.oxycblt.auxio.theme.SHOW_GENRES

// A Great Value androidx ListAdapter that can display three types of ViewHolders.
class LibraryAdapter(
    private val showMode: Int,
    val listener: ClickListener<BaseModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
            SHOW_GENRES -> GenreViewHolder(
                listener,
                ItemGenreBinding.inflate(LayoutInflater.from(parent.context))
            )

            SHOW_ARTISTS -> ArtistViewHolder(
                listener,
                ItemArtistBinding.inflate(LayoutInflater.from(parent.context))
            )

            SHOW_ALBUMS -> AlbumViewHolder(
                listener,
                ItemAlbumBinding.inflate(LayoutInflater.from(parent.context))
            )

            else -> ArtistViewHolder(
                listener,
                ItemArtistBinding.inflate(LayoutInflater.from(parent.context))
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (showMode) {
            SHOW_GENRES -> holder as GenreViewHolder
            SHOW_ARTISTS -> holder as ArtistViewHolder
            SHOW_ALBUMS -> holder as AlbumViewHolder

            else -> return
        }.bind(data[position])
    }

    // Update the data, as its an internal value.
    fun updateData(newData: List<BaseModel>) {
        data = newData

        notifyDataSetChanged()
    }
}
