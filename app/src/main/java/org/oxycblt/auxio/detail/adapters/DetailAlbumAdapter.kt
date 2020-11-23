package org.oxycblt.auxio.detail.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.oxycblt.auxio.databinding.ItemArtistAlbumBinding
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder

class DetailAlbumAdapter(
    private val doOnClick: (data: Album) -> Unit,
    private val doOnLongClick: (data: Album, view: View) -> Unit
) : ListAdapter<Album, DetailAlbumAdapter.AlbumViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        return AlbumViewHolder(
            ItemArtistAlbumBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Generic ViewHolder for a detail album
    inner class AlbumViewHolder(
        private val binding: ItemArtistAlbumBinding,
    ) : BaseViewHolder<Album>(binding, doOnClick, doOnLongClick) {

        override fun onBind(data: Album) {
            binding.album = data

            binding.albumName.requestLayout()
        }
    }
}
