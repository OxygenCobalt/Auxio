package org.oxycblt.auxio.library.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemArtistBinding
import org.oxycblt.auxio.music.models.Artist
import org.oxycblt.auxio.recycler.ClickListener

class ArtistAdapter(
    private val data: List<Artist>,
    private val listener: ClickListener<Artist>
) : RecyclerView.Adapter<ArtistAdapter.ViewHolder>() {

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemArtistBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    // Generic ViewHolder for an artist
    inner class ViewHolder(
        private val binding: ItemArtistBinding
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

            binding.root.setOnClickListener { listener.onClick(artist) }

            // Force-update the layout so ellipsizing works.
            binding.artistName.requestLayout()
            binding.executePendingBindings()
        }
    }
}
