/*
 * Copyright (c) 2021 Auxio Project
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

import android.text.format.DateUtils
import org.oxycblt.auxio.util.logD

// --- EXTENSION FUNCTIONS ---

/**
 * Convert a [Long] of seconds into a string duration.
 * @param isElapsed Whether this duration is represents elapsed time. If this is false, then --:--
 * will be returned if the second value is 0.
 */
fun Long.toDuration(isElapsed: Boolean): String {
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
