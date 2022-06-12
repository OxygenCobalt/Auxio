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
        // Prefer the format mime type first, as it actually is derived from the file
        // and not the extension. Just make sure to ignore audio/raw, as that could feasibly
        // correspond to multiple formats.
        val readableMime =
            if (fromFormat != null && fromFormat != "audio/raw") {
                fromFormat
            } else {
                fromExtension
            }

        // We have special names for the most common formats.
        val readableStringRes =
            when (readableMime) {
                // MPEG formats
                // While MP4 is AAC, it's considered separate given how common it is.
                "audio/mpeg",
                "audio/mp3" -> R.string.cdc_mp3
                "audio/mp4",
                "audio/mp4a-latm",
                "audio/mpeg4-generic" -> R.string.cdc_mp4

                // Free formats
                // Generic Ogg is included here as it's actually formatted as "Ogg", not "OGG"
                "audio/ogg",
                "application/ogg" -> R.string.cdc_ogg
                "audio/vorbis" -> R.string.cdc_ogg_vorbis
                "audio/opus" -> R.string.cdc_ogg_opus
                "audio/flac" -> R.string.cdc_flac

                // The other AAC containers have a generic name
                "audio/aac",
                "audio/aacp",
                "audio/aac-adts",
                "audio/3gpp",
                "audio/3gpp2", -> R.string.cdc_aac

                // Windows formats
                "audio/wav",
                "audio/x-wav",
                "audio/wave",
                "audio/vnd.wave" -> R.string.cdc_wav
                "audio/x-ms-wma" -> R.string.cdc_wma

                // Don't know
                else -> -1
            }

        return if (readableStringRes > -1) {
            context.getString(readableStringRes)
        } else {
            // Fall back to the extension if we can't find a special name for this format.
            MimeTypeMap.getSingleton().getExtensionFromMimeType(fromExtension)?.uppercase()
                ?: context.getString(R.string.def_codec)
        }
    }
}
