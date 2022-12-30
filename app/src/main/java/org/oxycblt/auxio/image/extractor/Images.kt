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
 
package org.oxycblt.auxio.image.extractor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Size as AndroidSize
import androidx.core.graphics.drawable.toDrawable
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.SourceResult
import coil.size.Dimension
import coil.size.Size
import coil.size.pxOrElse
import java.io.InputStream
import okio.buffer
import okio.source

/**
 * Utilities for constructing Artist and Genre images.
 * @author Alexander Capehart (OxygenCobalt), Karim Abou Zeid
 */
object Images {
    /**
     * Create a mosaic image from the given image [InputStream]s. Derived from phonograph:
     * https://github.com/kabouzeid/Phonograph
     * @param context [Context] required to generate the mosaic.
     * @param streams [InputStream]s of image data to create the mosaic out of.
     * @param size [Size] of the Mosaic to generate.
     */
    suspend fun createMosaic(
        context: Context,
        streams: List<InputStream>,
        size: Size
    ): FetchResult? {
        if (streams.size < 4) {
            return streams.firstOrNull()?.let { stream ->
                SourceResult(
                    source = ImageSource(stream.source().buffer(), context),
                    mimeType = null,
                    dataSource = DataSource.DISK)
            }
        }

        // Use whatever size coil gives us to create the mosaic.
        val mosaicSize = AndroidSize(size.width.mosaicSize(), size.height.mosaicSize())
        val mosaicFrameSize =
            Size(Dimension(mosaicSize.width / 2), Dimension(mosaicSize.height / 2))

        val mosaicBitmap =
            Bitmap.createBitmap(mosaicSize.width, mosaicSize.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mosaicBitmap)

        var x = 0
        var y = 0

        // For each stream, create a bitmap scaled to 1/4th of the mosaics combined size
        // and place it on a corner of the canvas.
        for (stream in streams) {
            if (y == mosaicSize.height) {
                break
            }

            // Run the bitmap through a transform to reflect the configuration of other images.
            val bitmap =
                SquareFrameTransform.INSTANCE.transform(
                    BitmapFactory.decodeStream(stream), mosaicFrameSize)
            canvas.drawBitmap(bitmap, x.toFloat(), y.toFloat(), null)

            x += bitmap.width
            if (x == mosaicSize.width) {
                x = 0
                y += bitmap.height
            }
        }

        // It's way easier to map this into a drawable then try to serialize it into an
        // BufferedSource. Just make sure we mark it as "sampled" so Coil doesn't try to
        // load low-res mosaics into high-res ImageViews.
        return DrawableResult(
            drawable = mosaicBitmap.toDrawable(context.resources),
            isSampled = true,
            dataSource = DataSource.DISK)
    }

    /**
     * Get an image dimension suitable to create a mosaic with.
     * @return A pixel dimension derived from the given [Dimension] that will always be even,
     * allowing it to be sub-divided.
     */
    private fun Dimension.mosaicSize(): Int {
        val size = pxOrElse { 512 }
        return if (size.mod(2) > 0) size + 1 else size
    }
}
