package org.oxycblt.auxio.songs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemSongBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.ClickListener
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder

class SongAdapter(
    private val data: List<Song>,
    private val listener: ClickListener<Song>
) : RecyclerView.Adapter<SongAdapter.ViewHolder>() {

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSongBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(
        private val binding: ItemSongBinding
    ) : BaseViewHolder<Song>(binding, listener) {

        override fun onBind(model: Song) {
            binding.song = model

            binding.songName.requestLayout()
            binding.songInfo.requestLayout()
        }
    }
}
