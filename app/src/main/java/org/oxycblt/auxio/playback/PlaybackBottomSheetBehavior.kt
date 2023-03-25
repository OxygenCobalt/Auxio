/*
 * Copyright (c) 2022 Auxio Project
 * PlaybackBottomSheetBehavior.kt is part of Auxio.
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
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.shape.MaterialShapeDrawable
import org.oxycblt.auxio.R
import org.oxycblt.auxio.ui.BaseBottomSheetBehavior
import org.oxycblt.auxio.util.getAttrColorCompat
import org.oxycblt.auxio.util.getDimen

/**
 * The [BaseBottomSheetBehavior] for the playback bottom sheet. This bottom sheet
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class PlaybackBottomSheetBehavior<V : View>(context: Context, attributeSet: AttributeSet?) :
    BaseBottomSheetBehavior<V>(context, attributeSet) {
    val sheetBackgroundDrawable =
        MaterialShapeDrawable.createWithElevationOverlay(context).apply {
            fillColor = context.getAttrColorCompat(R.attr.colorSurface)
            elevation = context.getDimen(R.dimen.elevation_normal)
        }

    init {
        isHideable = true
    }

    // Hack around issue where the playback sheet will try to intercept nested scrolling events
    // before the queue sheet.
    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent) =
        super.onInterceptTouchEvent(parent, child, event) && state != STATE_EXPANDED

    // Note: This is an extension to Auxio's vendored BottomSheetBehavior
    override fun isHideableWhenDragging() = false

    override fun createBackground(context: Context) =
        LayerDrawable(
            arrayOf(
                // Add another colored background so that there is always an obscuring
                // element even as the actual "background" element is faded out.
                MaterialShapeDrawable(sheetBackgroundDrawable.shapeAppearanceModel).apply {
                    fillColor = sheetBackgroundDrawable.fillColor
                },
                sheetBackgroundDrawable))
}
