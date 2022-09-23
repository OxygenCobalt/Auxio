/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.music

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.text.format.DateUtils
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.logD
import java.util.UUID

/** Shortcut for making a [ContentResolver] query with less superfluous arguments. */
fun ContentResolver.queryCursor(
    uri: Uri,
    projection: Array<out String>,
    selector: String? = null,
    args: Array<String>? = null
) = query(uri, projection, selector, args, null)

/** Shortcut for making a [ContentResolver] query and using the particular cursor with [use]. */
inline fun <reified R> ContentResolver.useQuery(
    uri: Uri,
    projection: Array<out String>,
    selector: String? = null,
    args: Array<String>? = null,
    block: (Cursor) -> R
) = queryCursor(uri, projection, selector, args)?.use(block)

/**
 * For some reason the album cover URI namespace does not have a member in [MediaStore], but it
 * still works since at least API 21.
 */
private val EXTERNAL_ALBUM_ART_URI = Uri.parse("content://media/external/audio/albumart")

/** Converts a [Long] Audio ID into a URI to that particular audio file. */
val Long.audioUri: Uri
    get() = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this)

/** Converts a [Long] Album ID into a URI pointing to MediaStore-cached album art. */
val Long.albumCoverUri: Uri
    get() = ContentUris.withAppendedId(EXTERNAL_ALBUM_ART_URI, this)

fun String.toUuidOrNull(): UUID? = try {
    UUID.fromString(this)
} catch (e: IllegalArgumentException) {
    null
}

/** Shortcut to resolve a year from a nullable date. Will return "No Date" if it is null. */
fun Date?.resolveYear(context: Context) =
    this?.resolveYear(context) ?: context.getString(R.string.def_date)

/** Converts a long in milliseconds to a long in deci-seconds */
fun Long.msToDs() = floorDiv(100)

/** Converts a long in milliseconds to a long in seconds */
fun Long.msToSecs() = floorDiv(1000)

/** Converts a long in deci-seconds to a long in milliseconds. */
fun Long.dsToMs() = times(100)

/** Converts a long in deci-seconds to a long in seconds. */
fun Long.dsToSecs() = floorDiv(10)

/** Converts a long in seconds to a long in milliseconds. */
fun Long.secsToMs() = times(1000)

/**
 * Convert a [Long] of milliseconds into a string duration.
 * @param isElapsed Whether this duration is represents elapsed time. If this is false, then --:--
 * will be returned if the second value is 0.
 */
fun Long.formatDurationMs(isElapsed: Boolean) = msToSecs().formatDurationSecs(isElapsed)

/**
 * Convert a [Long] of deci-seconds into a string duration.
 * @param isElapsed Whether this duration is represents elapsed time. If this is false, then --:--
 * will be returned if the second value is 0.
 */
fun Long.formatDurationDs(isElapsed: Boolean) = dsToSecs().formatDurationSecs(isElapsed)

/**
 * Convert a [Long] of seconds into a string duration.
 * @param isElapsed Whether this duration is represents elapsed time. If this is false, then --:--
 * will be returned if the second value is 0.
 */
fun Long.formatDurationSecs(isElapsed: Boolean): String {
    if (!isElapsed && this == 0L) {
        logD("Non-elapsed duration is zero, using --:--")
        return "--:--"
    }

    var durationString = DateUtils.formatElapsedTime(this)

    // If the duration begins with a excess zero [e.g 01:42], then cut it off.
    if (durationString[0] == '0') {
        durationString = durationString.slice(1 until durationString.length)
    }

    return durationString
}
