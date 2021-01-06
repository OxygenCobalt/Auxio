package org.oxycblt.auxio.songs

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.HeaderViewHolder
import org.oxycblt.auxio.recycler.viewholders.SongViewHolder

class SongSearchAdapter(
    private val doOnClick: (data: Song) -> Unit,
    private val doOnLongClick: (data: Song, view: View) -> Unit
) : ListAdapter<BaseModel, RecyclerView.ViewHolder>(DiffCallback()) {
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Header -> HeaderViewHolder.ITEM_TYPE
            is Song -> SongViewHolder.ITEM_TYPE

            else -> -1
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HeaderViewHolder.ITEM_TYPE -> HeaderViewHolder.from(parent.context)
            SongViewHolder.ITEM_TYPE -> SongViewHolder.from(parent.context, doOnClick, doOnLongClick)

            else -> error("Invalid viewholder item type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Header -> (holder as HeaderViewHolder).bind(item)
            is Song -> (holder as SongViewHolder).bind(item)
        }
    }
}
