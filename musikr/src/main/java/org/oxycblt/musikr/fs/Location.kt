/*
 * Copyright (c) 2024 Auxio Project
 * Location.kt is part of Auxio.
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
import org.oxycblt.musikr.fs.path.DocumentPathFactory
import org.oxycblt.musikr.fs.saf.contentResolverSafe

sealed class Location(val uri: Uri, val path: Path) {
    override fun equals(other: Any?) = other is Location && uri == other.uri

    override fun hashCode() = 31 * uri.hashCode()

    override fun toString(): String = uri.toString()

    class Unopened private constructor(uri: Uri, path: Path) : Location(uri, path) {
        fun open(context: Context): Opened? {
            if (isUnopened(context, uri)) {
                try {
                    context.contentResolverSafe.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                } catch (e: Exception) {
                    // Not fully sure what happens if I'm disallowed to take the permission,
                    // check for both circumstances (error or no-op)
                    return null
                }

                if (isUnopened(context, uri)) {
                    return null
                }
            }
            return Opened(uri, path)
        }

        private fun isUnopened(context: Context, uri: Uri) =
            context.contentResolverSafe.persistedUriPermissions.none {
                it.uri == uri && it.isReadPermission && it.isWritePermission
            }

        companion object {
            fun from(context: Context, uri: Uri): Unopened? {
                if (!DocumentsContract.isTreeUri(uri)) return null
                val documentPathFactory = DocumentPathFactory.from(context)
                val path = documentPathFactory.unpackDocumentTreeUri(uri) ?: return null
                return Unopened(uri, path)
            }
        }
    }

    class Opened internal constructor(uri: Uri, path: Path) : Location(uri, path) {
        override fun equals(other: Any?) = other is Opened && uri == other.uri && path == other.path

        override fun hashCode() = 31 * uri.hashCode() + path.hashCode()
    }
}
