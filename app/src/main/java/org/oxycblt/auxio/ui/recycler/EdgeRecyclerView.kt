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
 
package org.oxycblt.auxio.ui.recycler

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.annotation.AttrRes
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.util.systemBarInsetsCompat

/** A [RecyclerView] that automatically applies insets to itself. */
open class EdgeRecyclerView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    RecyclerView(context, attrs, defStyleAttr) {
    private val initialPadding = Rect(paddingLeft, paddingTop, paddingRight, paddingBottom)

    init {
        // Prevent children from being clipped by window insets
        clipToPadding = false
        setHasFixedSize(true)
    }

    final override fun setHasFixedSize(hasFixedSize: Boolean) {
        super.setHasFixedSize(hasFixedSize)
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        updatePadding(
            initialPadding.left,
            initialPadding.top,
            initialPadding.right,
            initialPadding.bottom + insets.systemBarInsetsCompat.bottom)

        return insets
    }
}
