package org.oxycblt.auxio.recycler.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.AlbumItemBinding
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.recycler.viewholders.AlbumViewHolder
import org.oxycblt.auxio.recycler.viewholders.ClickListener

class AlbumAdapter(
    private val data: List<Album>,
    private val listener: ClickListener<Album>
) : RecyclerView.Adapter<AlbumViewHolder>() {

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val binding = AlbumItemBinding.inflate(LayoutInflater.from(parent.context))

        // Force the item to *actually* be the screen width so ellipsizing can work.
        binding.root.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
        )

        return AlbumViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = data[position]

        holder.itemView.setOnClickListener {
            listener.onClick(album)
        }

        holder.bind(album)
    }
}
