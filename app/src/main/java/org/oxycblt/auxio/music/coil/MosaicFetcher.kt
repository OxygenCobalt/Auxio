package org.oxycblt.auxio.music.coil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.net.Uri
import androidx.core.graphics.drawable.toDrawable
import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.size.Size
import okio.buffer
import okio.source
import org.oxycblt.auxio.R
import java.io.InputStream

const val MOSAIC_BITMAP_SIZE = 512
const val MOSAIC_BITMAP_INCREMENT = 256

// A Fetcher that takes multiple cover uris and turns them into a NxN mosaic image.
class MosaicFetcher(private val context: Context) : Fetcher<List<Uri>> {
    override suspend fun fetch(
        pool: BitmapPool,
        data: List<Uri>,
        size: Size,
        options: Options
    ): FetchResult {
        val streams = mutableListOf<InputStream>()

        // Load the streams.
        data.forEach {
            val stream: InputStream? = context.contentResolver.openInputStream(it)

            if (stream != null) {
                streams.add(stream)
            }
        }

        // If so many streams failed that there's not enough images to make a mosaic, then
        // just return the first cover image.
        if (streams.size < 4) {
            streams.forEach { it.close() }

            return if (streams.isNotEmpty()) {
                SourceResult(
                    source = streams[0].source().buffer(),
                    mimeType = context.contentResolver.getType(data[0]),
                    dataSource = DataSource.DISK
                )
            } else {
                // If ALL the streams failed, then don't even bother with that.
                DrawableResult(
                    drawable = R.drawable.ic_song.toDrawable(),
                    isSampled = false,
                    dataSource = DataSource.DISK
                )
            }
        }

        // Create the mosaic, code adapted from Phonograph.
        // https://github.com/kabouzeid/Phonograph
        val finalBitmap = Bitmap.createBitmap(
            MOSAIC_BITMAP_SIZE, MOSAIC_BITMAP_SIZE, Bitmap.Config.RGB_565
        )

        val canvas = Canvas(finalBitmap)

        var x = 0
        var y = 0

        // For each stream, create a bitmap scaled to 1/4th of the mosaics combined size
        // and place it on a corner of the canvas.
        for (stream in streams) {
            val bitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeStream(stream),
                MOSAIC_BITMAP_INCREMENT,
                MOSAIC_BITMAP_INCREMENT,
                true
            )

            canvas.drawBitmap(bitmap, x.toFloat(), y.toFloat(), null)

            x += MOSAIC_BITMAP_INCREMENT

            if (x == MOSAIC_BITMAP_SIZE) {
                x = 0
                y += MOSAIC_BITMAP_INCREMENT

                if (y == MOSAIC_BITMAP_SIZE) {
                    break
                }
            }
        }

        // Close all the streams when done.
        streams.forEach { it.close() }

        return DrawableResult(
            drawable = finalBitmap.toDrawable(context.resources),
            isSampled = false,
            dataSource = DataSource.DISK
        )
    }

    override fun key(data: List<Uri>): String? = data.toString()
}
