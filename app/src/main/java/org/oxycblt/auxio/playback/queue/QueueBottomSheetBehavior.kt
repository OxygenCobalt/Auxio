/*
 * Copyright (c) 2022 Auxio Project
 * QueueBottomSheetBehavior.kt is part of Auxio.
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
import android.view.WindowInsets
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.R as MR
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.BaseBottomSheetBehavior
import org.oxycblt.auxio.ui.UISettings
import org.oxycblt.auxio.util.getAttrColorCompat
import org.oxycblt.auxio.util.getDimenPixels
import org.oxycblt.auxio.util.replaceSystemBarInsetsCompat
import org.oxycblt.auxio.util.systemBarInsetsCompat

/**
 * The [BaseBottomSheetBehavior] for the queue bottom sheet. This is placed within the playback
 * sheet and automatically arranges itself to show the playback bar at the top.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class QueueBottomSheetBehavior<V : View>(context: Context, attributeSet: AttributeSet?) :
    BaseBottomSheetBehavior<V>(context, attributeSet) {
    private var barHeight = 0
    private var barSpacing = context.getDimenPixels(R.dimen.spacing_small)

    init {
        // Not hide-able (and not programmatically hide-able)
        isHideable = false
    }

    override fun getIdealBarHeight(context: Context) =
        context.getDimenPixels(R.dimen.size_touchable_large)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View) =
        dependency.id == R.id.playback_bar_fragment

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: V,
        dependency: View
    ): Boolean {
        barHeight = dependency.height
        // No change, just grabbed the height
        return false
    }

    override fun createBackground(context: Context, uiSettings: UISettings) =
        MaterialShapeDrawable.createWithElevationOverlay(context).apply {
            // The queue sheet's background is a static elevated background.
            fillColor = context.getAttrColorCompat(MR.attr.colorSurfaceContainerHigh)
            if (uiSettings.roundMode) {
                shapeAppearanceModel =
                    ShapeAppearanceModel.builder(
                            context,
                            R.style.ShapeAppearance_Auxio_BottomSheet,
                            MR.style.ShapeAppearanceOverlay_Material3_Corner_Top)
                        .build()
            }
        }

    override fun applyWindowInsets(child: View, insets: WindowInsets): WindowInsets {
        super.applyWindowInsets(child, insets)
        // Offset our expanded panel by the size of the playback bar, as that is shown when
        // we slide up the panel.
        val bars = insets.systemBarInsetsCompat
        expandedOffset = barHeight + barSpacing
        return insets.replaceSystemBarInsetsCompat(
            bars.left, bars.top, bars.right, expandedOffset + bars.bottom)
    }
}
