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
 
package org.oxycblt.auxio.ui

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.NeoBottomSheetBehavior
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.*

/**
 * Implements the fundamental bottom sheet attributes used across the entire app.
 * @author OxygenCobalt
 */
abstract class AuxioSheetBehavior<V : View>(context: Context, attributeSet: AttributeSet?) :
    NeoBottomSheetBehavior<V>(context, attributeSet) {
    private var setup = false
    val sheetBackgroundDrawable =
        MaterialShapeDrawable.createWithElevationOverlay(context).apply {
            fillColor = context.getAttrColorSafe(R.attr.colorSurface).stateList
            elevation = context.getDimenSafe(R.dimen.elevation_normal)
        }

    init {
        // We need to disable isFitToContents for us to have our bottom sheet expand to the
        // whole of the screen and not just whatever portion it takes up.
        isFitToContents = false
    }

    /** Called when the child the bottom sheet applies to receives window insets. */
    open fun applyWindowInsets(child: View, insets: WindowInsets): WindowInsets {
        // All sheet behaviors derive their peek height from the size of the "bar" (i.e the
        // first child) plus the gesture insets.
        val gestures = insets.systemGestureInsetsCompat
        peekHeight = (child as ViewGroup).getChildAt(0).height + gestures.bottom
        return insets
    }

    // Enable experimental settings to allow us to skip the half expanded state without
    // dumb hacks.
    override fun shouldSkipHalfExpandedStateWhenDragging() = true
    override fun shouldExpandOnUpwardDrag(dragDurationMillis: Long, yPositionPercentage: Float) =
        true

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val layout = super.onLayoutChild(parent, child, layoutDirection)

        if (!setup) {
            child.apply {
                // Sometimes the sheet background will fade out, so guard it with another
                // colorSurface drawable to prevent content overlap.
                background =
                    LayerDrawable(
                        arrayOf(
                            ColorDrawable(context.getAttrColorSafe(R.attr.colorSurface)),
                            sheetBackgroundDrawable))

                // Try to disable drop shadows if possible.
                disableDropShadowCompat()

                setOnApplyWindowInsetsListener(::applyWindowInsets)
            }

            setup = true
        }

        return layout
    }
}
