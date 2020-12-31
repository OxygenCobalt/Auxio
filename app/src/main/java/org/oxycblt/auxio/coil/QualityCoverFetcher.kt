package org.oxycblt.auxio.coil

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import androidx.core.graphics.drawable.toDrawable
import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.size.Size
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toURI
import java.lang.Exception

/**
 * A [Fetcher] for fetching high-quality embedded covers instead of the compressed covers, albeit
 * at the cost of load time & caching.
 */
class QualityCoverFetcher(private val context: Context) : Fetcher<Song> {
    override suspend fun fetch(
        pool: BitmapPool,
        data: Song,
        size: Size,
        options: Options
    ): FetchResult {
        val extractor = MediaMetadataRetriever()

        extractor.setDataSource(context, data.id.toURI())

        val bitmap = try {
            val bytes = extractor.embeddedPicture

            if (bytes != null) {
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } else {
                throw Exception()
            }
        } catch (e: Exception) {
            extractor.release()
            error("Likely no album art for this item.")
        } finally {
            extractor.release()
        }

        return DrawableResult(
            drawable = bitmap.toDrawable(context.resources),
            isSampled = false,
            dataSource = DataSource.DISK
        )
    }

    override fun key(data: Song): String = data.album.id.toString()
}
