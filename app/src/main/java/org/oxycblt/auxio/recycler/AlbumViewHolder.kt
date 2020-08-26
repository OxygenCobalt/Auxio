package org.oxycblt.auxio.recycler

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.oxycblt.auxio.databinding.AlbumItemBinding
import org.oxycblt.auxio.music.models.Album

// Generic ViewHolder for an album
class AlbumViewHolder(
    private var binding: AlbumItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    // Bind the view w/new data
    fun bind(album: Album) {
        binding.album = album

        // Load the album cover
        binding.cover.load(album.coverUri) {
            crossfade(true)
            placeholder(android.R.color.transparent)
            error(android.R.color.transparent)
        }

        binding.executePendingBindings()
    }
}
