package org.oxycblt.auxio.songs

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemSongBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.Highlightable
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder
import org.oxycblt.auxio.ui.accent
import org.oxycblt.auxio.ui.setTextColorResource

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
) : RecyclerView.Adapter<SongsAdapter.SongViewHolder>() {

/*    private var currentSong: Song? = null
    private var lastHolder: Highlightable? = null*/

    override fun getItemCount(): Int = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            ItemSongBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(data[position])

/*        if (currentSong != null) {
            if (data[position].id == currentSong?.id) {
                // Reset the last ViewHolder before assigning the new, correct one to be highlighted
                lastHolder?.setHighlighted(false)
                lastHolder = holder
                holder.setHighlighted(true)
            } else {
                holder.setHighlighted(false)
            }
        }*/
    }

/*    fun setCurrentSong(song: Song?) {
        // Clear out the last ViewHolder as a song update usually signifies that this current
        // ViewHolder is likely invalid.
        lastHolder?.setHighlighted(false)
        lastHolder = null

        currentSong = song
    }*/

    inner class SongViewHolder(
        private val binding: ItemSongBinding
    ) : BaseViewHolder<Song>(binding, doOnClick, doOnLongClick), Highlightable {
        private val normalTextColor = binding.songName.currentTextColor

        override fun onBind(data: Song) {
            binding.song = data

            binding.songName.requestLayout()
            binding.songInfo.requestLayout()
        }

        override fun setHighlighted(isHighlighted: Boolean) {
            if (isHighlighted) {
                binding.songName.setTextColorResource(accent.first)
            } else {
                binding.songName.setTextColor(normalTextColor)
            }
        }
    }
}
