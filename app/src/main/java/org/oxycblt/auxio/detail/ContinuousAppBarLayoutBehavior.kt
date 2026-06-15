/*
 * Copyright (c) 2022 Auxio Project
 * ContinuousAppBarLayoutBehavior.kt is part of Auxio.
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
 
package org.oxycblt.auxio.detail

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout

class ContinuousAppBarLayoutBehavior
@JvmOverloads
constructor(context: Context? = null, attrs: AttributeSet? = null) :
    AppBarLayout.Behavior(context, attrs) {
    private var recycler: RecyclerView? = null
    private var pointerId = -1
    private var velocityTracker: VelocityTracker? = null

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: AppBarLayout,
        ev: MotionEvent
    ): Boolean {
        val consumed = super.onInterceptTouchEvent(parent, child, ev)
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                ensureVelocityTracker()
                findRecyclerView(child).stopScroll()
                pointerId = ev.getPointerId(0)
            }
            MotionEvent.ACTION_CANCEL -> {
                velocityTracker?.recycle()
                velocityTracker = null
                pointerId = -1
            }
            else -> {}
        }
        return consumed
    }

    override fun onTouchEvent(
        parent: CoordinatorLayout,
        child: AppBarLayout,
        ev: MotionEvent
    ): Boolean {
        val consumed = super.onTouchEvent(parent, child, ev)
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                ensureVelocityTracker()
                pointerId = ev.getPointerId(0)
            }
            MotionEvent.ACTION_UP -> {
                findRecyclerView(child).fling(0, getYVelocity(ev))
            }
            MotionEvent.ACTION_CANCEL -> {
                velocityTracker?.recycle()
                velocityTracker = null
                pointerId = -1
            }
            else -> {}
        }
        velocityTracker?.addMovement(ev)
        return consumed
    }

    private fun ensureVelocityTracker() {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
    }

    private fun getYVelocity(event: MotionEvent): Int {
        velocityTracker?.let {
            it.addMovement(event)
            it.computeCurrentVelocity(FLING_UNITS)
            return -it.getYVelocity(pointerId).toInt()
        }
        return 0
    }

    private fun findRecyclerView(child: AppBarLayout): RecyclerView {
        val recycler = recycler
        if (recycler != null) {
            return recycler
        }

        // Use the scrolling view in order to find a RecyclerView to use.
        val newRecycler =
            (child.parent as ViewGroup).findViewById<RecyclerView>(child.liftOnScrollTargetViewId)
        this.recycler = newRecycler
        return newRecycler
    }

    companion object {
        private const val FLING_UNITS = 1000 // copied from base class
    }
}
