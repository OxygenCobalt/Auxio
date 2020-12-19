package org.oxycblt.auxio.songs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemBasicSongBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder

/**
 * The adapter for [SongsFragment], shows basic songs without durations.
 * @param data List of [Song]s to be shown
 * @param doOnClick What to do on a click action
 * @param doOnLongClick What to do on a long click action
 */
class SongsAdapter(
    private val data: List<Song>,
    private val doOnClick: (data: Song) -> Unit,
    private val doOnLongClick: (data: Song, view: View) -> Unit
) : RecyclerView.Adapter<SongsAdapter.ViewHolder>() {

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBasicSongBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(
        private val binding: ItemBasicSongBinding
    ) : BaseViewHolder<Song>(binding, doOnClick, doOnLongClick) {

        override fun onBind(data: Song) {
            binding.song = data

            binding.songName.requestLayout()
            binding.songInfo.requestLayout()
        }
    }
}
