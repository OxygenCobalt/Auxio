package org.oxycblt.auxio.songs

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.viewholders.SongViewHolder

class SongAdapter(
    private val data: List<Song>,
    private val doOnClick: (data: Song) -> Unit
) : RecyclerView.Adapter<SongViewHolder>() {

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder.from(parent.context, doOnClick)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(data[position])
    }
}
