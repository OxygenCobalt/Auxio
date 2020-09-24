package org.oxycblt.auxio.songs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemSongBinding
import org.oxycblt.auxio.music.models.Song
import org.oxycblt.auxio.recycler.ClickListener

class SongAdapter(
    private val data: List<Song>,
    private val listener: ClickListener<Song>
) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSongBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    // Generic ViewHolder for an album
    inner class ViewHolder(
        private val binding: ItemSongBinding
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

            // Force-update the layouts so ellipsizing works.
            binding.songName.requestLayout()
            binding.songInfo.requestLayout()

            binding.executePendingBindings()
        }
    }
}
