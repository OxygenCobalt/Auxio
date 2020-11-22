package org.oxycblt.auxio.playback.queue

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.ItemQueueSongBinding
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder
import org.oxycblt.auxio.recycler.viewholders.HeaderViewHolder

/**
 * The single adapter for both the Next Queue and the User Queue.
 * - [submitList] is for the plain async diff calculations, use this if you
 * have no idea what the differences are between the old data & the new data
 * - [removeItem] and [moveItems] are used by [org.oxycblt.auxio.playback.PlaybackViewModel]
 * so that this adapter doesn't flip-out when items are moved (Which happens with [AsyncListDiffer])
 * @author OxygenCobalt
 */
class QueueAdapter(
    private val touchHelper: ItemTouchHelper
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var data = mutableListOf<BaseModel>()
    private var listDiffer = AsyncListDiffer(this, DiffCallback())

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        val item = data[position]

        return if (item is Header)
            HeaderViewHolder.ITEM_TYPE
        else
            QUEUE_ITEM_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HeaderViewHolder.ITEM_TYPE -> HeaderViewHolder.from(parent.context)
            QUEUE_ITEM_TYPE -> QueueSongViewHolder(
                ItemQueueSongBinding.inflate(LayoutInflater.from(parent.context))
            )
            else -> error("Someone messed with the ViewHolder item types.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = data[position]) {
            is Song -> (holder as QueueSongViewHolder).bind(item)
            is Header -> (holder as HeaderViewHolder).bind(item)

            else -> {
                Log.e(this::class.simpleName, "Bad data fed to QueueAdapter.")
            }
        }
    }

    fun submitList(newData: MutableList<BaseModel>) {
        if (data != newData) {
            data = newData

            listDiffer.submitList(newData)
        }
    }

    fun moveItems(adapterFrom: Int, adapterTo: Int) {
        val item = data.removeAt(adapterFrom)
        data.add(adapterTo, item)

        notifyItemMoved(adapterFrom, adapterTo)
    }

    fun removeItem(adapterIndex: Int) {
        data.removeAt(adapterIndex)

        // Check for two things:
        // If the data from the next queue is now entirely empty [Signified by a header at the end]
        // Or if the data from the last queue is now entirely empty [Signified by there being 2 headers with no items in between]
        if (data[data.lastIndex] is Header) {
            val lastIndex = data.lastIndex

            data.removeAt(lastIndex)

            notifyItemRangeRemoved(lastIndex, 2)
        } else if (data.lastIndex >= 1 && data[0] is Header && data[1] is Header) {
            data.removeAt(0)

            notifyItemRangeRemoved(0, 2)
        } else {
            notifyItemRemoved(adapterIndex)
        }
    }

    fun clearUserQueue() {
        val nextQueueHeaderIndex = data.indexOfLast { it is Header }
        val slice = data.slice(0 until nextQueueHeaderIndex)

        data.removeAll(slice)
        notifyItemRangeRemoved(0, slice.size)
    }

    // Generic ViewHolder for a queue item
    inner class QueueSongViewHolder(
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
