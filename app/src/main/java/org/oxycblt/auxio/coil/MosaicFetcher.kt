/*
 * Copyright (c) 2021 Auxio Project
 * MosaicFetcher.kt is part of Auxio.
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

package org.oxycblt.auxio.coil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.core.graphics.drawable.toDrawable
import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.size.OriginalSize
import coil.size.Size
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.MusicParent
import java.io.Closeable
import java.lang.Exception

/**
 * A [Fetcher] that takes an [Artist] or [Genre] and returns a mosaic of its albums.
 * @author OxygenCobalt
 */
class MosaicFetcher(private val context: Context) : Fetcher<MusicParent> {
    override suspend fun fetch(
        pool: BitmapPool,
        data: MusicParent,
        size: Size,
        options: Options
    ): FetchResult {
        // Get the URIs for either a genre or artist
        val albums = mutableListOf<Album>()

        when (data) {
            is Artist -> data.albums.forEachIndexed { index, album ->
                if (index < 4) { albums.add(album) }
            }

            is Genre -> data.songs.groupBy { it.album }.keys.forEachIndexed { index, album ->
                if (index < 4) { albums.add(album) }
            }

            else -> {}
        }

        // Fetch our cover art using AlbumArtFetcher, as that respects any settings and is
        // generally resilient to frustrating MediaStore issues
        val results = mutableListOf<SourceResult>()
        val artFetcher = AlbumArtFetcher(context)

        // Load MediaStore streams
        albums.forEach { album ->
            try {
                results.add(artFetcher.fetch(pool, album, OriginalSize, options) as SourceResult)
            } catch (e: Exception) {
                // Whatever.
            }
        }

        // If so many fetches failed that there's not enough images to make a mosaic, then
        // just return the first cover image.
        if (results.size < 4) {
            // Dont even bother if ALL the streams have failed.
            check(results.isNotEmpty()) { "All streams have failed. " }

            return results[0]
        }

        val bitmap = drawMosaic(results)

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
    private fun drawMosaic(results: List<SourceResult>): Bitmap {
        // Use a fixed 512x512 canvas for the mosaics. Preferably we would adapt this mosaic to
        // target ImageView size, but Coil seems to start image loading before we can even get
        // a width/height for the view, making that impractical.
        val mosaicBitmap = Bitmap.createBitmap(
            MOSAIC_BITMAP_SIZE, MOSAIC_BITMAP_SIZE, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(mosaicBitmap)

        var x = 0
        var y = 0

        // For each stream, create a bitmap scaled to 1/4th of the mosaics combined size
        // and place it on a corner of the canvas.
        results.forEach { result ->
            if (y == MOSAIC_BITMAP_SIZE) return@forEach

            val bitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeStream(result.source.inputStream()),
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

    override fun key(data: MusicParent): String = data.hashCode().toString()
    override fun handles(data: MusicParent) = data !is Album // Albums are not used here

    companion object {
        private const val MOSAIC_BITMAP_SIZE = 512
        private const val MOSAIC_BITMAP_INCREMENT = 256
    }
}
