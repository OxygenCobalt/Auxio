/*
 * Copyright (c) 2026 Auxio Project
 * CoverCompositionFetcher.kt is part of Auxio.
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
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.graphics.drawable.toDrawable
import coil3.asImage
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.size.Size
import coil3.size.pxOrElse
import kotlin.math.min
import kotlin.random.Random
import kotlin.random.nextInt
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import org.oxycblt.musikr.covers.CoverCollection

interface CoverComposition {
    val covers: CoverCollection
    val seed: Int
}

abstract class CoverCompositionFetcher(
    private val context: Context,
    private val data: CoverComposition,
    private val size: Size,
) : Fetcher {
    final override suspend fun fetch(): FetchResult? {
        val bitmaps =
            data.covers.covers
                .asFlow()
                .mapNotNull { cover -> cover.open() }
                .mapNotNull { stream -> BitmapFactory.decodeStream(stream).also { stream.close() } }
                .take(4)
                .toList()
        if (bitmaps.size < 4) {
            val first = bitmaps.firstOrNull() ?: return null
            return ImageFetchResult(
                image = first.toDrawable(context.resources).asImage(),
                isSampled = true,
                dataSource = DataSource.DISK,
            )
        }

        val squareSize =
            min(size.width.pxOrElse { 512 }, size.height.pxOrElse { 512 }).coerceAtLeast(1)
        val random = Random(data.seed)
        for (i in 0..10) {
            // cycle random a few times
            random.nextLong()
        }
        for (i in 0..random.nextInt(1..30)) {
            // then cycle it some more
            random.nextLong()
        }
        return ImageFetchResult(
            image = compose(bitmaps, squareSize, random).toDrawable(context.resources).asImage(),
            isSampled = true,
            dataSource = DataSource.DISK,
        )
    }

    /**
     * Compose the given input cover data into a single FetchResult containing a composed cover
     * bitmap.
     *
     * @param bitmaps The bitmaps to use.
     * @param size The size of the square bitmap you should create.
     */
    protected abstract fun compose(bitmaps: List<Bitmap>, size: Int, random: Random): Bitmap

    protected fun drawBitmapCover(canvas: Canvas, bitmap: Bitmap, dest: RectF, paint: Paint) {
        val bitmapRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val destRatio = dest.width() / dest.height()

        val srcRect = Rect(0, 0, bitmap.width, bitmap.height)

        // crop the bitmap if it's not properly 1:1
        if (bitmapRatio > destRatio) {
            val newWidth = (bitmap.height * destRatio).toInt()
            val xOffset = (bitmap.width - newWidth) / 2
            srcRect.left = xOffset
            srcRect.right = xOffset + newWidth
        } else {
            val newHeight = (bitmap.width / destRatio).toInt()
            val yOffset = (bitmap.height - newHeight) / 2
            srcRect.top = yOffset
            srcRect.bottom = yOffset + newHeight
        }

        canvas.drawBitmap(bitmap, srcRect, dest, paint)
    }

    protected fun seededZOrder(n: Int, random: Random): List<Int> =
        (0 until n).toList().shuffled(random)
}
