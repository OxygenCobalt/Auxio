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
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MetadataRetriever
import com.google.android.exoplayer2.metadata.Metadata
import com.google.android.exoplayer2.metadata.flac.PictureFrame
import com.google.android.exoplayer2.metadata.id3.ApicFrame
import okio.buffer
import okio.source
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.toAlbumArtURI
import org.oxycblt.auxio.music.toURI
import org.oxycblt.auxio.settings.SettingsManager
import java.io.ByteArrayInputStream
import java.lang.Exception

/**
 * Fetcher that returns the album art for a given [Album]. Handles settings on whether to use
 * quality covers or not.
 * @author OxygenCobalt
 */
class AlbumArtFetcher(private val context: Context) : Fetcher<Album> {
    override suspend fun fetch(
        pool: BitmapPool,
        data: Album,
        size: Size,
        options: Options
    ): FetchResult {
        val settingsManager = SettingsManager.getInstance()

        val result = if (settingsManager.useQualityCovers) {
            fetchQualityCovers(data.songs[0])
        } else {
            // If we're fetching plain MediaStore covers, optimize for speed and don't go through
            // the wild goose chase that we do for quality covers.
            fetchMediaStoreCovers(data)
        }

        checkNotNull(result) {
            "No cover art was found for ${data.name}"
        }

        return result
    }

    private fun fetchQualityCovers(song: Song): FetchResult? {
        // Loading quality covers basically means to parse the file metadata ourselves
        // and then extract the cover.

        // First try MediaMetadataRetriever. We will always do this first, as it supports
        // a variety of formats, has multiple levels of fault tolerance, and is pretty fast
        // for a manual parser.
        // However, Samsung seems to cripple this class as to force people to use their ad-infested
        // music app which relies on proprietary OneUI extensions instead of AOSP. That means
        // we have to have another layer of redundancy to retain quality. Thanks samsung. Prick.
        val result = fetchAospMetadataCovers(song)

        if (result != null) {
            return result
        }

        // Our next fallback is to rely on ExoPlayer's largely half-baked and undocumented
        // metadata system.
        val exoResult = fetchExoplayerCover(song)

        if (exoResult != null) {
            return exoResult
        }

        // If the previous two failed, we resort to MediaStore's covers despite it literally
        // going against the point of this setting. The previous two calls are just too unreliable
        // and we can't do any filesystem traversing due to scoped storage.
        val mediaStoreResult = fetchMediaStoreCovers(song.album)

        if (mediaStoreResult != null) {
            return mediaStoreResult
        }

        // There is no cover we could feasibly fetch. Give up.
        return null
    }

    private fun fetchMediaStoreCovers(data: Album): FetchResult? {
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

        return null
    }

    private fun fetchAospMetadataCovers(song: Song): FetchResult? {
        val extractor = MediaMetadataRetriever()

        extractor.use { ext ->
            val songUri = song.id.toURI()
            ext.setDataSource(context, songUri)

            // Get the embedded picture from MediaMetadataRetriever, which will return a full
            // ByteArray of the cover without any compression artifacts.
            // If its null [a.k.a there is no embedded cover], than just ignore it and move on
            ext.embeddedPicture?.let { coverBytes ->
                val stream = ByteArrayInputStream(coverBytes)

                stream.use { stm ->
                    return SourceResult(
                        source = stm.source().buffer(),
                        mimeType = context.contentResolver.getType(songUri),
                        dataSource = DataSource.DISK
                    )
                }
            }
        }

        return null
    }

    private fun fetchExoplayerCover(song: Song): FetchResult? {
        val uri = song.id.toURI()

        val future = MetadataRetriever.retrieveMetadata(
            context, MediaItem.fromUri(song.id.toURI())
        )

        // Coil is async, we can just spin until the loading has ended
        while (future.isDone) { /* no-op */ }

        val tracks = try {
            future.get()
        } catch (e: Exception) {
            null
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
            val pic = when (val entry = metadata.get(i)) {
                is ApicFrame -> entry.pictureData
                is PictureFrame -> entry.pictureData
                else -> null
            }

            if (pic != null) {
                // We found a cover, great.
                // TODO: Make sure that this is a correct front cover picture and pick the first
                //       one if one cannot be found
                stream = ByteArrayInputStream(pic)
                break
            }
        }

        return stream?.use { stm ->
            return SourceResult(
                source = stm.source().buffer(),
                mimeType = context.contentResolver.getType(uri),
                dataSource = DataSource.DISK
            )
        }
    }

    override fun key(data: Album) = data.id.toString()
}
