/*
 * Copyright (c) 2021 Auxio Project
 * AuxioRecyclerView.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.recycler

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.annotation.AttrRes
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.util.systemBarInsetsCompat

/**
 * A [RecyclerView] with a few QoL extensions, such as:
 * - Automatic edge-to-edge support
 * - Adapter-based [SpanSizeLookup] implementation
 * - Automatic [setHasFixedSize] setup
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
open class AuxioRecyclerView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    RecyclerView(context, attrs, defStyleAttr) {
    private val initialPaddingBottom = paddingBottom

    init {
        // Prevent children from being clipped by window insets
        clipToPadding = false
        // Auxio's non-dialog RecyclerViews never change their size based on adapter contents,
        // so we can enable fixed-size optimizations.
        setHasFixedSize(true)
        addItemDecoration(HeaderItemDecoration(context))
    }

    final override fun setHasFixedSize(hasFixedSize: Boolean) {
        // Prevent a this leak by marking setHasFixedSize as final.
        super.setHasFixedSize(hasFixedSize)
    }

    final override fun addItemDecoration(decor: ItemDecoration) {
        super.addItemDecoration(decor)
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        // Update the RecyclerView's padding such that the bottom insets are applied
        // while still preserving bottom padding.
        updatePadding(bottom = initialPaddingBottom + insets.systemBarInsetsCompat.bottom)
        return insets
    }

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)

        if (adapter is SpanSizeLookup) {
            // This adapter has support for special span sizes, hook it up to the
            // GridLayoutManager.
            val glm = (layoutManager as GridLayoutManager)
            val fullWidthSpanCount = glm.spanCount
            glm.spanSizeLookup =
                object : GridLayoutManager.SpanSizeLookup() {
                    // Using the adapter implementation, if the adapter specifies that
                    // an item is full width, it will take up all of the spans, using a
                    // single span otherwise.
                    override fun getSpanSize(position: Int) =
                        if (adapter.isItemFullWidth(position)) fullWidthSpanCount else 1
                }
        }
    }

    /** A [RecyclerView.Adapter]-specific hook to control divider decoration visibility. */

    /** An [RecyclerView.Adapter]-specific hook to [GridLayoutManager.SpanSizeLookup]. */
    interface SpanSizeLookup {
        /**
         * Get if the item at a position takes up the whole width of the [RecyclerView] or not.
         *
         * @param position The position of the item.
         * @return true if the item is full-width, false otherwise.
         */
        fun isItemFullWidth(position: Int): Boolean
    }
}
