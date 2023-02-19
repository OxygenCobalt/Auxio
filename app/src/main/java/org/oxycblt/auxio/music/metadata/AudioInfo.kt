/*
 * Copyright (c) 2023 Auxio Project
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
 
package org.oxycblt.auxio.music.metadata

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.storage.MimeType
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE
import org.oxycblt.auxio.util.logW

/**
 * The properties of a [Song]'s file.
 * @param bitrateKbps The bit rate, in kilobytes-per-second. Null if it could not be parsed.
 * @param sampleRateHz The sample rate, in hertz.
 * @param resolvedMimeType The known mime type of the [Song] after it's file format was determined.
 * @author Alexander Capehart (OxygenCobalt)
 */
data class AudioInfo(
    val bitrateKbps: Int?,
    val sampleRateHz: Int?,
    val resolvedMimeType: MimeType
) {
    /** Implements the process of extracting [AudioInfo] from a given [Song]. */
    interface Provider {
        /**
         * Extract the [AudioInfo] of a given [Song].
         * @param song The [Song] to read.
         * @return The [AudioInfo] of the [Song], if possible to obtain.
         */
        suspend fun extract(song: Song): AudioInfo
    }
}

/**
 * A framework-backed implementation of [AudioInfo.Provider].
 * @param context [Context] required to read audio files.
 */
class AudioInfoProviderImpl @Inject constructor(@ApplicationContext private val context: Context) :
    AudioInfo.Provider {

    override suspend fun extract(song: Song): AudioInfo {
        // While we would use ExoPlayer to extract this information, it doesn't support
        // common data like bit rate in progressive data sources due to there being no
        // demand. Thus, we are stuck with the inferior OS-provided MediaExtractor.
        val extractor = MediaExtractor()

        try {
            extractor.setDataSource(context, song.uri, emptyMap())
        } catch (e: Exception) {
            // Can feasibly fail with invalid file formats. Note that this isn't considered
            // an error condition in the UI, as there is still plenty of other song information
            // that we can show.
            logW("Unable to extract song attributes.")
            logW(e.stackTraceToString())
            return AudioInfo(null, null, song.mimeType)
        }

        // Get the first track from the extractor (This is basically always the only
        // track we need to analyze).
        val format = extractor.getTrackFormat(0)

        // Accessing fields can throw an exception if the fields are not present, and
        // the new method for using default values is not available on lower API levels.
        // So, we are forced to handle the exception and map it to a saner null value.
        val bitrate =
            try {
                // Convert bytes-per-second to kilobytes-per-second.
                format.getInteger(MediaFormat.KEY_BIT_RATE) / 1000
            } catch (e: NullPointerException) {
                logD("Unable to extract bit rate field")
                null
            }

        val sampleRate =
            try {
                format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            } catch (e: NullPointerException) {
                logE("Unable to extract sample rate field")
                null
            }

        val resolvedMimeType =
            if (song.mimeType.fromFormat != null) {
                // ExoPlayer was already able to populate the format.
                song.mimeType
            } else {
                // ExoPlayer couldn't populate the format somehow, populate it here.
                val formatMimeType =
                    try {
                        format.getString(MediaFormat.KEY_MIME)
                    } catch (e: NullPointerException) {
                        logE("Unable to extract mime type field")
                        null
                    }

                MimeType(song.mimeType.fromExtension, formatMimeType)
            }

        extractor.release()

        return AudioInfo(bitrate, sampleRate, resolvedMimeType)
    }
}
