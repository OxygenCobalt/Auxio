/*
 * Copyright (c) 2021 Auxio Project
 * QueueDragCallback.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.oxycblt.auxio.playback.queue

import android.graphics.Canvas
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import org.oxycblt.auxio.R
import org.oxycblt.auxio.playback.PlaybackViewModel
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

/**
 * The Drag callback used by the queue recyclerview. Delivers updates to [PlaybackViewModel]
 * and [QueueAdapter] simultaneously.
 * @author OxygenCobalt
 */
class QueueDragCallback(
    private val playbackModel: PlaybackViewModel,
    private val coordinator: CoordinatorLayout,
    private val appBar: AppBarLayout
) : ItemTouchHelper.Callback() {
    private lateinit var queueAdapter: QueueAdapter
    private val tConsumed = IntArray(2)
    private var shouldLift = true

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        // Only allow dragging/swiping with the queue item ViewHolder, not the headers.
        return if (viewHolder is QueueAdapter.QueueSongViewHolder) {
            makeFlag(
                ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP or ItemTouchHelper.DOWN
            ) or makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.START)
        } else 0
    }

    override fun interpolateOutOfBoundsScroll(
        recyclerView: RecyclerView,
        viewSize: Int,
        viewSizeOutOfBounds: Int,
        totalSize: Int,
        msSinceStartScroll: Long
    ): Int {
        // Fix to make QueueFragment scroll slower when an item is scrolled out of bounds.
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

        val result = clampedAbsVelocity * sign(viewSizeOutOfBounds.toDouble()).toInt()

        recyclerView.post {
            // CoordinatorLayout refuses to propagate a scroll event initiated by an item scroll,
            // so we do it ourselves.
            (appBar.layoutParams as CoordinatorLayout.LayoutParams).behavior
                ?.onNestedPreScroll(
                    coordinator, appBar, recyclerView,
                    0, result, tConsumed, 0
                )
        }

        return result
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        // The material design page on elevation has a cool example of draggable items elevating
        // themselves when being dragged. Too bad google's implementation of this doesn't even
        // work :^). To emulate it on my own, I check if this child is in a drag state and
        // then animate an elevation change. This animation also changes the background so that
        // the item will actually draw over.
        // TODO: Maybe restrict the item from being drawn over the recycler bounds?
        //  Seems like its possible with enough UI magic

        val view = viewHolder.itemView

        if (shouldLift && isCurrentlyActive && actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            view.animate()
                .withStartAction { view.setBackgroundResource(R.color.surface) }
                .translationZ(view.resources.getDimension(R.dimen.elevation_small))
                .setDuration(100)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()

            shouldLift = false
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        // When an elevated item is cleared, we reset the elevation using another animation
        // and set the background to null again so a seam doesn't show up in further actions.

        val view = viewHolder.itemView

        if (view.translationZ != 0.0f) {
            view.animate()
                .withEndAction { view.setBackgroundResource(android.R.color.transparent) }
                .translationZ(0.0f)
                .setDuration(100)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }

        shouldLift = true

        super.clearView(recyclerView, viewHolder)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return playbackModel.moveQueueDataItems(
            viewHolder.bindingAdapterPosition,
            target.bindingAdapterPosition,
            queueAdapter
        )
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        playbackModel.removeQueueDataItem(viewHolder.bindingAdapterPosition, queueAdapter)
    }

    /**
     * Add the queue adapter to this callback.
     * Done because there's a circular dependency between the two objects
     */
    fun addQueueAdapter(adapter: QueueAdapter) {
        queueAdapter = adapter
    }

    companion object {
        const val MINIMUM_INITIAL_DRAG_VELOCITY = 10
        const val MAXIMUM_INITIAL_DRAG_VELOCITY = 25
    }
}
