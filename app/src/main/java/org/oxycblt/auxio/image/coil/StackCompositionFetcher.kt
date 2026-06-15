/*
 * Copyright (c) 2024 Auxio Project
 * StackCompositionFetcher.kt is part of Auxio.
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

data class StackCoverComposition(
    override val covers: CoverCollection,
    val cornerRadiusRatio: Float,
    override val seed: Int,
    @ColorInt val backgroundColor: Int,
) : CoverComposition

/**
 * A fetcher that stacks covers towards the upper left corner, creating an orderly feeling. Used for
 * playlists.
 *
 * @author OxygenCobalt (Alexander Capehart)
 */
class StackCompositionFetcher
private constructor(context: Context, val data: StackCoverComposition, size: Size) :
    CoverCompositionFetcher(context, data, size) {
    private val gapPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = data.backgroundColor
            style = Paint.Style.FILL
        }

    private val coverPaint =
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            // Use the background color for the mask so anti-aliased edges blend
            // toward it, avoiding dark/light fringing artifacts
            color = data.backgroundColor
        }

    override fun compose(bitmaps: List<Bitmap>, size: Int, random: Random): Bitmap {
        val sizef = size.toFloat()
        val cornerRadius =
            min(sizef * data.cornerRadiusRatio, sizef * ComposeCoverDefaults.MAX_CORNER_RATIO)
        val gapWidthPx =
            max(
                sizef * ComposeCoverDefaults.GAP_RATIO,
                cornerRadius * ComposeCoverDefaults.MIN_GAP_CORNER_RATIO,
            )
        val zOrder = seededZOrder(bitmaps.size, random)
        val result = createBitmap(size, size)
        val canvas = Canvas(result)
        canvas.drawColor(data.backgroundColor)
        // this is how much we will overlap covers onto eachother
        val overlapSize = (sizef * ComposeCoverDefaults.COVER_SIZE_PERCENT).coerceAtMost(sizef)
        // in turn this is the visible fraction we will get of a cover post-overlap
        val visibleSize = sizef - overlapSize
        // by dividing it by 2 you uhhhhhhhh...hm....
        // this is a hard-coded constant derived when i was deslopping gpt output knowing
        // that we will always have 4 covers. specifically its the simplification of
        // (lastIndex - 1) = (3 - 1) = 2.
        // without this i can only fit at most 2ish covers in
        // this is why i dont want raw unreviewed ai slop in the codebase btw. lesson learned
        val maxStep = visibleSize / 2
        // re-adjust our corner radius by the actual width of the gap
        // (this makes corners proportional, i do this elsewhere and it looks nice)
        val innerRadius = (cornerRadius - gapWidthPx).coerceAtLeast(0f)
        for (stackIndex in bitmaps.indices) {
            val imageIndex = zOrder[stackIndex]
            val bitmap = bitmaps[imageIndex]

            // move diagonally, the more we move in x the more we should offset in y
            val offsetX = stackIndex * maxStep
            val offsetY = visibleSize - offsetX // x = y here

            // base frame for gap
            val baseRect =
                RectF(
                    offsetX,
                    offsetY - visibleSize,
                    offsetX + sizef,
                    offsetY - visibleSize + sizef,
                )
            // possible adjusted frame
            val coverRect = RectF(baseRect)

            val hasGap = stackIndex > 0 && gapWidthPx > 0f
            if (hasGap) {
                // draw gap
                val gapPath = teardrop(baseRect, cornerRadius)
                canvas.drawPath(gapPath, gapPaint)
                // adjust cover accordingly to compensate
                coverRect.left += gapWidthPx
                coverRect.bottom -= gapWidthPx
            }

            // draw cover on map
            val savedLayer = canvas.saveLayer(coverRect, null)
            val maskPath = teardrop(coverRect, if (hasGap) innerRadius else 0f)
            canvas.drawPath(maskPath, coverPaint)
            // special flags to avoid weird outlines
            coverPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            drawBitmapCover(canvas, bitmap, coverRect, coverPaint)
            coverPaint.xfermode = null
            canvas.restoreToCount(savedLayer)
        }

        return result
    }

    private fun teardrop(rect: RectF, radius: Float) =
        Path().apply {
            addRoundRect(
                rect,
                floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, radius, radius),
                Path.Direction.CW,
            )
        }

    class Factory @Inject constructor() : Fetcher.Factory<StackCoverComposition> {
        override fun create(
            data: StackCoverComposition,
            options: Options,
            imageLoader: ImageLoader,
        ) = StackCompositionFetcher(options.context, data, options.size)
    }

    class Keyer @Inject constructor() : CoilKeyer<StackCoverComposition> {
        override fun key(data: StackCoverComposition, options: Options): String {
            val config = "${data.cornerRadiusRatio}.${data.seed}.${data.backgroundColor}"
            return "s:${data.covers.hashCode()}.${options.size.width}.${options.size.height}.$config"
        }
    }
}
