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
 
package org.oxycblt.auxio.playback.queue

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import kotlin.math.max
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.AuxioSheetBehavior
import org.oxycblt.auxio.util.*

class QueueSheetBehavior<V : View>(context: Context, attributeSet: AttributeSet?) :
    AuxioSheetBehavior<V>(context, attributeSet) {
    private var barHeight = 0
    private var barSpacing = context.getDimenSizeSafe(R.dimen.spacing_small)

    init {
        sheetBackgroundDrawable.setCornerSize(context.getDimenSafe(R.dimen.size_corners_medium))
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View) =
        dependency.id == R.id.playback_bar_fragment

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: V,
        dependency: View
    ): Boolean {
        val ok = super.onDependentViewChanged(parent, child, dependency)
        barHeight = dependency.height
        return ok
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val success = super.onLayoutChild(parent, child, layoutDirection)

        child.setOnApplyWindowInsetsListener { _, insets ->
            val bars = insets.systemBarInsetsCompat
            val gestures = insets.systemGestureInsetsCompat

            expandedOffset = bars.top + barHeight + barSpacing
            peekHeight =
                (child as ViewGroup).getChildAt(0).height + max(gestures.bottom, bars.bottom)
            insets.replaceSystemBarInsetsCompat(
                bars.left, bars.top, bars.right, expandedOffset + bars.bottom)
        }

        return success
    }
}
