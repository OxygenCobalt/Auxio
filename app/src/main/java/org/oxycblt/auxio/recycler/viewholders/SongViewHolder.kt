package org.oxycblt.auxio.recycler.viewholders

import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.SongItemBinding
import org.oxycblt.auxio.music.models.Song

// Generic ViewHolder for a song
class SongViewHolder(
    private val binding: SongItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    // Bind the view w/new data
    fun bind(song: Song) {
        binding.song = song

        binding.songName.requestLayout()
        binding.songInfo.requestLayout()

        binding.executePendingBindings()
    }
}
