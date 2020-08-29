package org.oxycblt.auxio.recycler.viewholders

import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.SongItemBinding
import org.oxycblt.auxio.music.models.Song

// Generic ViewHolder for a song
class SongViewHolder(
    private var binding: SongItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    // Bind the view w/new data
    fun bind(song: Song) {
        binding.song = song

        binding.executePendingBindings()
    }
}
