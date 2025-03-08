/*
 * Copyright (c) 2024 Auxio Project
 * CoverFetcher.kt is part of Auxio.
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
import androidx.core.graphics.drawable.toDrawable
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.size.Dimension
import coil3.size.Size
import coil3.size.pxOrElse
import java.io.InputStream
import javax.inject.Inject
import okio.FileSystem
import okio.buffer
import okio.source
import org.oxycblt.musikr.covers.Cover

class CoverFetcher private constructor(private val context: Context, private val cover: Cover) :
    Fetcher {
    override suspend fun fetch(): FetchResult? {
        val stream = cover.open() ?: return null
        return SourceFetchResult(
            source = ImageSource(stream.source().buffer(), FileSystem.SYSTEM, null),
            mimeType = null,
            dataSource = DataSource.DISK)
    }

    /** Derived from phonograph: https://github.com/kabouzeid/Phonograph */
    private suspend fun createMosaic(streams: List<InputStream>, size: Size): FetchResult {
        // Use whatever size coil gives us to create the mosaic.
        val mosaicSize = android.util.Size(size.width.mosaicSize(), size.height.mosaicSize())
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

            // Crop the bitmap down to a square so it leaves no empty space
            // TODO: Work around this
            val bitmap =
                SquareCropTransformation.INSTANCE.transform(
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
        return ImageFetchResult(
            image = mosaicBitmap.toDrawable(context.resources).asImage(),
            isSampled = true,
            dataSource = DataSource.DISK)
    }

    private fun Dimension.mosaicSize(): Int {
        // Since we want the mosaic to be perfectly divisible into two, we need to round any
        // odd image sizes upwards to prevent the mosaic creation from failing.
        val size = pxOrElse { 512 }
        return if (size.mod(2) > 0) size + 1 else size
    }

    class Factory @Inject constructor() : Fetcher.Factory<Cover> {
        override fun create(data: Cover, options: Options, imageLoader: ImageLoader) =
            CoverFetcher(options.context, data)
    }
}
