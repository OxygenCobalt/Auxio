/*
 * Copyright (c) 2025 Auxio Project
 * CoverProvider.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import kotlinx.coroutines.runBlocking
import org.oxycblt.auxio.image.covers.MutableSiloedCovers
import org.oxycblt.auxio.image.covers.SiloedCoverId
import org.oxycblt.musikr.cover.CoverIdentifier
import org.oxycblt.musikr.cover.ObtainResult

// AndroidManifest.xml addition

// ImageProvider.java
class CoverProvider : ContentProvider() {
    override fun onCreate(): Boolean = true

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        check(mode == "r") { "Unsupported mode: $mode" }
        check(uriMatcher.match(uri) == 1) { "Unknown URI: $uri" }
        val id = requireNotNull(uri.lastPathSegment) { "No ID in URI: $uri" }
        val coverId = requireNotNull(SiloedCoverId.parse(id)) { "Invalid ID: $id" }
        return runBlocking {
            val siloedCovers =
                MutableSiloedCovers.from(requireContext(), coverId.silo, CoverIdentifier.md5())
            when (val res = siloedCovers.obtain(coverId.id)) {
                is ObtainResult.Hit -> res.cover.fd()
                is ObtainResult.Miss -> null
            }
        }
    }

    override fun getType(uri: Uri): String {
        check(uriMatcher.match(uri) == 1) { "Unknown URI: $uri" }
        return "image/*"
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor = throw UnsupportedOperationException()

    override fun insert(uri: Uri, values: ContentValues?): Uri =
        throw UnsupportedOperationException()

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int =
        throw UnsupportedOperationException()

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = throw UnsupportedOperationException()

    companion object {
        private const val AUTHORITY = "org.oxycblt.auxio.image"
        private const val IMAGES_PATH = "covers"
        private val uriMatcher =
            UriMatcher(UriMatcher.NO_MATCH).apply { addURI(AUTHORITY, "$IMAGES_PATH/*", 1) }

        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$IMAGES_PATH")
    }
}
