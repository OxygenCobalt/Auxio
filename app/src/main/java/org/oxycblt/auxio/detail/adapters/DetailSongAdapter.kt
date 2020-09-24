package org.oxycblt.auxio.detail.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemAlbumSongBinding
import org.oxycblt.auxio.music.models.Song
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.recycler.SongDiffCallback

class DetailSongAdapter(
    private val listener: ClickListener<Song>
) : ListAdapter<Song, DetailSongAdapter.ViewHolder>(SongDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAlbumSongBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Generic ViewHolder for a song
    inner class ViewHolder(
        private val binding: ItemAlbumSongBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Force the viewholder to *actually* be the screen width so ellipsizing can work.
            binding.root.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }

        // Bind the view w/new data
        fun bind(song: Song) {
            binding.song = song

            binding.root.setOnClickListener {
                listener.onClick(song)
            }

            // Force-update the layout so ellipsizing works.
            binding.songName.requestLayout()
            binding.executePendingBindings()
        }
    }
}
