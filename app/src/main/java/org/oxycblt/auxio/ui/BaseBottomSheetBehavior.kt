/*
 * Copyright (c) 2022 Auxio Project
 * BaseBottomSheetBehavior.kt is part of Auxio.
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
 
package org.oxycblt.auxio.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.R as MR
import com.google.android.material.bottomsheet.BackportBottomSheetBehavior
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getDimen
import org.oxycblt.auxio.util.getDimenPixels
import org.oxycblt.auxio.util.systemGestureInsetsCompat
import timber.log.Timber as L

/**
 * A BottomSheetBehavior that resolves several issues with the default implementation, including:
 * 1. No reasonable edge-to-edge support.
 * 2. Strange corner radius behaviors.
 * 3. Inability to skip half-expanded state when full-screen.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class BaseBottomSheetBehavior<V : View>(context: Context, attributeSet: AttributeSet?) :
    BackportBottomSheetBehavior<V>(context, attributeSet) {
    private var initalized = false
    private val idealBottomGestureInsets = context.getDimenPixels(R.dimen.spacing_medium)

    // I can't manually inject this, MainFragment must be the one to do it.
    // TODO: Just use another library. Tired of Hilt.
    lateinit var uiSettings: UISettings

    init {
        // Disable isFitToContents to make the bottom sheet expand to the top of the screen and
        // not just how much the content takes up.
        isFitToContents = false
    }

    /**
     * Create a background [Drawable] to use for this [BaseBottomSheetBehavior]'s child [View].
     *
     * @param context [Context] that can be used to draw the [Drawable].
     * @return A background drawable.
     */
    abstract fun createBackground(context: Context, uiSettings: UISettings): Drawable

    /** Get the ideal bar height to use before the bar is properly measured. */
    abstract fun getIdealBarHeight(context: Context): Int

    /**
     * Called when window insets are being applied to the [View] this [BaseBottomSheetBehavior] is
     * linked to.
     *
     * @param child The child view receiving the [WindowInsets].
     * @param insets The [WindowInsets] to apply.
     * @return The (possibly modified) [WindowInsets].
     * @see View.onApplyWindowInsets
     */
    open fun applyWindowInsets(child: View, insets: WindowInsets): WindowInsets {
        // All sheet behaviors derive their peek height from the size of the "bar" (i.e the
        // first child) plus the gesture insets.
        val gestures = insets.systemGestureInsetsCompat
        val bar = (child as ViewGroup).getChildAt(0)
        peekHeight =
            if (bar.measuredHeight > 0) {
                bar.measuredHeight + gestures.bottom
            } else {
                getIdealBarHeight(child.context) + gestures.bottom
            }
        return insets
    }

    // Enable experimental settings that allow us to skip the half-expanded state.
    override fun shouldSkipHalfExpandedStateWhenDragging() = true

    override fun shouldExpandOnUpwardDrag(dragDurationMillis: Long, yPositionPercentage: Float) =
        true

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val layout = super.onLayoutChild(parent, child, layoutDirection)
        // Don't repeat redundant initialization.
        if (!initalized) {
            L.d("Not initialized, setting up child")
            child.apply {
                // Set up compat elevation attributes. These are only shown below API 28.
                translationZ = context.getDimen(MR.dimen.m3_sys_elevation_level1)
                // Background differs depending on concrete implementation.
                background = createBackground(context, uiSettings)
                setOnApplyWindowInsetsListener(::applyWindowInsets)
            }
            initalized = true
            peekHeight = getIdealBarHeight(child.context) + idealBottomGestureInsets
        }
        // Sometimes CoordinatorLayout doesn't dispatch window insets to us, likely due to how
        // much we overload it. Ensure that we get them.
        child.requestApplyInsets()
        return layout
    }
}
