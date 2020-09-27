package org.oxycblt.auxio.library.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemArtistBinding
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.recycler.BaseViewHolder
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

    inner class ViewHolder(
        private val binding: ItemArtistBinding
    ) : BaseViewHolder<Artist>(binding, listener) {

        override fun onBind(model: Artist) {
            binding.artist = model
            binding.artistName.requestLayout()
        }
    }
}
