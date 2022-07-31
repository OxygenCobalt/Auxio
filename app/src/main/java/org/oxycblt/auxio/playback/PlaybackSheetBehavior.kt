/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.playback

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.coordinatorlayout.widget.CoordinatorLayout
import kotlin.math.max
import org.oxycblt.auxio.ui.AuxioSheetBehavior
import org.oxycblt.auxio.util.systemBarInsetsCompat
import org.oxycblt.auxio.util.systemGestureInsetsCompat

/**
 * The coordinator layout behavior used for the playback sheet, hacking in the many fixes required
 * to make bottom sheets like this work.
 * @author OxygenCobalt
 */
class PlaybackSheetBehavior<V : View>(context: Context, attributeSet: AttributeSet?) :
    AuxioSheetBehavior<V>(context, attributeSet) {
    private var lastInsets: WindowInsets? = null

    init {
        isHideable = true
    }

    // Hack around issue where the playback sheet will try to intercept nested scrolling events
    // before the queue sheet.
    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: V,
        event: MotionEvent
    ): Boolean = super.onInterceptTouchEvent(parent, child, event) && state != STATE_EXPANDED

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val success = super.onLayoutChild(parent, child, layoutDirection)

        (child as ViewGroup).apply {
            setOnApplyWindowInsetsListener { _, insets ->
                lastInsets = insets
                val bars = insets.systemBarInsetsCompat
                val gestures = insets.systemGestureInsetsCompat
                peekHeight = getChildAt(0).measuredHeight + max(gestures.bottom, bars.bottom)
                insets
            }
        }

        return success
    }

    // Note: This is an extension to Auxio's vendored BottomSheetBehavior
    override fun enableHidingGestures() = false

    fun hideSafe() {
        if (state != STATE_HIDDEN) {
            isDraggable = false
            state = STATE_HIDDEN
        }
    }

    fun unhideSafe() {
        if (state == STATE_HIDDEN) {
            state = STATE_COLLAPSED
            isDraggable = true
        }
    }
}
