/*
 * Copyright (c) 2023 Auxio Project
 * RoundedRectTransformation.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image.extractor

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.BitmapShader
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.RectF
import android.graphics.Shader
import androidx.annotation.Px
import androidx.core.graphics.applyCanvas
import coil.decode.DecodeUtils
import coil.size.Scale
import coil.size.Size
import coil.size.pxOrElse
import coil.transform.Transformation
import kotlin.math.roundToInt

/**
 * A vendoring of [coil.transform.RoundedCornersTransformation] that can handle non-1:1 aspect ratio
 * images without cropping them.
 *
 * @author Coil Team, Alexander Capehart (OxygenCobalt)
 */
class RoundedRectTransformation(
    @Px private val topLeft: Float = 0f,
    @Px private val topRight: Float = 0f,
    @Px private val bottomLeft: Float = 0f,
    @Px private val bottomRight: Float = 0f
) : Transformation {

    constructor(@Px radius: Float) : this(radius, radius, radius, radius)

    init {
        require(topLeft >= 0 && topRight >= 0 && bottomLeft >= 0 && bottomRight >= 0) {
            "All radii must be >= 0."
        }
    }

    override val cacheKey = "${javaClass.name}-$topLeft,$topRight,$bottomLeft,$bottomRight"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        val (outputWidth, outputHeight) = calculateOutputSize(input, size)

        val output = createBitmap(outputWidth, outputHeight, input.config)
        output.applyCanvas {
            drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

            val matrix = Matrix()
            val multiplier =
                DecodeUtils.computeSizeMultiplier(
                        srcWidth = input.width,
                        srcHeight = input.height,
                        dstWidth = outputWidth,
                        dstHeight = outputHeight,
                        scale = Scale.FILL)
                    .toFloat()
            val dx = (outputWidth - multiplier * input.width) / 2
            val dy = (outputHeight - multiplier * input.height) / 2
            matrix.setTranslate(dx, dy)
            matrix.preScale(multiplier, multiplier)

            val shader = BitmapShader(input, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            shader.setLocalMatrix(matrix)
            paint.shader = shader

            val radii =
                floatArrayOf(
                    topLeft,
                    topLeft,
                    topRight,
                    topRight,
                    bottomRight,
                    bottomRight,
                    bottomLeft,
                    bottomLeft,
                )
            val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
            val path = Path().apply { addRoundRect(rect, radii, Path.Direction.CW) }
            drawPath(path, paint)
        }

        return output
    }

    private fun calculateOutputSize(input: Bitmap, size: Size): Pair<Int, Int> {
        // MODIFICATION: Remove short-circuiting for original size and input size
        val multiplier =
            DecodeUtils.computeSizeMultiplier(
                srcWidth = input.width,
                srcHeight = input.height,
                dstWidth = size.width.pxOrElse { Int.MIN_VALUE },
                dstHeight = size.height.pxOrElse { Int.MIN_VALUE },
                scale = Scale.FIT)
        val outputWidth = (multiplier * input.width).roundToInt()
        val outputHeight = (multiplier * input.height).roundToInt()
        return outputWidth to outputHeight
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return other is RoundedRectTransformation &&
            topLeft == other.topLeft &&
            topRight == other.topRight &&
            bottomLeft == other.bottomLeft &&
            bottomRight == other.bottomRight
    }

    override fun hashCode(): Int {
        var result = topLeft.hashCode()
        result = 31 * result + topRight.hashCode()
        result = 31 * result + bottomLeft.hashCode()
        result = 31 * result + bottomRight.hashCode()
        return result
    }
}
