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
import android.util.AttributeSet
import android.view.View
import android.view.WindowInsets
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.NeoBottomSheetBehavior
import kotlin.math.abs
import org.oxycblt.auxio.util.replaceSystemBarInsetsCompat
import org.oxycblt.auxio.util.systemBarInsetsCompat

class BottomSheetContentViewBehavior<V : View>(context: Context, attributeSet: AttributeSet?) :
    CoordinatorLayout.Behavior<V>(context, attributeSet) {
    private var lastInsets: WindowInsets? = null
    private var dep: View? = null
    private var setup: Boolean = false

    override fun onMeasureChild(
        parent: CoordinatorLayout,
        child: V,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ): Boolean {
        return measureContent(parent, child, dep ?: return false)
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        super.onLayoutChild(parent, child, layoutDirection)
        child.layout(0, 0, child.measuredWidth, child.measuredHeight)

        if (!setup) {
            child.setOnApplyWindowInsetsListener { _, insets ->
                lastInsets = insets

                val dep = dep ?: return@setOnApplyWindowInsetsListener insets

                val bars = insets.systemBarInsetsCompat
                val behavior =
                    (dep.layoutParams as CoordinatorLayout.LayoutParams).behavior
                        as NeoBottomSheetBehavior

                val offset = behavior.calculateSlideOffset()
                if (behavior.peekHeight < 0 || offset == Float.MIN_VALUE) {
                    return@setOnApplyWindowInsetsListener insets
                }

                val adjustedBottomInset =
                    (bars.bottom - behavior.calculateConsumedByBar()).coerceAtLeast(0)

                insets.replaceSystemBarInsetsCompat(
                    bars.left, bars.top, bars.right, adjustedBottomInset)
            }

            setup = true
        }

        return true
    }

    private fun measureContent(parent: View, child: View, dep: View): Boolean {
        val behavior =
            (dep.layoutParams as CoordinatorLayout.LayoutParams).behavior as NeoBottomSheetBehavior

        val offset = behavior.calculateSlideOffset()
        if (behavior.peekHeight < 0 || offset == Float.MIN_VALUE) {
            return false
        }

        val contentWidthSpec =
            View.MeasureSpec.makeMeasureSpec(parent.measuredWidth, View.MeasureSpec.EXACTLY)
        val contentHeightSpec =
            View.MeasureSpec.makeMeasureSpec(
                parent.measuredHeight - behavior.calculateConsumedByBar(), View.MeasureSpec.EXACTLY)

        child.measure(contentWidthSpec, contentHeightSpec)

        return true
    }

    private fun NeoBottomSheetBehavior<*>.calculateConsumedByBar(): Int {
        val offset = calculateSlideOffset()
        return if (offset >= 0) {
            peekHeight
        } else {
            (peekHeight * (1 - abs(offset))).toInt()
        }
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        if ((dependency.layoutParams as CoordinatorLayout.LayoutParams).behavior
            is NeoBottomSheetBehavior) {
            dep = dependency
            return true
        }

        return false
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: V,
        dependency: View
    ): Boolean {
        lastInsets?.let(child::dispatchApplyWindowInsets)
        return measureContent(parent, child, dependency) &&
            onLayoutChild(parent, child, parent.layoutDirection)
    }
}
