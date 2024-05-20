/*
 * Copyright (c) 2021 Auxio Project
 * MaterialDragCallback.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.recycler

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R as MR
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import org.oxycblt.auxio.R
import org.oxycblt.auxio.list.recycler.MaterialDragCallback.ViewHolder
import org.oxycblt.auxio.util.getDimen
import org.oxycblt.auxio.util.getInteger
import org.oxycblt.auxio.util.logD

/**
 * A highly customized [ItemTouchHelper.Callback] that enables some extra eye candy in editable UIs,
 * such as an animation when lifting items. Note that this requires a [ViewHolder] implementation in
 * order to function.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class MaterialDragCallback : ItemTouchHelper.Callback() {
    private var shouldLift = true

    final override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) =
        if (viewHolder is ViewHolder && viewHolder.enabled) {
            makeFlag(
                ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP or ItemTouchHelper.DOWN) or
                makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.START)
        } else {
            0
        }

    override fun interpolateOutOfBoundsScroll(
        recyclerView: RecyclerView,
        viewSize: Int,
        viewSizeOutOfBounds: Int,
        totalSize: Int,
        msSinceStartScroll: Long
    ): Int {
        // Clamp the scroll speed to prevent the lists from freaking out
        // Adapted from NewPipe: https://github.com/TeamNewPipe/NewPipe
        val standardSpeed =
            super.interpolateOutOfBoundsScroll(
                recyclerView, viewSize, viewSizeOutOfBounds, totalSize, msSinceStartScroll)

        val clampedAbsVelocity =
            max(
                MINIMUM_INITIAL_DRAG_VELOCITY,
                min(abs(standardSpeed), MAXIMUM_INITIAL_DRAG_VELOCITY))

        return clampedAbsVelocity * sign(viewSizeOutOfBounds.toDouble()).toInt()
    }

    final override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val holder = viewHolder as ViewHolder

        // Hook drag events to "lifting" the item (i.e raising it's elevation). Make sure
        // this is only done once when the item is initially picked up.
        // TODO: I think this is possible to improve with a raw ValueAnimator.
        if (shouldLift && isCurrentlyActive && actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            logD("Lifting ViewHolder")

            val bg = holder.background
            val elevation = recyclerView.context.getDimen(MR.dimen.m3_sys_elevation_level4)
            holder.root
                .animate()
                .translationZ(elevation)
                .setDuration(
                    recyclerView.context.getInteger(R.integer.anim_fade_exit_duration).toLong())
                .setUpdateListener {
                    bg.alpha = ((holder.root.translationZ / elevation) * 255).toInt()
                }
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()

            shouldLift = false
        }

        // We show a background with a delete icon behind the item each time one is swiped
        // away. To avoid working with canvas, this is simply placed behind the body.
        // That comes with a couple of problems, however. For one, the background view will always
        // lag behind the body view, resulting in a noticeable pixel offset when dragging. To fix
        // this, we make this a separate view and make this view invisible whenever the item is
        // not being swiped. This issue is also the reason why the background is not merged with
        // the FrameLayout within the item.
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            holder.delete.isInvisible = dX == 0f
        }

        // Update other translations. We do not call the default implementation, so we must do
        // this ourselves.
        holder.body.translationX = dX
        holder.root.translationY = dY
    }

    final override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        // When an elevated item is cleared, we reset the elevation using another animation.
        val holder = viewHolder as ViewHolder

        // This function can be called multiple times, so only start the animation when the view's
        // translationZ is already non-zero.
        if (holder.root.translationZ != 0f) {
            logD("Lifting ViewHolder")

            val bg = holder.background
            val elevation = recyclerView.context.getDimen(MR.dimen.m3_sys_elevation_level4)
            holder.root
                .animate()
                .translationZ(0f)
                .setDuration(
                    recyclerView.context.getInteger(R.integer.anim_fade_exit_duration).toLong())
                .setUpdateListener {
                    bg.alpha = ((holder.root.translationZ / elevation) * 255).toInt()
                }
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
        }

        shouldLift = true

        // Reset translations. We do not call the default implementation, so we must do
        // this ourselves.
        holder.body.translationX = 0f
        holder.root.translationY = 0f
    }

    // Long-press events are too buggy, only allow dragging with the handle.
    final override fun isLongPressDragEnabled() = false

    /** Required [RecyclerView.ViewHolder] implementation that exposes required fields */
    interface ViewHolder {
        /** Whether this [ViewHolder] can be moved right now. */
        val enabled: Boolean
        /** The root view containing the delete scrim and information. */
        val root: View
        /** The body view containing music information. */
        val body: View
        /** The scrim view showing the delete icon. Should be behind [body]. */
        val delete: View
        /** The drawable of the [body] background that can be elevated. */
        val background: Drawable
    }

    companion object {
        const val MINIMUM_INITIAL_DRAG_VELOCITY = 10
        const val MAXIMUM_INITIAL_DRAG_VELOCITY = 25
    }
}
