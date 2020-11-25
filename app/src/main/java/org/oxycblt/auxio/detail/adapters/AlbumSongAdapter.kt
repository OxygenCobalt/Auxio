package org.oxycblt.auxio.detail.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.oxycblt.auxio.databinding.ItemAlbumSongBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder

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

        override fun onBind(data: Song) {
            binding.song = data

            binding.songName.requestLayout()
        }
    }
}
