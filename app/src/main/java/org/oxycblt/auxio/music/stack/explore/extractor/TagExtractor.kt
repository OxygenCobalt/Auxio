/*
 * Copyright (c) 2023 Auxio Project
 * TagExtractor.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.stack.explore.extractor

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.HandlerThread
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.MetadataRetriever
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.TrackGroupArray
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.guava.asDeferred
import org.oxycblt.auxio.music.stack.explore.AudioFile
import org.oxycblt.auxio.music.stack.explore.DeviceFile

interface TagExtractor {
    fun extract(deviceFiles: Flow<DeviceFile>): Flow<AudioFile>
}

class TagExtractorImpl
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val mediaSourceFactory: MediaSource.Factory,
) : TagExtractor {
    override fun extract(deviceFiles: Flow<DeviceFile>) = flow {
        val thread = HandlerThread("TagExtractor:${hashCode()}")
        deviceFiles.collect { deviceFile ->
            val exoPlayerMetadataFuture =
                MetadataRetriever.retrieveMetadata(
                    mediaSourceFactory, MediaItem.fromUri(deviceFile.uri), thread)
            val mediaMetadataRetriever = MediaMetadataRetriever()
            mediaMetadataRetriever.setDataSource(context, deviceFile.uri)
            val exoPlayerMetadata = exoPlayerMetadataFuture.asDeferred().await()
            val result = extractTags(deviceFile, exoPlayerMetadata, mediaMetadataRetriever)
            mediaMetadataRetriever.close()
            emit(result)
        }
    }

    private fun extractTags(
        input: DeviceFile,
        output: TrackGroupArray,
        retriever: MediaMetadataRetriever
    ): AudioFile {
        if (output.isEmpty) return defaultAudioFile(input, retriever)
        val track = output.get(0)
        if (track.length == 0) return defaultAudioFile(input, retriever)
        val format = track.getFormat(0)
        val metadata = format.metadata ?: return defaultAudioFile(input, retriever)
        val textTags = TextTags(metadata)
        return AudioFile(
            deviceFile = input,
            durationMs =
                need(
                    retriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLong(),
                    "duration"),
            replayGainTrackAdjustment = textTags.replayGainTrackAdjustment(),
            replayGainAlbumAdjustment = textTags.replayGainAlbumAdjustment(),
            musicBrainzId = textTags.musicBrainzId(),
            name = need(textTags.name() ?: input.path.name, "name"),
            sortName = textTags.sortName(),
            track = textTags.track(),
            disc = textTags.disc(),
            subtitle = textTags.subtitle(),
            date = textTags.date(),
            albumMusicBrainzId = textTags.albumMusicBrainzId(),
            albumName = textTags.albumName(),
            albumSortName = textTags.albumSortName(),
            releaseTypes = textTags.releaseTypes() ?: listOf(),
            artistMusicBrainzIds = textTags.artistMusicBrainzIds() ?: listOf(),
            artistNames = textTags.artistNames() ?: listOf(),
            artistSortNames = textTags.artistSortNames() ?: listOf(),
            albumArtistMusicBrainzIds = textTags.albumArtistMusicBrainzIds() ?: listOf(),
            albumArtistNames = textTags.albumArtistNames() ?: listOf(),
            albumArtistSortNames = textTags.albumArtistSortNames() ?: listOf(),
            genreNames = textTags.genreNames() ?: listOf())
    }

    private fun defaultAudioFile(
        deviceFile: DeviceFile,
        metadataRetriever: MediaMetadataRetriever
    ) =
        AudioFile(
            deviceFile,
            name = need(deviceFile.path.name, "name"),
            durationMs =
                need(
                    metadataRetriever
                        .extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLong(),
                    "duration"),
        )

    private fun <T> need(a: T, called: String) =
        requireNotNull(a) { "Invalid tag, missing $called" }
}
