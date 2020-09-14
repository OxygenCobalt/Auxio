package org.oxycblt.auxio.detail.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.ClickListener
import org.oxycblt.auxio.databinding.ItemAlbumBigBinding
import org.oxycblt.auxio.music.models.Album

class DetailAlbumAdapter(
    private val data: List<Album>,
    private val listener: ClickListener<Album>
) : RecyclerView.Adapter<DetailAlbumAdapter.ViewHolder>() {

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAlbumBigBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = data[position]

        holder.bind(album)
    }

    // Generic ViewHolder for an album
    inner class ViewHolder(
        private val binding: ItemAlbumBigBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Force the viewholder to *actually* be the screen width so ellipsizing can work.
            binding.root.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }

        // Bind the view w/new data
        fun bind(album: Album) {
            binding.album = album

            binding.root.setOnClickListener {
                listener.onClick(album)
            }

            // Force-update the layout so ellipsizing works.
            binding.albumName.requestLayout()
            binding.executePendingBindings()
        }
    }
}
