/*
 * Copyright (c) 2021 Auxio Project
 * AutoGridLayoutManager.kt is part of Auxio.
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

package org.oxycblt.auxio.accent

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max

/**
 * A sub-class of [GridLayoutManager] that automatically sets the spans so that they fit the width
 * of the RecyclerView.
 * Adapted from this StackOverflow answer: https://stackoverflow.com/a/30256880/14143986
 */
class AutoGridLayoutManager(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int,
    defStyleRes: Int
) : GridLayoutManager(context, attrs, defStyleAttr, defStyleRes) {
    // We use 72dp here since that's the rough size of the accent item.
    // This will need to be modified if this is used beyond the accent dialog.
    private var columnWidth = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, 72F, context.resources.displayMetrics
    ).toInt()

    private var lastWidth = -1
    private var lastHeight = -1

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        if (width > 0 && height > 0 && (lastWidth != width || lastHeight != height)) {
            val totalSpace = width - paddingRight - paddingLeft
            val spanCount = max(1, totalSpace / columnWidth)

            setSpanCount(spanCount)
        }

        lastWidth = width
        lastHeight = height

        super.onLayoutChildren(recycler, state)
    }
}
