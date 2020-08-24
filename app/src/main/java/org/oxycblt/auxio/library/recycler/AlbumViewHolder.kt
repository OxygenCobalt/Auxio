package org.oxycblt.auxio.library.recycler

import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.AlbumItemBinding
import org.oxycblt.auxio.music.models.Album

class AlbumViewHolder(
    private var binding: AlbumItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    // Bind the view w/new data
    fun bind(album: Album) {
        binding.album = album

        // Set an album cover [If possible]
        album.cover?.let { cover ->
            binding.cover.setImageBitmap(cover)
        }
        binding.executePendingBindings()
    }
}
