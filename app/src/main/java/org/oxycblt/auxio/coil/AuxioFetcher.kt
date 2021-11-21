package org.oxycblt.auxio.coil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.media.MediaMetadataRetriever
import androidx.core.graphics.drawable.toDrawable
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.MetadataRetriever
import com.google.android.exoplayer2.metadata.flac.PictureFrame
import com.google.android.exoplayer2.metadata.id3.ApicFrame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.source
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.toAlbumArtURI
import org.oxycblt.auxio.music.toURI
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.util.logD
import java.io.ByteArrayInputStream
import java.io.InputStream

abstract class AuxioFetcher : Fetcher {
    private val settingsManager = SettingsManager.getInstance()

    protected suspend fun fetchArt(context: Context, album: Album): InputStream? {
        if (!settingsManager.showCovers) {
            return null
        }

        return if (settingsManager.useQualityCovers) {
            fetchQualityCovers(context, album)
        } else {
            fetchMediaStoreCovers(context, album)
        }
    }

    /**
     * Create a mosaic image from multiple streams of image data, Code adapted from Phonograph
     * https://github.com/kabouzeid/Phonograph
     */
    protected fun createMosaic(context: Context, streams: List<InputStream>): FetchResult? {
        logD("idiot")

        if (streams.size < 4) {
            return streams.getOrNull(0)?.let { stream ->
                return SourceResult(
                    source = ImageSource(stream.source().buffer(), context),
                    mimeType = null,
                    dataSource = DataSource.DISK
                )
            }
        }

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
        for (stream in streams) {
            if (y == MOSAIC_BITMAP_SIZE) {
                break
            }

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

        return DrawableResult(
            drawable = mosaicBitmap.toDrawable(context.resources),
            isSampled = false,
            dataSource = DataSource.DISK
        )
    }

    private suspend fun fetchQualityCovers(context: Context, album: Album): InputStream? {
        // Loading quality covers basically means to parse the file metadata ourselves
        // and then extract the cover.

        // First try MediaMetadataRetriever. We will always do this first, as it supports
        // a variety of formats, has multiple levels of fault tolerance, and is pretty fast
        // for a manual parser.
        // However, Samsung seems to cripple this class as to force people to use their ad-infested
        // music app which relies on proprietary OneUI extensions instead of AOSP. That means
        // we have to have another layer of redundancy to retain quality. Thanks samsung. Prick.
        val result = fetchAospMetadataCovers(context, album)

        if (result != null) {
            return result
        }

        // Our next fallback is to rely on ExoPlayer's largely half-baked and undocumented
        // metadata system.
        val exoResult = fetchExoplayerCover(context, album)

        if (exoResult != null) {
            return exoResult
        }

        // If the previous two failed, we resort to MediaStore's covers despite it literally
        // going against the point of this setting. The previous two calls are just too unreliable
        // and we can't do any filesystem traversing due to scoped storage.
        val mediaStoreResult = fetchMediaStoreCovers(context, album)

        if (mediaStoreResult != null) {
            return mediaStoreResult
        }

        // There is no cover we could feasibly fetch. Give up.
        return null
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun fetchMediaStoreCovers(context: Context, data: Album): InputStream? {
        val uri = data.id.toAlbumArtURI()

        // Eliminate any chance that this blocking call might mess up the cancellation process
        return withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri)
        }
    }

    private suspend fun fetchAospMetadataCovers(context: Context, album: Album): InputStream? {
        val extractor = MediaMetadataRetriever()

        extractor.use { ext ->
            // To be safe, just make sure that this blocking call is wrapped so it doesn't
            // cause problems
            ext.setDataSource(context, album.songs[0].id.toURI())

            // Get the embedded picture from MediaMetadataRetriever, which will return a full
            // ByteArray of the cover without any compression artifacts.
            // If its null [a.k.a there is no embedded cover], than just ignore it and move on
            return ext.embeddedPicture?.let { coverBytes ->
                ByteArrayInputStream(coverBytes)
            }
        }
    }

    private suspend fun fetchExoplayerCover(context: Context, album: Album): InputStream? {
        val uri = album.songs[0].id.toURI()

        val future = MetadataRetriever.retrieveMetadata(
            context, MediaItem.fromUri(uri)
        )

        // future.get is a blocking call that makes the us spin until the future is done.
        // This is bad for a co-routine, as it prevents cancellation and by extension
        // messes with the image loading process and causes frustrating bugs.
        // To fix this we wrap this around in a withContext call to make it suspend and make
        // sure that the runner can do other coroutines.
        @Suppress("BlockingMethodInNonBlockingContext")
        val tracks = withContext(Dispatchers.IO) {
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

        // The metadata extraction process of ExoPlayer is normalized into a superclass.
        // That means we have to iterate through and find the cover art ourselves.
        val metadata = tracks[0].getFormat(0).metadata

        if (metadata == null || metadata.length() == 0) {
            // No (parsable) metadata. This is also expected.
            return null
        }

        var stream: ByteArrayInputStream? = null

        for (i in 0 until metadata.length()) {
            // We can only extract pictures from two tags with this method, ID3v2's APIC or
            // FLAC's PICTURE.
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
                logD("Image not a front cover, assigning image of type $type for now")

                stream = ByteArrayInputStream(pic)
            }
        }

        return stream
    }

    companion object {
        private const val MOSAIC_BITMAP_SIZE = 512
        private const val MOSAIC_BITMAP_INCREMENT = 256
    }
}
