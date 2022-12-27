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

/**
 * Convert milliseconds into deci-seconds (1/10th of a second).
 * @return A converted deci-second value.
 */
fun Long.msToDs() = floorDiv(100)

/**
 * Convert milliseconds into seconds.
 * @return A converted second value.
 */
fun Long.msToSecs() = floorDiv(1000)

/**
 * Convert deci-seconds (1/10th of a second) into milliseconds.
 * @return A converted millisecond value.
 */
fun Long.dsToMs() = times(100)

/**
 * Convert deci-seconds (1/10th of a second) into seconds.
 * @return A converted second value.
 */
fun Long.dsToSecs() = floorDiv(10)

/**
 * Convert seconds into milliseconds.
 * @return A converted millisecond value.
 */
fun Long.secsToMs() = times(1000)

/**
 * Convert a millisecond value into a string duration.
 * @param isElapsed Whether this duration is represents elapsed time. If this is false, then --:--
 * will be returned if the second value is 0.
 */
fun Long.formatDurationMs(isElapsed: Boolean) = msToSecs().formatDurationSecs(isElapsed)

/**
 * // * Format a deci-second value (1/10th of a second) into a string duration.
 * @param isElapsed Whether this duration is represents elapsed time. If this is false, then --:--
 * will be returned if the second value is 0.
 */
fun Long.formatDurationDs(isElapsed: Boolean) = dsToSecs().formatDurationSecs(isElapsed)

/**
 * Convert a second value into a string duration.
 * @param isElapsed Whether this duration is represents elapsed time. If this is false, then --:--
 * will be returned if the second value is 0.
 */
fun Long.formatDurationSecs(isElapsed: Boolean): String {
    if (!isElapsed && this == 0L) {
        // Non-elapsed duration is zero, return default value.
        return "--:--"
    }

    var durationString = DateUtils.formatElapsedTime(this)
    // Remove trailing zero values [i.e 01:42]. This is primarily for aesthetics.
    if (durationString[0] == '0') {
        durationString = durationString.slice(1 until durationString.length)
    }
    return durationString
}
