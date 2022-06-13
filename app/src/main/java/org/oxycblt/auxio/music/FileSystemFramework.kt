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
        // While it would be nice to have more refined mime type names (Layer I/II/III, containers,
        // etc.), it's not exactly practical with ExoPlayer. So, we go for the next best thing
        // and try to find a good enough readable name to use.
        val readableStringRes =
            if (fromFormat != null && fromFormat != MimeTypes.AUDIO_RAW) {
                // Prefer the extracted mime type, as it properly handles container formats and
                // is agnostic to the file extension
                when (fromFormat) {
                    MimeTypes.AUDIO_MPEG,
                    MimeTypes.AUDIO_MPEG_L1,
                    MimeTypes.AUDIO_MPEG_L2 -> R.string.cdc_mp3
                    MimeTypes.AUDIO_AAC -> R.string.cdc_mp4
                    MimeTypes.AUDIO_VORBIS -> R.string.cdc_ogg_vorbis
                    MimeTypes.AUDIO_OPUS -> R.string.cdc_ogg_opus
                    MimeTypes.AUDIO_WAV -> R.string.cdc_wav
                    else -> -1
                }
            } else {
                // Fall back to the file extension in the case that we have a useless
                when (fromExtension) {
                    "audio/mpeg" -> R.string.cdc_mp3
                    "audio/mp4" -> R.string.cdc_mp4
                    "audio/ogg" -> R.string.cdc_ogg
                    "audio/x-wav" -> R.string.cdc_wav
                    "audio/x-matroska" -> R.string.cdc_mka
                    else -> -1
                }
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
