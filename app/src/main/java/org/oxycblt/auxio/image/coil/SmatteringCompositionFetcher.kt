/*
 * Copyright (c) 2024 Auxio Project
 * SmatteringCompositionFetcher.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image.coil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import androidx.annotation.ColorInt
import androidx.core.graphics.createBitmap
import androidx.core.graphics.withRotation
import coil3.ImageLoader
import coil3.fetch.Fetcher
import coil3.key.Keyer as CoilKeyer
import coil3.request.Options
import coil3.size.Size
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import org.oxycblt.musikr.covers.CoverCollection

private const val OUTSET_PERCENT = 0.08f

data class SmatteringCoverComposition(
    override val covers: CoverCollection,
    val cornerRadiusRatio: Float,
    override val seed: Int,
    @ColorInt val backgroundColor: Int,
) : CoverComposition

class SmatteringCompositionFetcher
private constructor(context: Context, private val data: SmatteringCoverComposition, size: Size) :
    CoverCompositionFetcher(context, data, size) {

    override fun compose(bitmaps: List<Bitmap>, size: Int, random: Random): Bitmap {
        val sizef = size.toFloat()
        val cornerRadius =
            min(sizef * data.cornerRadiusRatio, sizef * ComposeCoverDefaults.MAX_CORNER_RATIO)
        val gapWidth =
            max(
                sizef * ComposeCoverDefaults.GAP_RATIO,
                cornerRadius * ComposeCoverDefaults.MIN_GAP_CORNER_RATIO,
            )
        val innerRadius = (cornerRadius - gapWidth).coerceAtLeast(0f)
        val fanAngle = seededFanAngle(random)
        val tiltAngle = seededTiltAngle(random)
        val zOrder = seededZOrder(bitmaps.size, random)
        val result = createBitmap(size, size)
        val canvas = Canvas(result)
        canvas.drawColor(data.backgroundColor)

        val gapPaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = data.backgroundColor
                style = Paint.Style.FILL
            }
        val imagePaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                // Use the background color for the mask so anti-aliased edges blend
                // toward it, avoiding dark/light fringing artifacts
                color = data.backgroundColor
            }

        val coverSize = sizef * ComposeCoverDefaults.COVER_SIZE_PERCENT
        val outsetSize = sizef * OUTSET_PERCENT

        val p0 = RectF(0f, 0f, coverSize, coverSize)
        val p1 = RectF(sizef - coverSize, 0f, sizef, coverSize)
        val p2 = RectF(0f, sizef - coverSize, coverSize, sizef)
        val p3 = RectF(sizef - coverSize, sizef - coverSize, sizef, sizef)

        val positions = listOf(p0, p1, p2, p3)

        for (imageIndex in zOrder) {
            val bitmap = bitmaps[imageIndex]

            val baseRect = RectF(positions[imageIndex]).apply { inset(-outsetSize, -outsetSize) }

            val innerRect = RectF(baseRect)
            val gapRect = RectF(innerRect)
            gapRect.inset(-gapWidth, -gapWidth)
            val gapPath = rounded(gapRect, cornerRadius)

            val centerX = baseRect.centerX()
            val centerY = baseRect.centerY()
            val rotation =
                if (imageIndex == 0 || imageIndex == 3) {
                    tiltAngle - fanAngle
                } else {
                    tiltAngle + fanAngle
                }

            canvas.withRotation(rotation, centerX, centerY) {
                drawPath(gapPath, gapPaint)

                val savedLayer = saveLayer(innerRect, null)
                val maskPath = rounded(innerRect, innerRadius)
                drawPath(maskPath, imagePaint)

                imagePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                drawBitmapCover(this, bitmap, innerRect, imagePaint)
                imagePaint.xfermode = null

                restoreToCount(savedLayer)
            }
        }

        return result
    }

    private fun rounded(rect: RectF, radius: Float): Path =
        Path().apply { addRoundRect(rect, FloatArray(8) { radius }, Path.Direction.CW) }

    private fun seededFanAngle(random: Random) = random.nextFloat() * 10f + 5f // 5-15 degrees

    private fun seededTiltAngle(random: Random) =
        random.nextFloat() * 20f - 10f // -10 to +10 degrees

    class Factory @Inject constructor() : Fetcher.Factory<SmatteringCoverComposition> {
        override fun create(
            data: SmatteringCoverComposition,
            options: Options,
            imageLoader: ImageLoader,
        ) = SmatteringCompositionFetcher(options.context, data, options.size)
    }

    class Keyer @Inject constructor() : CoilKeyer<SmatteringCoverComposition> {
        override fun key(data: SmatteringCoverComposition, options: Options): String {
            val config = "${data.cornerRadiusRatio}.${data.seed}.${data.backgroundColor}"
            return "m:${data.covers.hashCode()}.${options.size.width}.${options.size.height}.$config"
        }
    }
}
