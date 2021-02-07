package org.oxycblt.auxio.playback.queue

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.ItemActionHeaderBinding
import org.oxycblt.auxio.databinding.ItemQueueSongBinding
import org.oxycblt.auxio.logE
import org.oxycblt.auxio.music.BaseModel
import org.oxycblt.auxio.music.Header
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.recycler.DiffCallback
import org.oxycblt.auxio.recycler.viewholders.BaseHolder
import org.oxycblt.auxio.recycler.viewholders.HeaderViewHolder

/**
 * The single adapter for both the Next Queue and the User Queue.
 * @param touchHelper The [ItemTouchHelper] ***containing*** [QueueDragCallback] to be used
 * @param playbackModel The [PlaybackViewModel] to dispatch updates to.
 * @author OxygenCobalt
 */
class QueueAdapter(
    private val touchHelper: ItemTouchHelper,
    private val playbackModel: PlaybackViewModel,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var data = mutableListOf<BaseModel>()
    private var listDiffer = AsyncListDiffer(this, DiffCallback())

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        val item = data[position]

        return if (item is Header)
            if (item.isAction)
                USER_QUEUE_HEADER_ITEM_TYPE
            else
                HeaderViewHolder.ITEM_TYPE
        else
            QUEUE_SONG_ITEM_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HeaderViewHolder.ITEM_TYPE -> HeaderViewHolder.from(parent.context)

            USER_QUEUE_HEADER_ITEM_TYPE -> UserQueueHeaderViewHolder(
                parent.context, ItemActionHeaderBinding.inflate(LayoutInflater.from(parent.context))
            )

            QUEUE_SONG_ITEM_TYPE -> QueueSongViewHolder(
                ItemQueueSongBinding.inflate(LayoutInflater.from(parent.context))
            )

            else -> error("Someone messed with the ViewHolder item types.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = data[position]) {
            is Song -> (holder as QueueSongViewHolder).bind(item)
            is Header ->
                if (item.isAction) {
                    (holder as UserQueueHeaderViewHolder).bind(item)
                } else {
                    (holder as HeaderViewHolder).bind(item)
                }

            else -> {
                logE("Bad data fed to QueueAdapter.")
            }
        }
    }

    /**
     * Submit data using [AsyncListDiffer]. **Only use this if you have no idea what changes occurred to the data**
     */
    fun submitList(newData: MutableList<BaseModel>) {
        if (data != newData) {
            data = newData

            listDiffer.submitList(newData)
        }
    }

    /**
     * Move Items. Used since [submitList] will cause QueueAdapter to freak-out here.
     */
    fun moveItems(adapterFrom: Int, adapterTo: Int) {
        val item = data.removeAt(adapterFrom)
        data.add(adapterTo, item)

        notifyItemMoved(adapterFrom, adapterTo)
    }

    /**
     * Remove an item. Used since [submitList] will cause QueueAdapter to freak-out here.
     */
    fun removeItem(adapterIndex: Int) {
        data.removeAt(adapterIndex)

        /*
         * Check for two things:
         * If the data from the next queue is now entirely empty [Signified by a header at the end]
         * Or if the data from the last queue is now entirely empty [Signified by there being
         * 2 headers with no items in between]
         * If so, remove the header and the removed item in a range. Otherwise just remove the item.
         */
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

    /**
     * Generic ViewHolder for a queue song
     */
    inner class QueueSongViewHolder(
        private val binding: ItemQueueSongBinding,
    ) : BaseHolder<Song>(binding) {

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

    /**
     * ViewHolder for the **user queue header**. Has the clear queue button.
     */
    inner class UserQueueHeaderViewHolder(
        context: Context, private val binding: ItemActionHeaderBinding
    ) : BaseHolder<Header>(binding) {

        init {
            binding.headerButton.contentDescription = context.getString(
                R.string.description_clear_user_queue
            )
        }

        override fun onBind(data: Header) {
            binding.header = data
            binding.headerButton.apply {
                setImageResource(R.drawable.ic_clear)

                setOnClickListener {
                    playbackModel.clearUserQueue()
                }
            }
        }
    }

    companion object {
        const val QUEUE_SONG_ITEM_TYPE = 0xA005
        const val USER_QUEUE_HEADER_ITEM_TYPE = 0xA006
    }
}
