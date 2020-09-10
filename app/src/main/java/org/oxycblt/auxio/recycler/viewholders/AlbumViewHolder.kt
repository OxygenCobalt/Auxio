package org.oxycblt.auxio.recycler.viewholders

import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemAlbumBinding
import org.oxycblt.auxio.music.models.Album

// Generic ViewHolder for an album
class AlbumViewHolder(
    private val binding: ItemAlbumBinding
) : RecyclerView.ViewHolder(binding.root) {

    // Bind the view w/new data
    fun bind(album: Album) {
        binding.album = album

        binding.albumName.requestLayout()

        binding.executePendingBindings()
    }
}
