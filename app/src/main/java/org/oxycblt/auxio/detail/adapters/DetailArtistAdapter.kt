package org.oxycblt.auxio.detail.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemGenreArtistBinding
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.recycler.DiffCallback

class DetailArtistAdapter(
    private val listener: ClickListener<Artist>
) : ListAdapter<Artist, DetailArtistAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemGenreArtistBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Generic ViewHolder for an album
    inner class ViewHolder(
        private val binding: ItemGenreArtistBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            // Force the viewholder to *actually* be the screen width so ellipsizing can work.
            binding.root.layoutParams = RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
            )
        }

        // Bind the view w/new data
        fun bind(artist: Artist) {
            binding.artist = artist

            binding.root.setOnClickListener {
                listener.onClick(artist)
            }

            // Force-update the layout so ellipsizing works.
            binding.artistImage.requestLayout()
            binding.executePendingBindings()
        }
    }
}
