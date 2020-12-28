package org.oxycblt.auxio.detail.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.oxycblt.auxio.databinding.ItemGenreSongBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder

/**
 * An adapter for displaying the [Song]s of a genre.
 */
class GenreSongAdapter(
    private val doOnClick: (data: Song) -> Unit,
    private val doOnLongClick: (data: Song, view: View) -> Unit
) : ListAdapter<Song, GenreSongAdapter.GenreSongViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreSongViewHolder {
        return GenreSongViewHolder(
            ItemGenreSongBinding.inflate(LayoutInflater.from(parent.context)),
            doOnClick, doOnLongClick
        )
    }

    override fun onBindViewHolder(holder: GenreSongViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GenreSongViewHolder(
        private val binding: ItemGenreSongBinding,
        doOnClick: (data: Song) -> Unit,
        doOnLongClick: (data: Song, view: View) -> Unit
    ) : BaseViewHolder<Song>(binding, doOnClick, doOnLongClick) {

        override fun onBind(data: Song) {
            binding.song = data

            binding.songName.requestLayout()
            binding.songInfo.requestLayout()
        }
    }
}
