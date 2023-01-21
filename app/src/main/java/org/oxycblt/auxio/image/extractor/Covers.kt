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
 
package org.oxycblt.auxio.image.extractor

import android.content.Context
import android.media.MediaMetadataRetriever
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.MetadataRetriever
import com.google.android.exoplayer2.metadata.flac.PictureFrame
import com.google.android.exoplayer2.metadata.id3.ApicFrame
import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.image.CoverMode
import org.oxycblt.auxio.image.ImageSettings
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

/**
 * Internal utilities for loading album covers.
 * @author Alexander Capehart (OxygenCobalt).
 */
object Covers {
    /**
     * Fetch an album cover, respecting the current cover configuration.
     * @param context [Context] required to load the image.
     * @param album [Album] to load the cover from.
     * @return An [InputStream] of image data if the cover loading was successful, null if the cover
     * loading failed or should not occur.
     */
    suspend fun fetch(context: Context, album: Album): InputStream? {
        return try {
            when (ImageSettings.from(context).coverMode) {
                CoverMode.OFF -> null
                CoverMode.MEDIA_STORE -> fetchMediaStoreCovers(context, album)
                CoverMode.QUALITY -> fetchQualityCovers(context, album)
            }
        } catch (e: Exception) {
            logW("Unable to extract album cover due to an error: $e")
            null
        }
    }

    /**
     * Load an [Album] cover directly from one of it's Song files. This attempts the following in
     * order:
     * - [MediaMetadataRetriever], as it has the best support and speed.
     * - ExoPlayer's [MetadataRetriever], as some devices (notably Samsung) can have broken
     * [MediaMetadataRetriever] implementations.
     * - MediaStore, as a last-ditch fallback if the format is really obscure.
     *
     * @param context [Context] required to load the image.
     * @param album [Album] to load the cover from.
     * @return An [InputStream] of image data if the cover loading was successful, null otherwise.
     */
    private suspend fun fetchQualityCovers(context: Context, album: Album) =
        fetchAospMetadataCovers(context, album)
            ?: fetchExoplayerCover(context, album) ?: fetchMediaStoreCovers(context, album)

    /**
     * Loads an album cover with [MediaMetadataRetriever].
     * @param context [Context] required to load the image.
     * @param album [Album] to load the cover from.
     * @return An [InputStream] of image data if the cover loading was successful, null otherwise.
     */
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

    /**
     * Loads an [Album] cover with ExoPlayer's [MetadataRetriever].
     * @param context [Context] required to load the image.
     * @param album [Album] to load the cover from.
     * @return An [InputStream] of image data if the cover loading was successful, null otherwise.
     */
    private suspend fun fetchExoplayerCover(context: Context, album: Album): InputStream? {
        val uri = album.songs[0].uri
        val future = MetadataRetriever.retrieveMetadata(context, MediaItem.fromUri(uri))

        // future.get is a blocking call that makes us spin until the future is done.
        // This is bad for a co-routine, as it prevents cancellation and by extension
        // messes with the image loading process and causes annoying bugs.
        // To fix this we wrap this around in a withContext call to make it suspend and make
        // sure that the runner can do other coroutines.
        @Suppress("BlockingMethodInNonBlockingContext")
        val tracks =
            withContext(Dispatchers.Default) {
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

    /**
     * Loads an [Album] cover from MediaStore.
     * @param context [Context] required to load the image.
     * @param album [Album] to load the cover from.
     * @return An [InputStream] of image data if the cover loading was successful, null otherwise.
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun fetchMediaStoreCovers(context: Context, album: Album): InputStream? {
        // Eliminate any chance that this blocking call might mess up the loading process
        return withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(album.coverUri)
        }
    }
}
