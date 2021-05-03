package org.oxycblt.auxio.playback.queue

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.widget.TooltipCompat
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
import org.oxycblt.auxio.recycler.viewholders.BaseViewHolder
import org.oxycblt.auxio.recycler.viewholders.HeaderViewHolder
import org.oxycblt.auxio.ui.inflater

/**
 * The single adapter for both the Next Queue and the User Queue.
 * @param touchHelper The [ItemTouchHelper] ***containing*** [QueueDragCallback] to be used
 * @param playbackModel The [PlaybackViewModel] for updates to be dispatched to
 * @author OxygenCobalt
 */
class QueueAdapter(
    private val touchHelper: ItemTouchHelper,
    private val playbackModel: PlaybackViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var data = mutableListOf<BaseModel>()
    private var listDiffer = AsyncListDiffer(this, DiffCallback())

    override fun getItemCount(): Int = data.size

    override fun getItemViewType(position: Int): Int {
        return when (val item = data[position]) {
            is Header -> if (item.isAction) {
                USER_QUEUE_HEADER_ITEM_TYPE
            } else {
                HeaderViewHolder.ITEM_TYPE
            }

            is Song -> QUEUE_SONG_ITEM_TYPE

            else -> -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HeaderViewHolder.ITEM_TYPE -> HeaderViewHolder.from(parent.context)

            USER_QUEUE_HEADER_ITEM_TYPE -> UserQueueHeaderViewHolder(
                ItemActionHeaderBinding.inflate(parent.context.inflater)
            )

            QUEUE_SONG_ITEM_TYPE -> QueueSongViewHolder(
                ItemQueueSongBinding.inflate(parent.context.inflater)
            )

            else -> error("Invalid viewholder item type $viewType.")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = data[position]) {
            is Header -> if (item.isAction) {
                (holder as UserQueueHeaderViewHolder).bind(item)
            } else {
                (holder as HeaderViewHolder).bind(item)
            }

            is Song -> (holder as QueueSongViewHolder).bind(item)

            else -> logE("Bad data given to QueueAdapter.")
        }
    }

    /**
     * Submit data using [AsyncListDiffer].
     * **Only use this if you have no idea what changes occurred to the data**
     */
    fun submitList(newData: MutableList<BaseModel>) {
        if (data != newData) {
            data = newData

            listDiffer.submitList(newData)
        }
    }

    /**
     * Move Items.
     * Used since [submitList] will cause QueueAdapter to freak out.
     */
    fun moveItems(adapterFrom: Int, adapterTo: Int) {
        val item = data.removeAt(adapterFrom)
        data.add(adapterTo, item)

        notifyItemMoved(adapterFrom, adapterTo)
    }

    /**
     * Remove an item.
     * Used since [submitList] will cause QueueAdapter to freak out.
     */
    fun removeItem(adapterIndex: Int) {
        data.removeAt(adapterIndex)

        /*
         * If the data from the next queue is now entirely empty [Signified by a header at the
         * end, remove the next queue header as notify as such.
         *
         * If the user queue is empty [Signified by there being two headers at the beginning with
         * nothing in between], then remove the user queue header and notify as such.
         *
         * Otherwise just remove the item as usual.
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
    ) : BaseViewHolder<Song>(binding) {

        @SuppressLint("ClickableViewAccessibility")
        override fun onBind(data: Song) {
            binding.song = data

            binding.songName.requestLayout()
            binding.songInfo.requestLayout()

            binding.songDragHandle.setOnTouchListener { _, motionEvent ->
                binding.songDragHandle.performClick()

                if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                    touchHelper.startDrag(this)
                    true
                } else false
            }
        }
    }

    /*
     * The viewholder for
     */
    inner class UserQueueHeaderViewHolder(
        private val binding: ItemActionHeaderBinding
    ) : BaseViewHolder<Header>(binding) {

        override fun onBind(data: Header) {
            binding.header = data

            binding.headerButton.apply {
                setImageResource(R.drawable.ic_clear)

                contentDescription = context.getString(R.string.description_clear_user_queue)
                TooltipCompat.setTooltipText(this, contentDescription)

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
