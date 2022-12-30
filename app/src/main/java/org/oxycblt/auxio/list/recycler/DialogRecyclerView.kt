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
 
package org.oxycblt.auxio.list.recycler

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.core.view.isInvisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDivider
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getDimenPixels

/**
 * A [RecyclerView] intended for use in Dialogs, adding features such as:
 * - NestedScrollView scrollIndicators behavior emulation
 * - Dialog-specific [ViewHolder] that automatically resolves certain issues.
 * @author Alexander Capehart (OxygenCobalt)
 */
class DialogRecyclerView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    RecyclerView(context, attrs, defStyleAttr) {
    private val topDivider = MaterialDivider(context)
    private val bottomDivider = MaterialDivider(context)
    private val spacingMedium = context.getDimenPixels(R.dimen.spacing_medium)

    init {
        // Apply top padding to give enough room to the dialog title, assuming that this view
        // is at the top of the dialog.
        updatePadding(top = spacingMedium)
        // Disable over-scrolling, the top and bottom dividers have the same purpose.
        overScrollMode = OVER_SCROLL_NEVER
        // Safer to use the overlay than the actual RecyclerView children.
        overlay.apply {
            add(topDivider)
            add(bottomDivider)
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        measureDivider(topDivider)
        measureDivider(bottomDivider)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        topDivider.layout(l, spacingMedium, r, spacingMedium + topDivider.measuredHeight)
        bottomDivider.layout(l, measuredHeight - bottomDivider.measuredHeight, r, b)
        // Make sure we initialize the dividers here before we start drawing.
        invalidateDividers()
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        // Scroll event occurred, need to update the dividers.
        invalidateDividers()
    }

    private fun measureDivider(divider: MaterialDivider) {
        val widthMeasureSpec =
            ViewGroup.getChildMeasureSpec(
                MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                0,
                divider.layoutParams.width)
        val heightMeasureSpec =
            ViewGroup.getChildMeasureSpec(
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY),
                0,
                divider.layoutParams.height)
        divider.measure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun invalidateDividers() {
        val lmm = layoutManager as LinearLayoutManager
        // Top divider should only be visible when the first item has gone off-screen.
        topDivider.isInvisible = lmm.findFirstCompletelyVisibleItemPosition() < 1
        // Bottom divider should only be visible when the lsat item is completely on-screen.
        bottomDivider.isInvisible =
            lmm.findLastCompletelyVisibleItemPosition() == (lmm.itemCount - 1)
    }

    /** A [RecyclerView.ViewHolder] that implements dialog-specific fixes. */
    abstract class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        init {
            // ViewHolders are not automatically full-width in dialogs, manually resize
            // them to be as such.
            root.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }
    }
}
