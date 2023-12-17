/*
 * Copyright (c) 2023 Auxio Project
 * SwipeCoverView.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.GestureDetector.OnGestureListener
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.annotation.AttrRes
import kotlin.math.abs
import org.oxycblt.auxio.image.CoverView
import org.oxycblt.auxio.util.isRtl

class SwipeCoverView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    CoverView(context, attrs, defStyleAttr), OnGestureListener {

    private val gestureDetector = GestureDetector(context, this)
    private val viewConfig = ViewConfiguration.get(context)

    var onSwipeListener: OnSwipeListener? = null

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        return gestureDetector.onGenericMotionEvent(event) || super.onGenericMotionEvent(event)
    }

    override fun onDown(e: MotionEvent): Boolean = true

    override fun onShowPress(e: MotionEvent) = Unit

    override fun onSingleTapUp(e: MotionEvent): Boolean = false

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean = false

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        e1 ?: return false
        val diffY = e2.y - e1.y
        val diffX = e2.x - e1.x
        if (abs(diffX) > abs(diffY) &&
            abs(diffX) > viewConfig.scaledTouchSlop &&
            abs(velocityX) > viewConfig.scaledMinimumFlingVelocity) {
            if (diffX > 0) {
                onSwipeRight()
            } else {
                onSwipeLeft()
            }
            return true
        }
        return false
    }

    override fun onLongPress(e: MotionEvent) = Unit

    private fun onSwipeRight() {
        onSwipeListener?.run { if (isRtl) onSwipeNext() else onSwipePrevious() }
    }

    private fun onSwipeLeft() {
        onSwipeListener?.run { if (isRtl) onSwipePrevious() else onSwipeNext() }
    }

    interface OnSwipeListener {

        fun onSwipePrevious()

        fun onSwipeNext()
    }
}
