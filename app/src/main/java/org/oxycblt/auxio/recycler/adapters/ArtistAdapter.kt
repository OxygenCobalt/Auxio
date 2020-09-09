package org.oxycblt.auxio.recycler.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ArtistItemBinding
import org.oxycblt.auxio.music.models.Artist
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.recycler.viewholders.ArtistViewHolder

class ArtistAdapter(
    private val data: List<Artist>,
    private val listener: ClickListener<Artist>
) : RecyclerView.Adapter<ArtistViewHolder>() {

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val binding = ArtistItemBinding.inflate(LayoutInflater.from(parent.context))

        // Force the item to *actually* be the screen width so ellipsizing can work.
        binding.root.layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT
        )

        return ArtistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        val artist = data[position]

        holder.itemView.setOnClickListener {
            listener.onClick(artist)
        }

        holder.bind(artist)
    }
}
