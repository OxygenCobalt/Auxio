/*
 * Copyright (c) 2021 Auxio Project
 * CenterSmoothScroller.kt is part of Auxio.
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

package org.oxycblt.auxio.recycler

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller

/**
 * [LinearSmoothScroller] subclass that centers the item on the screen instead of snapping to the
 * top or bottom.
 * @author OxygenCobalt
 */
class CenterSmoothScroller(context: Context, target: Int) : LinearSmoothScroller(context) {
    init {
        targetPosition = target
    }

    override fun calculateDtToFit(
        viewStart: Int,
        viewEnd: Int,
        boxStart: Int,
        boxEnd: Int,
        snapPreference: Int
    ): Int {
        return (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
    }
}
