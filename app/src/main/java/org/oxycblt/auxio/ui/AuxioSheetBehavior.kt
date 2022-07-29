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
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.NeoBottomSheetBehavior
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.*

abstract class AuxioSheetBehavior<V : View>(context: Context, attributeSet: AttributeSet?) :
    NeoBottomSheetBehavior<V>(context, attributeSet) {
    private var elevationNormal = context.getDimenSafe(R.dimen.elevation_normal)
    val sheetBackgroundDrawable =
        MaterialShapeDrawable.createWithElevationOverlay(context).apply {
            fillColor = context.getAttrColorSafe(R.attr.colorSurface).stateList
            elevation = context.pxOfDp(elevationNormal).toFloat()
        }

    init {
        isFitToContents = false
    }

    override fun shouldSkipHalfExpandedStateWhenDragging() = true
    override fun shouldExpandOnUpwardDrag(dragDurationMillis: Long, yPositionPercentage: Float) =
        true

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val success = super.onLayoutChild(parent, child, layoutDirection)

        (child as ViewGroup).apply {
            background =
                LayerDrawable(
                    arrayOf(
                        ColorDrawable(context.getAttrColorSafe(R.attr.colorSurface)),
                        sheetBackgroundDrawable))

            disableDropShadowCompat()
        }

        return success
    }
}
