/*
 * Copyright (c) 2026 Auxio Project
 * SquareMaskableFrameLayout.kt is part of Auxio.
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
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

/** A square [FrameLayout] that clips its contents to a Material shape. */
class SquareMaskableFrameLayout
@JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {
    private val shapeDrawable = MaterialShapeDrawable()
    private val maskPath = Path()
    private val maskRect = RectF()

    init {
        val shapeAppearanceModel =
            ShapeAppearanceModel.builder(context, attrs, defStyleAttr, 0).build()
        shapeDrawable.shapeAppearanceModel = shapeAppearanceModel
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = minOf(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        maskRect.set(0f, 0f, w.toFloat(), h.toFloat())
        shapeDrawable.setBounds(0, 0, w, h)
        maskPath.reset()
        shapeDrawable.getPathForSize(w, h, maskPath)
    }

    override fun dispatchDraw(canvas: Canvas) {
        val save = canvas.save()
        canvas.clipPath(maskPath)
        super.dispatchDraw(canvas)
        canvas.restoreToCount(save)
    }
}
