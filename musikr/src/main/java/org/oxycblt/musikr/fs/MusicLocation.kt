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
import org.oxycblt.musikr.fs.device.contentResolverSafe
import org.oxycblt.musikr.fs.path.DocumentPathFactory
import org.oxycblt.musikr.util.splitEscaped

class MusicLocation private constructor(val uri: Uri, val path: Path, val excludedSubdirs: Set<String> = emptySet()) {
    override fun equals(other: Any?) = other is MusicLocation && uri == other.uri

    override fun hashCode() = 31 * uri.hashCode()

    override fun toString(): String = uri.toString()

    companion object {
        fun new(context: Context, uri: Uri, excludedSubdirs: Set<String> = emptySet()): MusicLocation? {
            if (!DocumentsContract.isTreeUri(uri)) return null
            val documentPathFactory = DocumentPathFactory.from(context)
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
            return MusicLocation(uri, path, excludedSubdirs)
        }

        fun existing(context: Context, uri: Uri, excludedSubdirs: Set<String> = emptySet()): MusicLocation? {
            val documentPathFactory = DocumentPathFactory.from(context)
            if (!DocumentsContract.isTreeUri(uri)) return null
            val notPersisted =
                context.contentResolverSafe.persistedUriPermissions.none {
                    it.uri == uri && it.isReadPermission && it.isWritePermission
                }
            if (notPersisted) return null
            val path = documentPathFactory.unpackDocumentTreeUri(uri) ?: return null
            return MusicLocation(uri, path, excludedSubdirs)
        }

        fun toString(list: List<MusicLocation>): String {
            return list.joinToString(";") { location ->
                val uriStr = location.uri.toString().replace(";", "\\;")
                val excludedSubdirsStr = location.excludedSubdirs.joinToString(",") { it.replace(",", "\\,").replace(";", "\\;") }
                if (excludedSubdirsStr.isEmpty()) uriStr else "$uriStr|$excludedSubdirsStr"
            }
        }

        fun existing(context: Context, string: String): List<MusicLocation> {
            return string.splitEscaped { it == ';' }.mapNotNull { entry ->
                val parts = entry.split("|", limit = 2)
                val uriStr = parts[0]
                val excludedSubdirs = if (parts.size > 1) {
                    parts[1].splitEscaped { it == ',' }.toSet()
                } else {
                    emptySet()
                }
                existing(context, Uri.parse(uriStr), excludedSubdirs)
            }
        }
    }
}
