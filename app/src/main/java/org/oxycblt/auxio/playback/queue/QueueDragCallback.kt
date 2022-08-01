/*
 * Copyright (c) 2021 Auxio Project
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
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getDimenSafe
import org.oxycblt.auxio.util.logD

/**
 * A highly customized [ItemTouchHelper.Callback] that handles the queue system while basically
 * rebuilding most the "Material-y" aspects of an editable list because Google's implementations are
 * hot garbage. This shouldn't have *too many* UI bugs. I hope.
 * @author OxygenCobalt
 */
class QueueDragCallback(private val playbackModel: QueueViewModel) : ItemTouchHelper.Callback() {
    private var shouldLift = true

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val queueHolder = viewHolder as QueueSongViewHolder
        return if (queueHolder.isEnabled) {
            makeFlag(
                ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP or ItemTouchHelper.DOWN) or
                makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.START)
        } else {
            0
        }
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
        val holder = viewHolder as QueueSongViewHolder

        if (shouldLift && isCurrentlyActive && actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            logD("Lifting queue item")

            val bg = holder.backgroundDrawable
            val elevation = recyclerView.context.getDimenSafe(R.dimen.elevation_normal)
            holder.itemView
                .animate()
                .translationZ(elevation)
                .setDuration(100)
                .setUpdateListener {
                    bg.alpha = ((holder.itemView.translationZ / elevation) * 255).toInt()
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
        // another weird pixel desynchronization issue that is less visible but still incredibly
        // annoying.
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            holder.backgroundView.isInvisible = dX == 0f
        }

        holder.bodyView.translationX = dX
        holder.itemView.translationY = dY
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        // When an elevated item is cleared, we reset the elevation using another animation.
        val holder = viewHolder as QueueSongViewHolder

        if (holder.itemView.translationZ != 0f) {
            logD("Dropping queue item")

            val bg = holder.backgroundDrawable
            val elevation = recyclerView.context.getDimenSafe(R.dimen.elevation_normal)
            holder.itemView
                .animate()
                .translationZ(0f)
                .setDuration(100)
                .setUpdateListener {
                    bg.alpha = ((holder.itemView.translationZ / elevation) * 255).toInt()
                }
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
    ): Boolean =
        playbackModel.moveQueueDataItems(
            viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        playbackModel.removeQueueDataItem(viewHolder.bindingAdapterPosition)
    }

    override fun isLongPressDragEnabled() = false
}
