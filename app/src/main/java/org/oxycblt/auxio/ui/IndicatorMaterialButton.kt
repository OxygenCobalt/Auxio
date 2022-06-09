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
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.AttrRes
import com.google.android.material.button.MaterialButton
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.getDrawableSafe

class IndicatorMaterialButton
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, @AttrRes defStyleAttr: Int = 0) :
    MaterialButton(context, attrs, defStyleAttr) {
    private val indicatorDrawable = context.getDrawableSafe(R.drawable.ui_indicator)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Put the indicator right below the icon.
        val x = (measuredWidth - indicatorDrawable.intrinsicWidth) / 2
        val y = ((measuredHeight - iconSize) / 2) + iconSize

        indicatorDrawable.bounds.set(
            x, y, x + indicatorDrawable.intrinsicWidth, y + indicatorDrawable.intrinsicHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // I would use onDrawForeground but apparently that isn't called by Lollipop devices.
        // This is not referenced in the documentation at all.
        if (isActivated) {
            indicatorDrawable.draw(canvas)
        }
    }
}
