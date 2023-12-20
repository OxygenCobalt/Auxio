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
 
package org.oxycblt.auxio.music.external

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import org.oxycblt.auxio.music.fs.Components
import org.oxycblt.auxio.music.fs.Path
import org.oxycblt.auxio.music.metadata.correctWhitespace
import org.oxycblt.auxio.util.logW

/**
 * Minimal M3U file format implementation.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface M3U {
    /**
     * Reads an M3U file from the given [stream] and returns a [ImportedPlaylist] containing the
     * paths to the files listed in the M3U file.
     *
     * @param stream The stream to read the M3U file from.
     * @param workingDirectory The directory that the M3U file is contained in. This is used to
     *   resolve relative paths.
     * @return An [ImportedPlaylist] containing the paths to the files listed in the M3U file,
     */
    fun read(stream: InputStream, workingDirectory: Path): ImportedPlaylist?
}

class M3UImpl @Inject constructor() : M3U {
    override fun read(stream: InputStream, workingDirectory: Path): ImportedPlaylist? {
        val reader = BufferedReader(InputStreamReader(stream))
        val paths = mutableListOf<Path>()
        var name: String? = null

        consumeFile@ while (true) {
            var path: String?
            collectMetadata@ while (true) {
                // The M3U format consists of "entries" that begin with a bunch of metadata
                // prefixed with "#", and then a relative/absolute path or url to the file.
                // We don't really care about the metadata except for the playlist name, so
                // we discard everything but that.
                val currentLine =
                    (reader.readLine() ?: break@consumeFile).correctWhitespace()
                        ?: continue@collectMetadata
                if (currentLine.startsWith("#")) {
                    // Metadata entries are roughly structured
                    val split = currentLine.split(":", limit = 2)
                    when (split[0]) {
                        // Playlist name
                        "#PLAYLIST" -> name = split.getOrNull(1)?.correctWhitespace()
                        // Add more metadata handling here if needed.
                        else -> {}
                    }
                } else {
                    // Something that isn't a metadata entry, assume it's a path. It could be
                    // a URL, but it'll just get mangled really badly and not match with anything,
                    // so it's okay.
                    path = currentLine
                    break@collectMetadata
                }
            }

            if (path == null) {
                logW("Expected a path, instead got an EOF")
                break@consumeFile
            }

            // The path may be relative to the directory that the M3U file is contained in,
            // so we may need to resolve it into an absolute path before moving ahead.
            val relativeComponents = Components.parse(path)
            val absoluteComponents =
                resolveRelativePath(relativeComponents, workingDirectory.components)

            paths.add(Path(workingDirectory.volume, absoluteComponents))
        }

        return if (paths.isNotEmpty()) {
            ImportedPlaylist(name, paths)
        } else {
            // Couldn't get anything useful out of this file.
            null
        }
    }

    private fun resolveRelativePath(
        relative: Components,
        workingDirectory: Components
    ): Components {
        var components = workingDirectory
        for (component in relative.components) {
            when (component) {
                // Parent specifier, go "back" one directory (in practice cleave off the last
                // component)
                ".." -> components = components.parent()
                // Current directory, the components are already there.
                "." -> {}
                // New directory, add it
                else -> components = components.child(component)
            }
        }
        return components
    }
}
