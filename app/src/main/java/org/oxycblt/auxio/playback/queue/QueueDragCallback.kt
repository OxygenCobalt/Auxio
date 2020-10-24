package org.oxycblt.auxio.playback.queue

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.playback.PlaybackViewModel
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

// The drag callback used for the Queue RecyclerView.
class QueueDragCallback(
    private val playbackModel: PlaybackViewModel
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
    ItemTouchHelper.START
) {
    override fun interpolateOutOfBoundsScroll(
        recyclerView: RecyclerView,
        viewSize: Int,
        viewSizeOutOfBounds: Int,
        totalSize: Int,
        msSinceStartScroll: Long
    ): Int {
        // Fix to make QueueFragment scroll when an item is scrolled out of bounds.
        // Adapted from NewPipe: https://github.com/TeamNewPipe/NewPipe

        val standardSpeed = super.interpolateOutOfBoundsScroll(
            recyclerView, viewSize, viewSizeOutOfBounds, totalSize, msSinceStartScroll
        )

        val clampedAbsVelocity = max(
            MINIMUM_INITIAL_DRAG_VELOCITY,
            min(
                abs(standardSpeed),
                MAXIMUM_INITIAL_DRAG_VELOCITY
            )
        )

        return clampedAbsVelocity * sign(viewSizeOutOfBounds.toDouble()).toInt()
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        playbackModel.moveQueueItems(viewHolder.adapterPosition, target.adapterPosition)

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        playbackModel.removeQueueItem(viewHolder.adapterPosition)
    }

    companion object {
        const val MINIMUM_INITIAL_DRAG_VELOCITY = 10
        const val MAXIMUM_INITIAL_DRAG_VELOCITY = 25
    }
}
