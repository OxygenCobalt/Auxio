package org.oxycblt.auxio.recycler.viewholders

import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ArtistItemBinding
import org.oxycblt.auxio.music.models.Artist

// Generic ViewHolder for an album
class ArtistViewHolder(
    private val binding: ArtistItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    // Bind the view w/new data
    fun bind(artist: Artist) {
        binding.artist = artist

        binding.artistName.requestLayout()

        binding.executePendingBindings()
    }
}
