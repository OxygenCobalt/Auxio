package org.oxycblt.auxio.playback.queue

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemQueueSongBinding
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder
import org.oxycblt.auxio.recycler.viewholders.HeaderViewHolder

class QueueAdapter(
    val touchHelper: ItemTouchHelper
) : ListAdapter<BaseModel, RecyclerView.ViewHolder>(DiffCallback<BaseModel>()) {
    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)

        return if (item is Header)
            HeaderViewHolder.ITEM_TYPE
        else
            QUEUE_ITEM_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HeaderViewHolder.ITEM_TYPE -> HeaderViewHolder.from(parent.context)
            QUEUE_ITEM_TYPE -> ViewHolder(
                ItemQueueSongBinding.inflate(LayoutInflater.from(parent.context))
            )
            else -> error("Someone messed with the ViewHolder item types. Tell OxygenCobalt.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Song -> (holder as ViewHolder).bind(item)
            is Header -> (holder as HeaderViewHolder).bind(item)

            else -> {
                Log.d(this::class.simpleName, "Bad data fed to QueueAdapter.")
            }
        }
    }

    // Generic ViewHolder for a queue item
    inner class ViewHolder(
        private val binding: ItemQueueSongBinding,
    ) : BaseViewHolder<Song>(binding, null, null) {

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

    companion object {
        const val QUEUE_ITEM_TYPE = 0xA015
    }
}
