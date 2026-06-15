/*
 * Copyright (c) 2022 Auxio Project
 * QueryUtil.kt is part of Auxio.
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
 
package org.oxycblt.musikr.fs.saf

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri

/**
 * Get a content resolver that will not mangle MediaStore queries on certain devices. See
 * https://github.com/OxygenCobalt/Auxio/issues/50 for more info.
 */
internal val Context.contentResolverSafe: ContentResolver
    get() = applicationContext.contentResolver

/**
 * A shortcut for querying the [ContentResolver] database.
 *
 * @param uri The [Uri] of content to retrieve.
 * @param projection A list of SQL columns to query from the database.
 * @param selector A SQL selection statement to filter results. Spaces where arguments should be
 *   filled in are represented with a "?".
 * @param args The arguments used for the selector.
 * @return A [Cursor] of the queried values, organized by the column projection.
 * @throws IllegalStateException If the [ContentResolver] did not return the queried [Cursor].
 * @see ContentResolver.query
 */
internal fun ContentResolver.safeQuery(
    uri: Uri,
    projection: Array<out String>,
    selector: String? = null,
    args: Array<String>? = null
) = requireNotNull(query(uri, projection, selector, args, null)) { "ContentResolver query failed" }

/**
 * A shortcut for [safeQuery] with [use] applied, automatically cleaning up the [Cursor]'s resources
 * when no longer used.
 *
 * @param uri The [Uri] of content to retrieve.
 * @param projection A list of SQL columns to query from the database.
 * @param selector A SQL selection statement to filter results. Spaces where arguments should be
 *   filled in are represented with a "?".
 * @param args The arguments used for the selector.
 * @param block The block of code to run with the queried [Cursor]. Will not be ran if the [Cursor]
 *   is empty.
 * @throws IllegalStateException If the [ContentResolver] did not return the queried [Cursor].
 * @see ContentResolver.query
 */
internal inline fun <reified R> ContentResolver.useQuery(
    uri: Uri,
    projection: Array<out String>,
    selector: String? = null,
    args: Array<String>? = null,
    block: (Cursor) -> R
) = safeQuery(uri, projection, selector, args).use(block)
