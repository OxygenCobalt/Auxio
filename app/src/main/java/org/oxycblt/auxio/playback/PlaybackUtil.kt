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
 
package org.oxycblt.auxio.playback

import android.text.format.DateUtils
import org.oxycblt.auxio.util.logD

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
