package org.oxycblt.auxio.library.recycler

import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.LibraryItemBinding
import org.oxycblt.auxio.music.models.Album
import org.oxycblt.auxio.music.models.Artist

class LibraryViewHolder(
    private var binding: LibraryItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    // Bind the view w/new data
    fun bindAlbum(album: Album) {
        binding.album = album
        binding.executePendingBindings()
    }

    fun bindArtist(artist: Artist) {
        // TODO: Not implemented.
    }
}
