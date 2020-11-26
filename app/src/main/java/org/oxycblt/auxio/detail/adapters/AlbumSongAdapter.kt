package org.oxycblt.auxio.detail.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.oxycblt.auxio.databinding.ItemAlbumSongBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder
import org.oxycblt.auxio.ui.accent
import org.oxycblt.auxio.ui.toColor

/**
 * An adapter for displaying the [Song]s of an album.
 */
class AlbumSongAdapter(
    private val doOnClick: (data: Song) -> Unit,
    private val doOnLongClick: (data: Song, view: View) -> Unit
) : ListAdapter<Song, AlbumSongAdapter.ViewHolder>(DiffCallback()) {
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
        private val binding: ItemAlbumSongBinding,
    ) : BaseViewHolder<Song>(binding, doOnClick, doOnLongClick) {
        private val normalColor = binding.songName.currentTextColor
        private val inactiveColor = binding.songTrack.currentTextColor

        override fun onBind(data: Song) {
            binding.song = data

            binding.songName.requestLayout()
        }

        fun setPlaying(context: Context) {
            val accentColor = accent.first.toColor(context)

            binding.songName.setTextColor(accentColor)
            binding.songTrack.setTextColor(accentColor)
        }

        fun removePlaying() {
            binding.songName.setTextColor(normalColor)
            binding.songTrack.setTextColor(inactiveColor)
        }
    }
}
