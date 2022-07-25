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
 
package org.oxycblt.auxio.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import android.util.Size as AndroidSize
import androidx.core.graphics.drawable.toDrawable
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.size.Dimension
import coil.size.Size
import coil.size.pxOrElse
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.MetadataRetriever
import com.google.android.exoplayer2.metadata.flac.PictureFrame
import com.google.android.exoplayer2.metadata.id3.ApicFrame
import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.source
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.settings.Settings
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

/**
 * The base implementation for all image fetchers in Auxio.
 * @author OxygenCobalt
 *
 * TODO: File-system derived images [cover.jpg, Artist Images]
 */
abstract class BaseFetcher : Fetcher {
    /**
     * Fetch the artwork of an [album]. This call respects user configuration and has proper
     * redundancy in the case that metadata fails to load.
     */
    protected suspend fun fetchArt(context: Context, album: Album): InputStream? {
        val settings = Settings(context)

        if (!settings.showCovers) {
            return null
        }

        return try {
            if (settings.useQualityCovers) {
                fetchQualityCovers(context, album)
            } else {
                fetchMediaStoreCovers(context, album)
            }
        } catch (e: Exception) {
            logW("Unable to extract album cover due to an error: $e")
            null
        }
    }

    private suspend fun fetchQualityCovers(context: Context, album: Album) =
        // Loading quality covers basically means to parse the file metadata ourselves
        // and then extract the cover.

        // First try MediaMetadataRetriever. We will always do this first, as it supports
        // a variety of formats, has multiple levels of fault tolerance, and is pretty fast
        // for a manual parser.
        // However, Samsung seems to cripple this class as to force people to use their ad-infested
        // music app which relies on proprietary OneUI extensions instead of AOSP. That means
        // we have to add even more layers of redundancy to make sure we can extract a cover.
        // Thanks Samsung. Prick.
        fetchAospMetadataCovers(context, album)
            ?: fetchExoplayerCover(context, album) ?: fetchMediaStoreCovers(context, album)

    private fun fetchAospMetadataCovers(context: Context, album: Album): InputStream? {
        MediaMetadataRetriever().apply {
            // This call is time-consuming but it also doesn't seem to hold up the main thread,
            // so it's probably fine not to wrap it.rmt
            setDataSource(context, album.songs[0].uri)

            // Get the embedded picture from MediaMetadataRetriever, which will return a full
            // ByteArray of the cover without any compression artifacts.
            // If its null [i.e there is no embedded cover], than just ignore it and move on
            return embeddedPicture?.let { ByteArrayInputStream(it) }.also { release() }
        }
    }

    private suspend fun fetchExoplayerCover(context: Context, album: Album): InputStream? {
        val uri = album.songs[0].uri
        val future = MetadataRetriever.retrieveMetadata(context, MediaItem.fromUri(uri))

        // future.get is a blocking call that makes us spin until the future is done.
        // This is bad for a co-routine, as it prevents cancellation and by extension
        // messes with the image loading process and causes frustrating bugs.
        // To fix this we wrap this around in a withContext call to make it suspend and make
        // sure that the runner can do other coroutines.
        @Suppress("BlockingMethodInNonBlockingContext")
        val tracks =
            withContext(Dispatchers.IO) {
                try {
                    future.get()
                } catch (e: Exception) {
                    null
                }
            }

        if (tracks == null || tracks.isEmpty) {
            // Unrecognized format. This is expected, as ExoPlayer only supports a
            // subset of formats.
            return null
        }

        // The metadata extraction process of ExoPlayer results in a dump of all metadata
        // it found, which must be iterated through.
        val metadata = tracks[0].getFormat(0).metadata

        if (metadata == null || metadata.length() == 0) {
            // No (parsable) metadata. This is also expected.
            return null
        }

        var stream: ByteArrayInputStream? = null

        for (i in 0 until metadata.length()) {
            // We can only extract pictures from two tags with this method, ID3v2's APIC or
            // Vorbis picture comments.
            val pic: ByteArray?
            val type: Int

            when (val entry = metadata.get(i)) {
                is ApicFrame -> {
                    pic = entry.pictureData
                    type = entry.pictureType
                }
                is PictureFrame -> {
                    pic = entry.pictureData
                    type = entry.pictureType
                }
                else -> continue
            }

            // Ensure the picture type here is a front cover image so that we don't extract
            // an incorrect cover image.
            // Yes, this does add some latency, but its quality covers so we can prioritize
            // correctness over speed.
            if (type == MediaMetadata.PICTURE_TYPE_FRONT_COVER) {
                logD("Front cover successfully found")

                // We have a front cover image. Great.
                stream = ByteArrayInputStream(pic)
                break
            } else if (stream != null) {
                // In the case a front cover is not found, use the first image in the tag instead.
                // This can be corrected later on if a front cover frame is found.
                logW("No front cover image, using image of type $type instead")
                stream = ByteArrayInputStream(pic)
            }
        }

        return stream
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun fetchMediaStoreCovers(context: Context, data: Album): InputStream? {
        val uri = data.coverUri

        // Eliminate any chance that this blocking call might mess up the loading process
        return withContext(Dispatchers.IO) { context.contentResolver.openInputStream(uri) }
    }

    /**
     * Create a mosaic image from multiple streams of image data, Code adapted from Phonograph
     * https://github.com/kabouzeid/Phonograph
     */
    protected suspend fun createMosaic(
        context: Context,
        streams: List<InputStream>,
        size: Size
    ): FetchResult? {
        if (streams.size < 4) {
            return streams.firstOrNull()?.let { stream ->
                return SourceResult(
                    source = ImageSource(stream.source().buffer(), context),
                    mimeType = null,
                    dataSource = DataSource.DISK)
            }
        }

        // Use whatever size coil gives us to create the mosaic, rounding it to even so that we
        // get a symmetrical mosaic [and to prevent bugs]. If there is no size, default to a
        // 512x512 mosaic.
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

            // Run the bitmap through a transform to make sure it's a square of the desired
            // resolution.
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

    private fun Dimension.mosaicSize(): Int {
        val size = pxOrElse { 512 }
        return if (size.mod(2) > 0) size + 1 else size
    }
}
