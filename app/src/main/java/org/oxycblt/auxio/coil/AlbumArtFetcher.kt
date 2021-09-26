/*
 * Copyright (c) 2021 Auxio Project
 * AlbumArtFetcher.kt is part of Auxio.
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
import android.media.MediaMetadataRetriever
import coil.bitmap.BitmapPool
import coil.decode.DataSource
import coil.decode.Options
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.size.Size
import okio.buffer
import okio.source
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.toAlbumArtURI
import org.oxycblt.auxio.music.toURI
import org.oxycblt.auxio.settings.SettingsManager
import java.io.ByteArrayInputStream

/**
 * Fetcher that returns the album art for a given [Album]. Handles settings on whether to use
 * quality covers or not.
 * @author OxygenCobalt
 */
class AlbumArtFetcher(private val context: Context) : Fetcher<Album> {
    private val settingsManager = SettingsManager.getInstance()

    override suspend fun fetch(
        pool: BitmapPool,
        data: Album,
        size: Size,
        options: Options
    ): FetchResult {
        return if (settingsManager.useQualityCovers) {
            loadQualityCovers(data)
        } else {
            loadMediaStoreCovers(data)
        }
    }

    private fun loadMediaStoreCovers(data: Album): SourceResult {
        val uri = data.id.toAlbumArtURI()
        val stream = context.contentResolver.openInputStream(uri)

        if (stream != null) {
            // Don't close the stream here as it will cause an error later from an attempted read.
            // This stream still seems to close itself at some point, so its fine.
            return SourceResult(
                source = stream.source().buffer(),
                mimeType = context.contentResolver.getType(uri),
                dataSource = DataSource.DISK
            )
        }

        error("No cover art for album ${data.name}")
    }

    private fun loadQualityCovers(data: Album): SourceResult {
        val extractor = MediaMetadataRetriever()

        extractor.use { ext ->
            val songUri = data.songs[0].id.toURI()
            ext.setDataSource(context, songUri)

            // Get the embedded picture from MediaMetadataRetriever, which will return a full
            // ByteArray of the cover without any compression artifacts.
            // If its null [a.k.a there is no embedded cover], than just ignore it and move on
            ext.embeddedPicture?.let { coverBytes ->
                val stream = ByteArrayInputStream(coverBytes)

                return SourceResult(
                    source = stream.source().buffer(),
                    mimeType = context.contentResolver.getType(songUri),
                    dataSource = DataSource.DISK
                )
            }
        }

        // If we are here, the extractor likely failed so instead attempt to return the compressed
        // cover instead.
        return loadMediaStoreCovers(data)
    }

    override fun key(data: Album) = data.id.toString()
}
