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
 
package org.oxycblt.auxio.music

import android.content.Context
import android.webkit.MimeTypeMap
import com.google.android.exoplayer2.util.MimeTypes
import org.oxycblt.auxio.R

/**
 * Represents a path to a file from the android file-system. Intentionally designed to be
 * version-agnostic and follow modern storage recommendations.
 */
data class Path(val name: String, val parent: Dir)

/**
 * Represents a directory from the android file-system. Intentionally designed to be
 * version-agnostic and follow modern storage recommendations.
 */
sealed class Dir {
    /**
     * An absolute path.
     *
     * This is only used with [Song] instances on pre-Q android versions. This should be avoided in
     * most cases for [Relative].
     */
    data class Absolute(val path: String) : Dir()

    /**
     * A directory with a volume.
     *
     * This data structure is not version-specific:
     * - With excluded directories, it is the only path that is used. On versions that do not
     * support path, [Volume.Primary] is used.
     * - On songs, this is version-specific. It will only appear on versions that support it.
     */
    data class Relative(val volume: Volume, val relativePath: String) : Dir()

    sealed class Volume {
        object Primary : Volume()
        data class Secondary(val name: String) : Volume()
    }

    fun resolveName(context: Context) =
        when (this) {
            is Absolute -> path
            is Relative ->
                when (volume) {
                    is Volume.Primary -> context.getString(R.string.fmt_primary_path, relativePath)
                    is Volume.Secondary ->
                        context.getString(R.string.fmt_secondary_path, relativePath)
                }
        }
}

/**
 * Represents a mime type as it is loaded by Auxio. [fromExtension] is based on the file extension
 * should always exist, while [fromFormat] is based on the file itself and may not be available.
 * @author OxygenCobalt
 */
data class MimeType(val fromExtension: String, val fromFormat: String?) {
    fun resolveName(context: Context): String {
        // We try our best to produce a more readable name for the common audio formats.
        val formatName =
            when (fromFormat) {
                // We start with the extracted mime types, as they are more consistent. Note that
                // we do not include container formats at all with these names. It is only the
                // inner codec that we show.
                MimeTypes.AUDIO_MPEG,
                MimeTypes.AUDIO_MPEG_L1,
                MimeTypes.AUDIO_MPEG_L2 -> R.string.cdc_mp3
                MimeTypes.AUDIO_AAC -> R.string.cdc_aac
                MimeTypes.AUDIO_VORBIS -> R.string.cdc_vorbis
                MimeTypes.AUDIO_OPUS -> R.string.cdc_opus
                MimeTypes.AUDIO_FLAC -> R.string.cdc_flac
                MimeTypes.AUDIO_WAV -> R.string.cdc_wav

                // We don't give a name to more unpopular formats.

                else -> -1
            }

        if (formatName > -1) {
            return context.getString(formatName)
        }

        // Fall back to the file extension in the case that we have no mime type or
        // a useless "audio/raw" mime type. Here:
        // - We return names for container formats instead of the inner format, as we
        // cannot parse the file.
        // - We are at the mercy of the Android OS, hence we check for every possible mime
        // type for a particular format.
        val extensionName =
            when (fromExtension) {
                "audio/mpeg",
                "audio/mp3" -> R.string.cdc_mp3
                "audio/mp4",
                "audio/mp4a-latm",
                "audio/mpeg4-generic" -> R.string.cdc_mp4
                "audio/aac",
                "audio/aacp",
                "audio/3gpp",
                "audio/3gpp2" -> R.string.cdc_aac
                "audio/ogg",
                "application/ogg",
                "application/x-ogg" -> R.string.cdc_ogg
                "audio/flac" -> R.string.cdc_flac
                "audio/wav",
                "audio/x-wav",
                "audio/wave",
                "audio/vnd.wave" -> R.string.cdc_wav
                "audio/x-matroska" -> R.string.cdc_mka
                else -> -1
            }

        return if (extensionName > -1) {
            context.getString(extensionName)
        } else {
            // Fall back to the extension if we can't find a special name for this format.
            MimeTypeMap.getSingleton().getExtensionFromMimeType(fromExtension)?.uppercase()
                ?: context.getString(R.string.def_codec)
        }
    }
}
