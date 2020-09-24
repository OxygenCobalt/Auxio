package org.oxycblt.auxio.detail.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.reycler.ClickListener
import org.oxycblt.auxio.databinding.ItemAlbumBinding
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.reycler.DiffCallback

class DetailAlbumAdapter(
    private val listener: ClickListener<Album>
) : ListAdapter<Album, DetailAlbumAdapter.ViewHolder>(DiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAlbumBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Generic ViewHolder for an album
    inner class ViewHolder(
        private val binding: ItemAlbumBinding
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
