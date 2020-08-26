package org.oxycblt.auxio.recycler

import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.AlbumItemBinding
import org.oxycblt.auxio.music.models.Album

// Generic ViewHolder for an album
class AlbumViewHolder(
    private var binding: AlbumItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    // Bind the view w/new data
    fun bind(album: Album) {
        binding.album = album

        if (album.cover == null) {
            // If there is no cover, clear the ImageView so that the previous
            // View's cover doesn't stick around.
            binding.cover.setImageResource(android.R.color.transparent)
        } else {
            binding.cover.setImageBitmap(album.cover)
        }

        binding.executePendingBindings()
    }
}
