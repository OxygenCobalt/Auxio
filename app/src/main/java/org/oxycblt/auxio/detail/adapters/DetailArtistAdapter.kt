package org.oxycblt.auxio.detail.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.oxycblt.auxio.databinding.ItemGenreArtistBinding
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder

class DetailArtistAdapter(
    private val doOnClick: (data: Artist) -> Unit
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
    ) : BaseViewHolder<Artist>(binding, doOnClick, null) {

        override fun onBind(data: Artist) {
            binding.artist = data

            binding.artistName.requestLayout()
        }
    }
}
