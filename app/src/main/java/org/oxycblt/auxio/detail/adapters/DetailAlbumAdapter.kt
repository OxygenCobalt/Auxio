package org.oxycblt.auxio.detail.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.oxycblt.auxio.databinding.ItemArtistAlbumBinding
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder

class DetailAlbumAdapter(
    private val doOnClick: (Album) -> Unit
) : ListAdapter<Album, DetailAlbumAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemArtistAlbumBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Generic ViewHolder for a detail album
    inner class ViewHolder(
        private val binding: ItemArtistAlbumBinding
    ) : BaseViewHolder<Album>(binding, doOnClick) {

        override fun onBind(model: Album) {
            binding.album = model

            binding.albumName.requestLayout()
        }
    }
}
