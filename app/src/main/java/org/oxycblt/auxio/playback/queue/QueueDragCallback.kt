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
import org.oxycblt.auxio.util.getDimen
import org.oxycblt.auxio.util.getInteger
import org.oxycblt.auxio.util.logD

/**
 * A highly customized [ItemTouchHelper.Callback] that enables some extra eye candy in the queue UI,
 * such as an animation when lifting items.
 * @author Alexander Capehart (OxygenCobalt)
 */
class QueueDragCallback(private val playbackModel: QueueViewModel) : ItemTouchHelper.Callback() {
    private var shouldLift = true

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val queueHolder = viewHolder as QueueSongViewHolder
        return if (queueHolder.isFuture) {
            makeFlag(
                ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP or ItemTouchHelper.DOWN) or
                makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.START)
        } else {
            // Avoid allowing any touch actions for already-played queue items, as the playback
            // system does not currently allow for this.
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
        val holder = viewHolder as QueueSongViewHolder

        // Hook drag events to "lifting" the queue item (i.e raising it's elevation). Make sure
        // this is only done once when the item is initially picked up.
        // TODO: I think this is possible to improve with a raw ValueAnimator.
        if (shouldLift && isCurrentlyActive && actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            logD("Lifting queue item")

            val bg = holder.backgroundDrawable
            val elevation = recyclerView.context.getDimen(R.dimen.elevation_normal)
            holder.itemView
                .animate()
                .translationZ(elevation)
                .setDuration(
                    recyclerView.context.getInteger(R.integer.anim_fade_exit_duration).toLong())
                .setUpdateListener {
                    bg.alpha = ((holder.itemView.translationZ / elevation) * 255).toInt()
                }
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()

            shouldLift = false
        }

        // We show a background with a delete icon behind the queue song each time one is swiped
        // away. To avoid working with canvas, this is simply placed behind the queue body.
        // That comes with a couple of problems, however. For one, the background view will always
        // lag behind the body view, resulting in a noticeable pixel offset when dragging. To fix
        // this, we make this a separate view and make this view invisible whenever the item is
        // not being swiped. This issue is also the reason why the background is not merged with
        // the FrameLayout within the queue item.
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            holder.backgroundView.isInvisible = dX == 0f
        }

        // Update other translations. We do not call the default implementation, so we must do
        // this ourselves.
        holder.bodyView.translationX = dX
        holder.itemView.translationY = dY
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        // When an elevated item is cleared, we reset the elevation using another animation.
        val holder = viewHolder as QueueSongViewHolder

        // This function can be called multiple times, so only start the animation when the view's
        // translationZ is already non-zero.
        if (holder.itemView.translationZ != 0f) {
            logD("Dropping queue item")

            val bg = holder.backgroundDrawable
            val elevation = recyclerView.context.getDimen(R.dimen.elevation_normal)
            holder.itemView
                .animate()
                .translationZ(0f)
                .setDuration(
                    recyclerView.context.getInteger(R.integer.anim_fade_exit_duration).toLong())
                .setUpdateListener {
                    bg.alpha = ((holder.itemView.translationZ / elevation) * 255).toInt()
                }
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }

        shouldLift = true

        // Reset translations. We do not call the default implementation, so we must do
        // this ourselves.
        holder.bodyView.translationX = 0f
        holder.itemView.translationY = 0f
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) =
        playbackModel.moveQueueDataItems(
            viewHolder.bindingAdapterPosition, target.bindingAdapterPosition)

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        playbackModel.removeQueueDataItem(viewHolder.bindingAdapterPosition)
    }

    // Long-press events are too buggy, only allow dragging with the handle.
    override fun isLongPressDragEnabled() = false
}
