package org.oxycblt.auxio.playback.queue

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.SongViewHolder

class QueueAdapter(
    private val doOnClick: (Song) -> Unit
) : ListAdapter<Song, SongViewHolder>(DiffCallback<Song>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder.from(parent.context, doOnClick)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
