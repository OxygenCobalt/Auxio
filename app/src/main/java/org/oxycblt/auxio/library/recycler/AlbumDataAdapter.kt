package org.oxycblt.auxio.library.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import org.oxycblt.auxio.databinding.LibraryItemBinding
import org.oxycblt.auxio.music.models.Album

class AlbumDataAdapter : ListAdapter<Album, LibraryViewHolder>(DiffCallback) {

    var data = listOf<Album>()
        set(newData) {
            field = newData
            submitList(data)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        return LibraryViewHolder(
            LibraryItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val album = getItem(position)

        holder.bindAlbum(album)
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
