/*
 * Copyright (c) 2024 Auxio Project
 * Format.kt is part of Auxio.
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
 
package org.oxycblt.musikr.fs

import android.webkit.MimeTypeMap
import org.oxycblt.auxio.util.unlikelyToBeNull

sealed interface Format {
    val mimeType: String

    data object MPEG3 : Format {
        override val mimeType = "audio/mpeg"
    }

    data class MPEG4(val containing: Format?) : Format {
        override val mimeType = "audio/mp4"
    }

    data object AAC : Format {
        override val mimeType = "audio/aac"
    }

    data object ALAC : Format {
        override val mimeType = "audio/alac"
    }

    data class Ogg(val containing: Format?) : Format {
        override val mimeType = "audio/ogg"
    }

    data object Opus : Format {
        override val mimeType = "audio/opus"
    }

    data object Vorbis : Format {
        override val mimeType = "audio/vorbis"
    }

    data object FLAC : Format {
        override val mimeType = "audio/flac"
    }

    data object Wav : Format {
        override val mimeType = "audio/wav"
    }

    data class Unknown(override val mimeType: String) : Format {
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)?.uppercase()
    }

    companion object {
        private val CODEC_MAP =
            mapOf(
                "audio/mpeg" to MPEG3,
                "audio/mp3" to MPEG3,
                "audio/aac" to AAC,
                "audio/aacp" to AAC,
                "audio/3gpp" to AAC,
                "audio/3gpp2" to AAC,
                "audio/alac" to ALAC,
                "audio/opus" to Opus,
                "audio/vorbis" to Vorbis,
                "audio/flac" to FLAC,
                "audio/wav" to Wav,
                "audio/raw" to Wav,
                "audio/x-wav" to Wav,
                "audio/vnd.wave" to Wav,
                "audio/wave" to Wav,
            )

        fun infer(containerMimeType: String, codecMimeType: String): Format {
            val codecFormat = CODEC_MAP[codecMimeType]
            if (codecFormat != null) {
                // Codec found, possibly wrap in container.
                return unlikelyToBeNull(wrapInContainer(containerMimeType, codecFormat))
            }
            val extensionFormat = CODEC_MAP[containerMimeType]
            if (extensionFormat != null) {
                // Standalone container of some codec.
                return extensionFormat
            }
            return wrapInContainer(containerMimeType, null) ?: Unknown(containerMimeType)
        }

        private fun wrapInContainer(containerMimeType: String, format: Format?) =
            when (containerMimeType) {
                "audio/mp4",
                "audio/mp4a-latm",
                "audio/mpeg4-generic" -> MPEG4(format)
                "audio/ogg",
                "application/ogg",
                "application/x-ogg" -> Ogg(format)
                else -> format
            }
    }
}
