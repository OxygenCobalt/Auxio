package org.oxycblt.auxio.recycler.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.AlbumItemBinding
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.recycler.viewholders.AlbumViewHolder

class AlbumDataAdapter(val data: List<Album>) : RecyclerView.Adapter<AlbumViewHolder>() {

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        return AlbumViewHolder(
            AlbumItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = data[position]

        holder.bind(album)
    }
}
