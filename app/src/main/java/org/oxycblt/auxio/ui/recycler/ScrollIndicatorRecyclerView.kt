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
 
package org.oxycblt.auxio.ui.recycler

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.core.view.isInvisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDivider
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getDimenSizeSafe

/**
 * A RecyclerView that enables something resembling the android:scrollIndicators attribute. Only
 * used in dialogs.
 * @author OxygenCobalt
 */
class ScrollIndicatorRecyclerView
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    RecyclerView(context, attrs, defStyleAttr) {
    private val topDivider = MaterialDivider(context)
    private val bottomDivider = MaterialDivider(context)

    private val spacingMedium = context.getDimenSizeSafe(R.dimen.spacing_medium)

    init {
        updatePadding(top = spacingMedium)
        overScrollMode = OVER_SCROLL_NEVER

        overlay.apply {
            add(topDivider)
            add(bottomDivider)
        }
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)

        val manager = layoutManager as LinearLayoutManager
        topDivider.isInvisible = manager.findFirstCompletelyVisibleItemPosition() < 1
        bottomDivider.isInvisible =
            manager.findLastCompletelyVisibleItemPosition() == (manager.itemCount - 1)
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        super.onMeasure(widthSpec, heightSpec)
        measureDivider(topDivider)
        measureDivider(bottomDivider)
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

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        topDivider.layout(l, spacingMedium, r, spacingMedium + topDivider.measuredHeight)
        bottomDivider.layout(l, measuredHeight - bottomDivider.measuredHeight, r, b)
    }
}
