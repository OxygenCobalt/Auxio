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
 
package org.oxycblt.auxio.music.dirs

import android.os.Build
import java.io.File
import org.oxycblt.auxio.music.Dir
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

data class MusicDirs(val dirs: List<Dir.Relative>, val shouldInclude: Boolean) {
    companion object {
        private const val VOLUME_PRIMARY_NAME = "primary"

        fun parseDir(dir: String): Dir.Relative? {
            logD("Parse from string $dir")

            val split = dir.split(File.pathSeparator, limit = 2)

            val volume =
                when (split[0]) {
                    VOLUME_PRIMARY_NAME -> Dir.Volume.Primary
                    else ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            Dir.Volume.Secondary(split[0])
                        } else {
                            // While Android Q provides a stable way of accessing volumes, we can't
                            // trust that DATA provides a stable volume scheme on older versions, so
                            // external volumes are not supported.
                            logW("Cannot use secondary volumes below Android 10")
                            return null
                        }
                }

            val relativePath = split.getOrNull(1) ?: return null

            return Dir.Relative(volume, relativePath)
        }

        fun toDir(dir: Dir.Relative): String {
            val volume =
                when (dir.volume) {
                    is Dir.Volume.Primary -> VOLUME_PRIMARY_NAME
                    is Dir.Volume.Secondary -> dir.volume.name
                }

            return "${volume}:${dir.relativePath}"
        }
    }
}
