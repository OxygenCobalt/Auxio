package org.oxycblt.auxio.recycler.viewholders

import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.oxycblt.auxio.databinding.SongItemBinding
import org.oxycblt.auxio.music.models.Song

// Generic ViewHolder for a song
class SongViewHolder(
    private var binding: SongItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    // Bind the view w/new data
    fun bind(song: Song) {
        binding.song = song

        // Load the album cover
        binding.cover.load(song.album.coverUri) {
            crossfade(true)
            placeholder(android.R.color.transparent)
            error(android.R.color.transparent)
        }

        binding.executePendingBindings()
    }
}
