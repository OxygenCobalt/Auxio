/*
 * Copyright (c) 2026 Auxio Project
 * ScaledPlaybackButtonGroup.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.ui.button

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.AttrRes
import androidx.core.view.children
import com.google.android.material.R
import com.google.android.material.button.MaterialButtonGroup
import kotlin.math.min

/**
 * A [com.google.android.material.button.MaterialButtonGroup] that scales playback controls down to
 * actually fit mid-sized screens.
 *
 * M3 Expressive for some reason only has "Large" and then massively smaller "Medium"/"Small" size
 * classes. So either you clip on smaller phones or jump to an unusable button row sizing.
 *
 * Fix this by just force-scaling down buttons.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class ScaledPlaybackButtonGroup
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = R.attr.materialButtonGroupStyle,
) : MaterialButtonGroup(context, attrs, defStyleAttr) {
    private val baseSpacing = spacing
    private var lastAppliedSpacing = baseSpacing

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var scale = 1f

        val spbChildren = children.filterIsInstance<ScaledPlaybackButton>().toList()
        if (spbChildren.isEmpty()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        // Must equally space/distribute children across the row equally
        val rowWidth = spbChildren.sumOf { it.baseWidth } + baseSpacing * (spbChildren.size - 1)
        // Must still fit to the highest child (when scaled)
        val rowHeight = spbChildren.maxOf { it.baseHeight }

        // Build a scaling coefficient that fits exactly within the bounds but still
        // scales down 1:1.
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize > 0 && rowWidth > widthSize) {
            scale = min(scale, widthSize.toFloat() / rowWidth)
        }

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize > 0 && rowHeight > heightSize) {
            scale = min(scale, heightSize.toFloat() / rowHeight)
        }

        // Then apply the scaling
        val spacing = (baseSpacing * scale).toInt()
        if (lastAppliedSpacing != spacing) {
            lastAppliedSpacing = spacing
            setSpacing(spacing)
        }
        spbChildren.forEach { it.applyPlaybackScale(scale) }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}
