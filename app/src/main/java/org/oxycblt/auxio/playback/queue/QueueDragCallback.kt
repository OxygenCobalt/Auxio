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
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.playback.PlaybackViewModel
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

/**
 * A highly customized [ItemTouchHelper.Callback] that handles the queue system while basically
 * rebuilding most the "Material-y" aspects of an editable list because Google's implementations
 * are hot garbage. This shouldn't have *too many* UI bugs. I hope.
 * @author OxygenCobalt
 */
class QueueDragCallback(private val playbackModel: PlaybackViewModel) : ItemTouchHelper.Callback() {
    private lateinit var queueAdapter: QueueAdapter
    private var shouldLift = true

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int =
        makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP or ItemTouchHelper.DOWN) or
            makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.START)

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

        return clampedAbsVelocity * sign(viewSizeOutOfBounds.toDouble()).toInt()
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
        // work! To emulate it on my own, I check if this child is in a drag state and then animate
        // an elevation change.

        val holder = viewHolder as QueueAdapter.QueueSongViewHolder

        if (shouldLift && isCurrentlyActive && actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            val bg = holder.bodyView.background as MaterialShapeDrawable
            val elevation = recyclerView.resources.getDimension(R.dimen.elevation_small)

            holder.itemView.animate()
                .translationZ(elevation)
                .setDuration(100)
                .setUpdateListener {
                    bg.elevation = holder.itemView.translationZ
                }
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()

            shouldLift = false
        }

        // We show a background with a clear icon behind the queue song each time one is swiped
        // away. To avoid any canvas shenanigans, we just place a custom background view behind the
        // main "body" layout of the queue item and then translate that.
        //
        // That comes with a couple of problems, however. For one, the background view will always
        // lag behind the body view, resulting in a noticeable pixel offset when dragging. To fix
        // this, we make this a separate view and make this view invisible whenever the item is
        // not being swiped. We cannot merge this view with the FrameLayout, as that will cause
        // another weird pixel desync issue that is less visible but still incredibly annoying.
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            holder.backgroundView.isInvisible = dX == 0f
        }

        holder.bodyView.translationX = dX
        holder.itemView.translationY = dY
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        // When an elevated item is cleared, we reset the elevation using another animation.
        val holder = viewHolder as QueueAdapter.QueueSongViewHolder

        if (holder.itemView.translationZ != 0.0f) {
            val bg = holder.bodyView.background as MaterialShapeDrawable

            holder.itemView.animate()
                .translationZ(0.0f)
                .setDuration(100)
                .setUpdateListener { bg.elevation = holder.itemView.translationZ }
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }

        shouldLift = true

        holder.bodyView.translationX = 0f
        holder.itemView.translationY = 0f
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val from = viewHolder.bindingAdapterPosition
        val to = target.bindingAdapterPosition

        return playbackModel.moveQueueDataItems(from, to) {
            queueAdapter.moveItems(from, to)
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        playbackModel.removeQueueDataItem(viewHolder.bindingAdapterPosition) {
            queueAdapter.removeItem(viewHolder.bindingAdapterPosition)
        }
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
