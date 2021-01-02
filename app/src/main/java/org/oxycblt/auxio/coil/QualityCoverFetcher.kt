package org.oxycblt.auxio.coil

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.size.Size
import okio.buffer
import okio.source
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toURI
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * A [Fetcher] for fetching high-quality embedded covers instead of the compressed covers, albeit
 * at the cost of load time & memory usage.
 */
class QualityCoverFetcher(private val context: Context) : Fetcher<Song> {
    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun fetch(
        pool: BitmapPool,
        data: Song,
        size: Size,
        options: Options
    ): FetchResult {
        val extractor = MediaMetadataRetriever()
        val stream: InputStream?
        val uri: Uri

        extractor.use { ext ->
            ext.setDataSource(context, data.id.toURI())
            val cover = ext.embeddedPicture

            stream = if (cover != null) {
                uri = data.id.toURI()

                ByteArrayInputStream(cover)
            } else {
                // Fallback to the compressed cover if the cover loading failed.
                uri = data.album.coverUri

                // Blocking call, but coil is on a background thread so it doesn't matter
                context.contentResolver.openInputStream(data.album.coverUri)
            }

            stream?.use {
                return SourceResult(
                    source = stream.source().buffer(),
                    mimeType = context.contentResolver.getType(uri),
                    dataSource = DataSource.DISK
                )
            }
        }

        // If we are here, the extractor likely failed so instead attempt to return the compressed
        // cover instead.
        context.contentResolver.openInputStream(data.album.coverUri)?.use { str ->
            return SourceResult(
                source = str.source().buffer(),
                mimeType = context.contentResolver.getType(uri),
                dataSource = DataSource.DISK
            )
        }

        // If even that failed, then error out entirely.
        error("Likely no bitmap for this song/album.")
    }

    // Group bitmaps by their album so that caching is more efficent
    override fun key(data: Song): String = data.album.id.toString()
}
