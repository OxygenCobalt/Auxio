/*
 * Copyright (c) 2023 Auxio Project
 * PlaylistImporter.kt is part of Auxio.
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

import android.content.ContentResolver
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.oxycblt.auxio.music.fs.ContentPathResolver
import org.oxycblt.auxio.music.fs.Path

interface PlaylistImporter {
    suspend fun import(uri: Uri): ImportedPlaylist?
}

data class ImportedPlaylist(val name: String?, val paths: List<Path>)

class PlaylistImporterImpl
@Inject
constructor(
    @ApplicationContext private val contentResolver: ContentResolver,
    private val contentPathResolver: ContentPathResolver,
    private val m3u: M3U
) : PlaylistImporter {
    override suspend fun import(uri: Uri): ImportedPlaylist? {
        val workingDirectory = contentPathResolver.resolve(uri) ?: return null
        return contentResolver.openInputStream(uri)?.use {
            val paths = m3u.read(it, workingDirectory) ?: return null
            return ImportedPlaylist(null, paths)
        }
    }
}
