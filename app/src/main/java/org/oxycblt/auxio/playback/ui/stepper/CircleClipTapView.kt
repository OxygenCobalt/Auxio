/*
 * Copyright (c) 2025 Auxio Project
 * CircleClipTapView.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.ui.stepper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class CircleClipTapView(context: Context?, attrs: AttributeSet) : View(context, attrs) {

    private var backgroundPaint = Paint()

    private var widthPx = 0
    private var heightPx = 0

    // Background

    private var shapePath = Path()
    private var arcSize: Float = 80f
    private var isLeft = true

    init {
        requireNotNull(context) { "Context is null." }

        backgroundPaint.apply {
            style = Paint.Style.FILL
            isAntiAlias = true
            color = 0x30000000
        }

        val dm = context.resources.displayMetrics
        widthPx = dm.widthPixels
        heightPx = dm.heightPixels

        updatePathShape()
    }

    fun updateArcSize(baseView: View) {
        val newArcSize = baseView.height / 11.4f
        if (arcSize != newArcSize) {
            arcSize = newArcSize
            updatePathShape()
        }
    }

    fun updatePosition(newIsLeft: Boolean) {
        if (isLeft != newIsLeft) {
            isLeft = newIsLeft
            updatePathShape()
        }
    }

    private fun updatePathShape() {
        val halfWidth = widthPx * 0.5f

        shapePath.reset()

        val w = if (isLeft) 0f else widthPx.toFloat()
        val f = if (isLeft) 1 else -1

        shapePath.moveTo(w, 0f)
        shapePath.lineTo(f * (halfWidth - arcSize) + w, 0f)
        shapePath.quadTo(
            f * (halfWidth + arcSize) + w,
            heightPx.toFloat() / 2,
            f * (halfWidth - arcSize) + w,
            heightPx.toFloat())
        shapePath.lineTo(w, heightPx.toFloat())

        shapePath.close()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widthPx = w
        heightPx = h
        updatePathShape()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.clipPath(shapePath)
        canvas.drawPath(shapePath, backgroundPaint)
    }
}
