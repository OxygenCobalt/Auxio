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

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import javax.inject.Inject
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.music.fs.Components
import org.oxycblt.auxio.music.fs.Path
import org.oxycblt.auxio.music.metadata.correctWhitespace
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.util.logE

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

    /**
     * Writes the given [playlist] to the given [outputStream] in the M3U format,.
     *
     * @param playlist The playlist to write.
     * @param outputStream The stream to write the M3U file to.
     * @param workingDirectory The directory that the M3U file is contained in. This is used to
     *   create relative paths to where the M3U file is assumed to be stored.
     */
    fun write(playlist: Playlist, outputStream: OutputStream, workingDirectory: Path)
}

class M3UImpl @Inject constructor(@ApplicationContext private val context: Context) : M3U {
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
                logE("Expected a path, instead got an EOF")
                break@consumeFile
            }

            // The path may be relative to the directory that the M3U file is contained in,
            // signified by either the typical ./ or the absence of any separator at all.
            // so we may need to resolve it into an absolute path before moving ahead.
            val components = Components.parse(path)
            val absoluteComponents =
                if (path.startsWith(File.separatorChar)) {
                    // Already an absolute path, do nothing. Theres still some relative-ness here,
                    // as we assume that the path is still in the same volume as the working
                    // directory.
                    // Unsure if any program goes as far as writing out the full unobfuscated
                    // absolute path.
                    components
                } else {
                    // Relative path, resolve it
                    components.absoluteTo(workingDirectory.components)
                }

            paths.add(Path(workingDirectory.volume, absoluteComponents))
        }

        return if (paths.isNotEmpty()) {
            ImportedPlaylist(name, paths)
        } else {
            // Couldn't get anything useful out of this file.
            null
        }
    }

    override fun write(playlist: Playlist, outputStream: OutputStream, workingDirectory: Path) {
        val writer = outputStream.bufferedWriter()
        // Try to be as compliant to the spec as possible while also cramming it full of extensions
        // I imagine other players will use.
        writer.writeLine("#EXTM3U")
        writer.writeLine("#EXTENC:UTF-8")
        writer.writeLine("#PLAYLIST:${playlist.name.resolve(context)}")
        for (song in playlist.songs) {
            val relativePath = song.path.components.relativeTo(workingDirectory.components)
            writer.writeLine("#EXTINF:${song.durationMs},${song.name.resolve(context)}")
            writer.writeLine("#EXTALB:${song.album.name.resolve(context)}")
            writer.writeLine("#EXTART:${song.artists.resolveNames(context)}")
            writer.writeLine("#EXTGEN:${song.genres.resolveNames(context)}")
            writer.writeLine(relativePath.toString())
        }
        writer.flush()
    }

    private fun BufferedWriter.writeLine(line: String) {
        write(line)
        newLine()
    }

    private fun Components.absoluteTo(workingDirectory: Components): Components {
        var absoluteComponents = workingDirectory
        for (component in components) {
            when (component) {
                // Parent specifier, go "back" one directory (in practice cleave off the last
                // component)
                ".." -> absoluteComponents = absoluteComponents.parent()
                // Current directory, the components are already there.
                "." -> {}
                // New directory, add it
                else -> absoluteComponents = absoluteComponents.child(component)
            }
        }
        return absoluteComponents
    }

    private fun Components.relativeTo(workingDirectory: Components): Components {
        // We want to find the common prefix of the working directory and path, and then
        // and them combine them with the correct relative elements to make sure they
        // resolve the same.
        var commonIndex = 0
        while (commonIndex < components.size &&
            commonIndex < workingDirectory.components.size &&
            components[commonIndex] == workingDirectory.components[commonIndex]) {
            ++commonIndex
        }

        var relativeComponents = Components.parse(".")

        // TODO: Simplify this logic
        when {
            commonIndex == components.size && commonIndex == workingDirectory.components.size -> {
                // The paths are the same. This shouldn't occur.
            }
            commonIndex == components.size -> {
                // The working directory is deeper in the path, backtrack.
                for (i in 0..workingDirectory.components.size - commonIndex - 1) {
                    relativeComponents = relativeComponents.child("..")
                }
            }
            commonIndex == workingDirectory.components.size -> {
                // Working directory is shallower than the path, can just append the
                // non-common remainder of the path
                relativeComponents = relativeComponents.child(depth(commonIndex))
            }
            else -> {
                // The paths are siblings. Backtrack and append as needed.
                for (i in 0..workingDirectory.components.size - commonIndex - 1) {
                    relativeComponents = relativeComponents.child("..")
                }
                relativeComponents = relativeComponents.child(depth(commonIndex))
            }
        }

        return relativeComponents
    }
}
