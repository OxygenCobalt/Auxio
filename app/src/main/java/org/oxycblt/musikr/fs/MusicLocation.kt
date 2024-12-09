/*
 * Copyright (c) 2024 Auxio Project
 * MusicLocation.kt is part of Auxio.
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

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.oxycblt.musikr.fs.path.DocumentPathFactory
import org.oxycblt.musikr.fs.util.contentResolverSafe

class MusicLocation internal constructor(val uri: Uri, val path: Path) {
    override fun equals(other: Any?) =
        other is MusicLocation && uri == other.uri && path == other.path

    override fun hashCode() = 31 * uri.hashCode() + path.hashCode()

    override fun toString(): String {
        val volumeId =
            when (path.volume) {
                is Volume.Internal -> VOLUME_INTERNAL
                is Volume.External -> path.volume.id
            }
        return "$uri=${volumeId}:${path.components.unixString}"
    }

    interface Factory {
        fun new(uri: Uri): MusicLocation?

        fun existing(uri: Uri): MusicLocation?
    }
}

class MusicLocationFactoryImpl
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val documentPathFactory: DocumentPathFactory
) : MusicLocation.Factory {
    override fun new(uri: Uri): MusicLocation? {
        if (!DocumentsContract.isTreeUri(uri)) return null
        val path = documentPathFactory.unpackDocumentTreeUri(uri) ?: return null
        val notPersisted =
            context.contentResolverSafe.persistedUriPermissions.none {
                it.uri == uri && it.isReadPermission && it.isWritePermission
            }
        if (notPersisted) {
            context.contentResolverSafe.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        return MusicLocation(uri, path)
    }

    override fun existing(uri: Uri): MusicLocation? {
        if (!DocumentsContract.isTreeUri(uri)) return null
        val notPersisted =
            context.contentResolverSafe.persistedUriPermissions.none {
                it.uri == uri && it.isReadPermission && it.isWritePermission
            }
        if (notPersisted) return null
        val path = documentPathFactory.unpackDocumentTreeUri(uri) ?: return null
        return MusicLocation(uri, path)
    }
}

private const val VOLUME_INTERNAL = "internal"
