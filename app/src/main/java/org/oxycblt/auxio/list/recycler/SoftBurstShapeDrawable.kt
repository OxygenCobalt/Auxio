/*
 * Copyright (c) 2026 Auxio Project
 * SoftBurstShapeDrawable.kt is part of Auxio.
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
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import com.google.android.material.R as MR
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import org.oxycblt.auxio.ui.ExpressiveShapes
import org.oxycblt.auxio.util.getAttrColorCompat

/**
 * A drawable that fills its bounds with the expressive soft-burst shape.
 *
 * The shape is always rendered as a centered square and uniformly scaled from its geometric center
 * to avoid distortion in non-square bounds.
 */
class SoftBurstShapeDrawable(context: Context) : Drawable() {
    private val pathPosition = FloatArray(2)
    private val pathMatrix = Matrix()
    private val pathBounds = RectF()
    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = context.getAttrColorCompat(MR.attr.colorSecondaryContainer).defaultColor
            style = Paint.Style.FILL
        }

    private var fittedPath = Path()

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        rebuildPath()
    }

    private fun rebuildPath() {
        val drawableBounds = bounds
        if (drawableBounds.isEmpty) {
            fittedPath = Path()
            return
        }

        val size = min(drawableBounds.width(), drawableBounds.height()).toFloat()
        val left = drawableBounds.exactCenterX() - size / 2f
        val top = drawableBounds.exactCenterY() - size / 2f
        pathBounds.set(left, top, left + size, top + size)

        fittedPath = ExpressiveShapes.getSoftBurst(pathBounds)
        val maxRadius = computeMaxRadius(fittedPath, pathBounds.centerX(), pathBounds.centerY())
        if (maxRadius > 0f) {
            val targetRadius = size / 2f
            val uniformScale = targetRadius / maxRadius
            pathMatrix.reset()
            pathMatrix.setScale(
                uniformScale,
                uniformScale,
                pathBounds.centerX(),
                pathBounds.centerY(),
            )
            fittedPath.transform(pathMatrix)
        }
    }

    override fun draw(canvas: Canvas) {
        if (fittedPath.isEmpty) {
            return
        }
        canvas.drawPath(fittedPath, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha.coerceIn(0, 255)
        invalidateSelf()
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    private fun computeMaxRadius(source: Path, centerX: Float, centerY: Float): Float {
        val measure = PathMeasure(source, false)
        var maxRadius = 0f

        do {
            val length = measure.length
            if (length <= 0f) {
                continue
            }

            val sampleCount = max((length / 1f).roundToInt(), 1)
            for (sample in 0..sampleCount) {
                val distance = length * sample / sampleCount
                if (measure.getPosTan(distance, pathPosition, null)) {
                    maxRadius =
                        max(maxRadius, hypot(pathPosition[0] - centerX, pathPosition[1] - centerY))
                }
            }
        } while (measure.nextContour())

        return maxRadius
    }
}
