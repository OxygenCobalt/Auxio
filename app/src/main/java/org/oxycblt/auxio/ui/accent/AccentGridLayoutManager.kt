/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio.ui.accent

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getDimenPixels

/**
 * A [GridLayoutManager] that automatically sets the span size in order to use the most possible
 * space in the [RecyclerView]. Derived from this StackOverflow answer:
 * https://stackoverflow.com/a/30256880/14143986
 */
class AccentGridLayoutManager(
    context: Context,
    attrs: AttributeSet,
    defStyleAttr: Int,
    defStyleRes: Int
) : GridLayoutManager(context, attrs, defStyleAttr, defStyleRes) {
    // We use 56dp here since that's the rough size of the accent item.
    // This will need to be modified if this is used beyond the accent dialog.
    private var columnWidth = context.getDimenPixels(R.dimen.size_accent_item)
    private var lastWidth = -1
    private var lastHeight = -1

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        if (width > 0 && height > 0 && (lastWidth != width || lastHeight != height)) {
            val totalSpace = width - paddingRight - paddingLeft
            spanCount = max(1, totalSpace / columnWidth)
        }
        lastWidth = width
        lastHeight = height
        super.onLayoutChildren(recycler, state)
    }
}
