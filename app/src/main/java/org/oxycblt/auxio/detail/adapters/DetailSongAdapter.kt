package org.oxycblt.auxio.detail.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.ListAdapter
import org.oxycblt.auxio.databinding.ItemAlbumSongBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder
import org.oxycblt.auxio.theme.accent
import org.oxycblt.auxio.theme.toColor

class DetailSongAdapter(
    private val doOnClick: (Song) -> Unit
) : ListAdapter<Song, DetailSongAdapter.ViewHolder>(DiffCallback()) {
    private var currentSong: Song? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAlbumSongBinding.inflate(LayoutInflater.from(parent.context)),
            accent.first.toColor(parent.context)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Generic ViewHolder for a song
    inner class ViewHolder(
        private val binding: ItemAlbumSongBinding,
        @ColorInt private val resolvedAccent: Int
    ) : BaseViewHolder<Song>(binding, doOnClick) {

        override fun onBind(model: Song) {
            binding.song = model

            if (model == currentSong) {
                binding.songName.setTextColor(resolvedAccent)
            }

            binding.songName.requestLayout()
        }
    }
}
