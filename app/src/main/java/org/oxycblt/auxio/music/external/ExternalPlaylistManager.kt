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
 
package org.oxycblt.auxio.music.external

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import org.oxycblt.auxio.music.Playlist
import javax.inject.Inject
import org.oxycblt.auxio.music.fs.DocumentPathFactory
import org.oxycblt.auxio.music.fs.Path
import org.oxycblt.auxio.music.fs.contentResolverSafe
import org.oxycblt.auxio.util.logE

/**
 * Generic playlist file importing abstraction.
 *
 * @see ImportedPlaylist
 * @see M3U
 * @author Alexander Capehart (OxygenCobalt)
 */
interface ExternalPlaylistManager {
    suspend fun import(uri: Uri): ImportedPlaylist?
    suspend fun export(playlist: Playlist, uri: Uri): Boolean
}

/**
 * A playlist that has been imported.
 *
 * @property name The name of the playlist. May be null if not provided.
 * @property paths The paths of the files in the playlist.
 * @see ExternalPlaylistManager
 * @see M3U
 */
data class ImportedPlaylist(val name: String?, val paths: List<Path>)

class ExternalPlaylistManagerImpl
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val documentPathFactory: DocumentPathFactory,
    private val m3u: M3U
) : ExternalPlaylistManager {
    override suspend fun import(uri: Uri): ImportedPlaylist? {
        val filePath = documentPathFactory.unpackDocumentUri(uri) ?: return null

        return try {
            context.contentResolverSafe.openInputStream(uri)?.use {
                return m3u.read(it, filePath.directory)
            }
        } catch (e: Exception) {
            logE("Failed to import playlist: $e")
            null
        }
    }

    override suspend fun export(playlist: Playlist, uri: Uri): Boolean {
        val filePath = documentPathFactory.unpackDocumentUri(uri) ?: return false
        return try {
            val outputStream = context.contentResolverSafe.openOutputStream(uri)
            if (outputStream == null) {
                logE("Failed to export playlist: Could not open output stream")
                return false
            }
            outputStream.use {
                m3u.write(playlist, it, filePath.directory)
                true
            }
        } catch (e: Exception) {
            logE("Failed to export playlist: $e")
            false
        }
    }
}
