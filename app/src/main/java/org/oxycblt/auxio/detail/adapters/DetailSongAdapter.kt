package org.oxycblt.auxio.detail.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.oxycblt.auxio.databinding.ItemAlbumSongBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.BaseViewHolder
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.recycler.DiffCallback

class DetailSongAdapter(
    private val listener: ClickListener<Song>
) : ListAdapter<Song, DetailSongAdapter.ViewHolder>(DiffCallback()) {

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
    ) : BaseViewHolder<Song>(binding, listener) {

        override fun onBind(model: Song) {
            binding.song = model
            binding.songName.requestLayout()
        }
    }
}
