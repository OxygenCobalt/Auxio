/*
 * Copyright (c) 2023 Auxio Project
 * CoverExtractor.kt is part of Auxio.
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
import android.media.MediaMetadataRetriever
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.MetadataRetriever
import com.google.android.exoplayer2.metadata.flac.PictureFrame
import com.google.android.exoplayer2.metadata.id3.ApicFrame
import com.google.android.exoplayer2.source.MediaSource
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.asDeferred
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.image.CoverMode
import org.oxycblt.auxio.image.ImageSettings
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

class CoverExtractor
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val imageSettings: ImageSettings,
    private val mediaSourceFactory: MediaSource.Factory
) {

    suspend fun extract(album: Album): InputStream? =
        try {
            when (imageSettings.coverMode) {
                CoverMode.OFF -> null
                CoverMode.MEDIA_STORE -> extractMediaStoreCover(album)
                CoverMode.QUALITY -> extractQualityCover(album)
            }
        } catch (e: Exception) {
            logW("Unable to extract album cover due to an error: $e")
            null
        }

    private suspend fun extractQualityCover(album: Album) =
        extractAospMetadataCover(album)
            ?: extractExoplayerCover(album) ?: extractMediaStoreCover(album)

    private fun extractAospMetadataCover(album: Album): InputStream? =
        MediaMetadataRetriever().run {
            // This call is time-consuming but it also doesn't seem to hold up the main thread,
            // so it's probably fine not to wrap it.rmt
            setDataSource(context, album.songs[0].uri)

            // Get the embedded picture from MediaMetadataRetriever, which will return a full
            // ByteArray of the cover without any compression artifacts.
            // If its null [i.e there is no embedded cover], than just ignore it and move on
            return embeddedPicture?.let { ByteArrayInputStream(it) }.also { release() }
        }

    private suspend fun extractExoplayerCover(album: Album): InputStream? {
        val tracks =
            MetadataRetriever.retrieveMetadata(
                    mediaSourceFactory, MediaItem.fromUri(album.songs[0].uri))
                .asDeferred()
                .await()

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

            if (type == MediaMetadata.PICTURE_TYPE_FRONT_COVER) {
                logD("Front cover found")
                stream = ByteArrayInputStream(pic)
                break
            } else if (stream == null) {
                stream = ByteArrayInputStream(pic)
            }
        }

        return stream
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun extractMediaStoreCover(album: Album) =
        // Eliminate any chance that this blocking call might mess up the loading process
        withContext(Dispatchers.IO) { context.contentResolver.openInputStream(album.coverUri) }
}
