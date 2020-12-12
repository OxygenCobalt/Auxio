package org.oxycblt.auxio.library.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.recycler.viewholders.AlbumViewHolder
import org.oxycblt.auxio.recycler.viewholders.ArtistViewHolder
import org.oxycblt.auxio.recycler.viewholders.GenreViewHolder

/**
 * A near-identical adapter as [SearchAdapter] but this one isn't a [ListAdapter]
 * Id love to unify these two adapters but that triggers a bug on the android backend, so...
 * @author OxygenCobalt
 */
class LibraryAdapter(
    private val doOnClick: (data: BaseModel) -> Unit,
    private val doOnLongClick: (data: BaseModel, view: View) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data = listOf<BaseModel>()

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return when (data[position]) {
            is Genre -> GenreViewHolder.ITEM_TYPE
            is Artist -> ArtistViewHolder.ITEM_TYPE
            is Album -> AlbumViewHolder.ITEM_TYPE

            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            GenreViewHolder.ITEM_TYPE -> GenreViewHolder.from(
                parent.context, doOnClick, doOnLongClick
            )

            ArtistViewHolder.ITEM_TYPE -> ArtistViewHolder.from(
                parent.context, doOnClick, doOnLongClick
            )

            AlbumViewHolder.ITEM_TYPE -> AlbumViewHolder.from(
                parent.context, doOnClick, doOnLongClick
            )

            else -> error("Invalid viewholder item type.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = data[position]) {
            is Genre -> (holder as GenreViewHolder).bind(item)
            is Artist -> (holder as ArtistViewHolder).bind(item)
            is Album -> (holder as AlbumViewHolder).bind(item)
        }
    }

    fun updateData(newData: List<BaseModel>) {
        data = newData

        notifyDataSetChanged()
    }
}
