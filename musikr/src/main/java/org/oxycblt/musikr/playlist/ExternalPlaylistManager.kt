/*
 * Copyright (c) 2023 Auxio Project
 * ExternalPlaylistManager.kt is part of Auxio.
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
 
package org.oxycblt.musikr.playlist

import android.content.Context
import android.net.Uri
import org.oxycblt.musikr.Playlist
import org.oxycblt.musikr.fs.Components
import org.oxycblt.musikr.fs.Path
import org.oxycblt.musikr.fs.device.contentResolverSafe
import org.oxycblt.musikr.fs.path.DocumentPathFactory
import org.oxycblt.musikr.playlist.m3u.M3U

/**
 * Generic playlist file importing abstraction.
 *
 * @see ImportedPlaylist
 * @see M3U
 * @author Alexander Capehart (OxygenCobalt)
 */
interface ExternalPlaylistManager {
    /**
     * Import the playli L.d("Unable to extract bit rate field") st file at the given [uri].
     *
     * @param uri The [Uri] of the playlist file to import.
     * @return An [ImportedPlaylist] containing the paths to the files listed in the playlist file,
     *   or null if the playlist could not be imported.
     */
    suspend fun import(uri: Uri): ImportedPlaylist?

    /**
     * Export the given [playlist] to the given [uri].
     *
     * @param playlist The playlist to export.
     * @param uri The [Uri] to export the playlist to.
     * @param config The configuration to use when exporting the playlist.
     * @return True if the playlist was successfully exported, false otherwise.
     */
    suspend fun export(playlist: Playlist, uri: Uri, config: ExportConfig): Boolean

    companion object {
        fun from(context: Context): ExternalPlaylistManager =
            ExternalPlaylistManagerImpl(
                context, DocumentPathFactory.from(context), M3U.from(context))
    }
}

/**
 * Configuration to use when exporting playlists.
 *
 * @property absolute Whether or not to use absolute paths when exporting. If not, relative paths
 *   will be used.
 * @property windowsPaths Whether or not to use Windows-style paths when exporting (i.e prefixed
 *   with C:\\ and using \). If not, Unix-style paths will be used (i.e prefixed with /).
 * @see ExternalPlaylistManager.export
 */
data class ExportConfig(val absolute: Boolean, val windowsPaths: Boolean)

/**
 * A playlist that has been imported.
 *
 * @property name The name of the playlist. May be null if not provided.
 * @property paths The paths of the files in the playlist.
 * @see ExternalPlaylistManager
 * @see M3U
 */
data class ImportedPlaylist(val name: String?, val paths: List<PossiblePaths>)

typealias PossiblePaths = List<Path>

private class ExternalPlaylistManagerImpl(
    private val context: Context,
    private val documentPathFactory: DocumentPathFactory,
    private val m3u: M3U
) : ExternalPlaylistManager {
    override suspend fun import(uri: Uri): ImportedPlaylist? {
        val filePath = documentPathFactory.unpackDocumentUri(uri) ?: return null

        return try {
            context.contentResolverSafe.openInputStream(uri)?.use {
                val imported = m3u.read(it, filePath.directory) ?: return null
                val name = imported.name
                if (name != null) {
                    return imported
                }
                // Strip extension
                val fileName = filePath.name ?: return imported
                val split = fileName.split(".")
                var newName = split[0]
                // Replace delimiters with space
                newName = newName.replace(Regex("[_-]"), " ")
                // Replace long stretches of whitespace with one space
                newName = newName.replace(Regex("\\s+"), " ")
                return ImportedPlaylist(newName, imported.paths)
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun export(playlist: Playlist, uri: Uri, config: ExportConfig): Boolean {
        val filePath = documentPathFactory.unpackDocumentUri(uri) ?: return false
        val workingDirectory =
            if (config.absolute) {
                Path(filePath.volume, Components.parseUnix("/"))
            } else {
                filePath.directory
            }
        return try {
            val outputStream = context.contentResolverSafe.openOutputStream(uri) ?: return false
            outputStream.use {
                m3u.write(playlist, it, workingDirectory, config)
                true
            }
        } catch (e: Exception) {
            false
        }
    }
}
