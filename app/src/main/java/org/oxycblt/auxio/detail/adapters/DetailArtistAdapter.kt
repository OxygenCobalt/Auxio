package org.oxycblt.auxio.detail.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.oxycblt.auxio.databinding.ItemGenreArtistBinding
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.recycler.BaseViewHolder
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
    ) : BaseViewHolder<Artist>(binding, listener) {

        override fun onBind(model: Artist) {
            binding.artist = model

            binding.artistName.requestLayout()
        }
    }
}
