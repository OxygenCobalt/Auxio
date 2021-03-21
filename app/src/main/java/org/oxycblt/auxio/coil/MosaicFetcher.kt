package org.oxycblt.auxio.coil

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
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Parent
import java.io.Closeable
import java.io.InputStream

/**
 * A [Fetcher] that takes an [Artist] or [Genre] and returns a mosaic of its albums.
 * @author OxygenCobalt
 */
class MosaicFetcher(private val context: Context) : Fetcher<Parent> {
    override suspend fun fetch(
        pool: BitmapPool,
        data: Parent,
        size: Size,
        options: Options
    ): FetchResult {
        // Get the URIs for either a genre or artist
        val uris = mutableListOf<Uri>()

        when (data) {
            is Artist -> data.albums.forEachIndexed { index, album ->
                if (index < 4) { uris.add(album.coverUri) }
            }

            is Genre -> data.songs.groupBy { it.album.coverUri }.keys.forEachIndexed { index, uri ->
                if (index < 4) { uris.add(uri) }
            }

            else -> {}
        }

        val streams = mutableListOf<InputStream>()

        // Load MediaStore streams
        uris.forEach { uri ->
            val stream: InputStream? = context.contentResolver.openInputStream(uri)

            if (stream != null) {
                streams.add(stream)
            }
        }

        // If so many streams failed that there's not enough images to make a mosaic, then
        // just return the first cover image.
        if (streams.size < 4) {
            // Dont even bother if ALL the streams have failed.
            check(streams.isNotEmpty()) { "All streams have failed. " }

            return SourceResult(
                source = streams[0].source().buffer(),
                mimeType = context.contentResolver.getType(uris[0]),
                dataSource = DataSource.DISK
            )
        }

        val bitmap = drawMosaic(streams)

        return DrawableResult(
            drawable = bitmap.toDrawable(context.resources),
            isSampled = false,
            dataSource = DataSource.DISK
        )
    }

    /**
     * Create the mosaic image, Code adapted from Phonograph
     * https://github.com/kabouzeid/Phonograph
     */
    private fun drawMosaic(streams: List<InputStream>): Bitmap {
        val mosaicBitmap = Bitmap.createBitmap(
            MOSAIC_BITMAP_SIZE, MOSAIC_BITMAP_SIZE, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(mosaicBitmap)

        var x = 0
        var y = 0

        // For each stream, create a bitmap scaled to 1/4th of the mosaics combined size
        // and place it on a corner of the canvas.
        streams.useForEach { stream ->
            if (y == MOSAIC_BITMAP_SIZE) return@useForEach

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
            }
        }

        return mosaicBitmap
    }

    /**
     * Iterate through a list of [Closeable]s, running [block] on each and closing it when done.
     */
    private fun <T : Closeable> List<T>.useForEach(block: (T) -> Unit) {
        forEach { it.use(block) }
    }

    override fun key(data: Parent): String = data.hashCode().toString()
    override fun handles(data: Parent) = data !is Album // Albums are not used here

    companion object {
        private const val MOSAIC_BITMAP_SIZE = 512
        private const val MOSAIC_BITMAP_INCREMENT = 256
    }
}
