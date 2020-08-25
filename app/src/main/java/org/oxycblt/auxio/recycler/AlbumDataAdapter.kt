package org.oxycblt.auxio.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.AlbumItemBinding
import org.oxycblt.auxio.music.models.Album

class AlbumDataAdapter : ListAdapter<Album, AlbumViewHolder>(DiffCallback) {

    var data = listOf<Album>()
        set(newData) {
            field = newData
            submitList(data)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        return AlbumViewHolder(
            AlbumItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = getItem(position)

        holder.bind(album)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Album>() {
        override fun areItemsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Album, newItem: Album): Boolean {
            return oldItem.id == newItem.id
        }
    }
}

class AlbumViewHolder(
    private var binding: AlbumItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    // Bind the view w/new data
    fun bind(album: Album) {
        binding.album = album

        if (album.cover == null) {
            // If there is no cover, clear the ImageView so that the previous
            // View's cover doesnt stick around.
            binding.cover.setImageResource(android.R.color.transparent)
        } else {
            binding.cover.setImageBitmap(album.cover)
        }

        binding.executePendingBindings()
    }
}
