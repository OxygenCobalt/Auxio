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
 
package org.oxycblt.auxio.music.excluded

import android.os.Build
import java.io.File
import org.oxycblt.auxio.util.logW

/**
 * Represents a directory excluded from the music loading process. This is a in-code representation
 * of a typical document tree URI scheme, designed to not only provide support for external volumes,
 * but also provide it in a way compatible with older android versions.
 * @author OxygenCobalt
 */
data class ExcludedDirectory(val volume: Volume, val relativePath: String) {
    override fun toString(): String = "${volume}:$relativePath"

    sealed class Volume {
        object Primary : Volume() {
            override fun toString() = VOLUME_PRIMARY_NAME
        }

        data class Secondary(val name: String) : Volume() {
            override fun toString() = name
        }

        companion object {
            private const val VOLUME_PRIMARY_NAME = "primary"

            fun fromString(volume: String) =
                when (volume) {
                    VOLUME_PRIMARY_NAME -> Primary
                    else -> Secondary(volume)
                }
        }
    }

    companion object {
        fun fromString(dir: String): ExcludedDirectory? {
            val split = dir.split(File.pathSeparator, limit = 2)

            val volume = Volume.fromString(split.getOrNull(0) ?: return null)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && volume is Volume.Secondary) {
                // While Android Q provides a stable way of accessing volumes, we can't trust
                // that DATA provides a stable volume scheme on older versions, so external
                // volumes are not supported.
                logW("Cannot use secondary volumes below API 29")
                return null
            }

            val relativePath = split.getOrNull(1) ?: return null

            return ExcludedDirectory(volume, relativePath)
        }
    }
}
