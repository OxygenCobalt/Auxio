package org.oxycblt.auxio.playback.queue

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import org.oxycblt.auxio.databinding.ItemQueueSongBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder

class QueueAdapter(
    val touchHelper: ItemTouchHelper
) : ListAdapter<Song, QueueAdapter.ViewHolder>(DiffCallback<Song>()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemQueueSongBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Generic ViewHolder for a queue item
    inner class ViewHolder(
        private val binding: ItemQueueSongBinding,
    ) : BaseViewHolder<Song>(binding, null) {

        @SuppressLint("ClickableViewAccessibility")
        override fun onBind(data: Song) {
            binding.song = data

            binding.songName.requestLayout()
            binding.songInfo.requestLayout()

            binding.songDragHandle.setOnTouchListener { _, motionEvent ->
                binding.songDragHandle.performClick()

                if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                    touchHelper.startDrag(this)
                    return@setOnTouchListener true
                }

                false
            }
        }
    }
}
