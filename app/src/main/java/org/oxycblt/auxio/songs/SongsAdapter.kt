package org.oxycblt.auxio.songs

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.viewholders.SongViewHolder

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
) : RecyclerView.Adapter<SongViewHolder>() {

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder.from(parent.context, doOnClick, doOnLongClick)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(data[position])
    }
}
