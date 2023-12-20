/*
 * Copyright (c) 2023 Auxio Project
 * M3U.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.import

import org.oxycblt.auxio.music.fs.Components
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import org.oxycblt.auxio.music.fs.Path
import org.oxycblt.auxio.util.logW
import javax.inject.Inject

interface M3U {
    fun read(stream: InputStream, workingDirectory: Path): List<Path>?
}

class M3UImpl @Inject constructor() : M3U {
    override fun read(stream: InputStream, workingDirectory: Path): List<Path>? {
        val reader = BufferedReader(InputStreamReader(stream))
        val media = mutableListOf<Path>()

        consumeFile@ while (true) {
            collectMetadata@ while (true) {
                val line = reader.readLine() ?: break@consumeFile
                if (!line.startsWith("#")) {
                    break@collectMetadata
                }
            }

            val path = reader.readLine()
            if (path == null) {
                logW("Expected a path, instead got an EOF")
                break@consumeFile
            }

            val relativeComponents = Components.parse(path)
            val absoluteComponents =
                resolveRelativePath(relativeComponents, workingDirectory.components)

            media.add(Path(workingDirectory.volume, absoluteComponents))
        }

        return media.ifEmpty { null }
    }

    private fun resolveRelativePath(relative: Components, workingDirectory: Components): Components {
        var components = workingDirectory
        for (component in relative.components) {
            when (component) {
                ".." -> components = components.parent()
                "." -> {}
                else -> components = components.child(component)
            }
        }
        return components
    }
}
