/*
 * Copyright (c) 2024 Auxio Project
 * GalleryComposeFetcher.kt is part of Auxio.
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
import androidx.core.graphics.withClip
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

data class GalleryCoverCollection(
    override val covers: CoverCollection,
    override val seed: Int,
    val cornerRadiusRatio: Float,
    @ColorInt val backgroundColor: Int,
) : CoverComposition

class GalleryComposeFetcher
private constructor(context: Context, private val data: GalleryCoverCollection, size: Size) :
    CoverCompositionFetcher(context, data, size) {

    override fun compose(bitmaps: List<Bitmap>, size: Int, random: Random): Bitmap {
        val sizef = size.toFloat()
        val cornerRadiusPx =
            min(sizef * data.cornerRadiusRatio, sizef * ComposeCoverDefaults.MAX_CORNER_RATIO)
        val gapWidthPx =
            max(
                sizef * ComposeCoverDefaults.GAP_RATIO,
                cornerRadiusPx * ComposeCoverDefaults.MIN_GAP_CORNER_RATIO,
            )
        val backgroundColor = data.backgroundColor
        val zOrder = seededZOrder(bitmaps.size, random)
        val result = createBitmap(size, size)
        val canvas = Canvas(result)
        canvas.drawColor(backgroundColor)

        val gapPaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = backgroundColor
                style = Paint.Style.FILL
            }
        val imagePaint =
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                // Use the background color for the mask so anti-aliased edges blend
                // toward it, avoiding dark/light fringing artifacts
                color = backgroundColor
            }

        val coverSize = sizef * ComposeCoverDefaults.COVER_SIZE_PERCENT

        val p0 = RectF(0f, 0f, coverSize, coverSize)
        val p1 = RectF(sizef - coverSize, 0f, sizef, coverSize)
        val p2 = RectF(0f, sizef - coverSize, coverSize, sizef)
        val p3 = RectF(sizef - coverSize, sizef - coverSize, sizef, sizef)

        val positions = listOf(p0, p1, p2, p3)
        // first we actually have to aggregate the geometry here so that
        // our overlaps will actually work
        val tiles =
            positions.mapIndexed { index, baseRect ->
                // generate path-tiles for each cover
                val isTop = index < 2
                val isLeft = index % 2 == 0
                val isBottom = !isTop
                val isRight = !isLeft

                // inset base cover rect to prevent rounded corners from stacking in a way
                // that leads weird "holes" and is instead more dynamic
                // todo: refine this so we can do dynamic overlap?
                val innerRect = RectF(baseRect)
                innerRect.left += if (isLeft) 0f else gapWidthPx
                innerRect.top += if (isTop) 0f else gapWidthPx
                innerRect.right -= if (isRight) 0f else gapWidthPx
                innerRect.bottom -= if (isBottom) 0f else gapWidthPx

                // set up the actual gap path
                val gapRect = RectF(innerRect)
                gapRect.inset(-gapWidthPx, -gapWidthPx)

                val gapPath =
                    rounded(
                        gapRect,
                        topLeft = if (!isTop && !isLeft) cornerRadiusPx else 0f,
                        topRight = if (!isTop && !isRight) cornerRadiusPx else 0f,
                        bottomRight = if (!isBottom && !isRight) cornerRadiusPx else 0f,
                        bottomLeft = if (!isBottom && !isLeft) cornerRadiusPx else 0f,
                    )

                // then the inner mask
                // adjust corner radii to make up for gap
                val innerRadius = (cornerRadiusPx - gapWidthPx).coerceAtLeast(0f)
                val maskPath =
                    rounded(
                        innerRect,
                        topLeft = if (!isTop && !isLeft) innerRadius else 0f,
                        topRight = if (!isTop && !isRight) innerRadius else 0f,
                        bottomRight = if (!isBottom && !isRight) innerRadius else 0f,
                        bottomLeft = if (!isBottom && !isLeft) innerRadius else 0f,
                    )

                TileGeometry(innerRect, gapPath, maskPath)
            }

        for ((orderIndex, imageIndex) in zOrder.withIndex()) {
            val bitmap = bitmaps[imageIndex]
            val tile = tiles[imageIndex]
            // we actually go and difference our gap with the others so we know
            // what parts of the path are actually going to be visible after
            // the inset
            val visiblePath = Path(tile.gapPath)
            for (index in orderIndex + 1 until zOrder.size) {
                visiblePath.op(tiles[zOrder[index]].gapPath, Path.Op.DIFFERENCE)
                if (visiblePath.isEmpty) {
                    break
                }
            }

            if (visiblePath.isEmpty) {
                continue
            }

            // draw adjusted gap
            canvas.drawPath(visiblePath, gapPaint)

            // draw cover clipped to gap
            canvas.withClip(visiblePath) {
                val savedLayer = canvas.saveLayer(tile.innerRect, null)
                canvas.drawPath(tile.maskPath, imagePaint)

                imagePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
                drawBitmapCover(canvas, bitmap, tile.innerRect, imagePaint)
                imagePaint.xfermode = null

                canvas.restoreToCount(savedLayer)
            }
        }

        return result
    }

    private fun rounded(
        rect: RectF,
        topLeft: Float,
        topRight: Float,
        bottomRight: Float,
        bottomLeft: Float,
    ) =
        Path().apply {
            addRoundRect(
                rect,
                floatArrayOf(
                    topLeft,
                    topLeft,
                    topRight,
                    topRight,
                    bottomRight,
                    bottomRight,
                    bottomLeft,
                    bottomLeft,
                ),
                Path.Direction.CW,
            )
        }

    private data class TileGeometry(val innerRect: RectF, val gapPath: Path, val maskPath: Path)

    class Factory @Inject constructor() : Fetcher.Factory<GalleryCoverCollection> {
        override fun create(
            data: GalleryCoverCollection,
            options: Options,
            imageLoader: ImageLoader,
        ) = GalleryComposeFetcher(options.context, data, options.size)
    }

    class Keyer @Inject constructor() : CoilKeyer<GalleryCoverCollection> {
        override fun key(data: GalleryCoverCollection, options: Options): String {
            val config = "${data.cornerRadiusRatio}.${data.seed}.${data.backgroundColor}"
            return "g:${data.covers.hashCode()}.${options.size.width}.${options.size.height}.$config"
        }
    }
}
