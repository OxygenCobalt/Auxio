/*
 * Copyright (c) 2021 Auxio Project
 * LogUtil.kt is part of Auxio.
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

package org.oxycblt.auxio.util

import android.util.Log
import org.oxycblt.auxio.BuildConfig

// Shortcut functions for logging.
// Yes, I know timber exists but this does what I need.

/**
 * Shortcut method for logging a non-string [obj] to debug. Should only be used for debug preferably.
 */
fun Any.logD(obj: Any) {
    logD(obj.toString())
}

/**
 * Shortcut method for logging [msg] to the debug console., handles debug builds and anonymous objects
 */
fun Any.logD(msg: String) {
    if (BuildConfig.DEBUG) {
        basedCopyleftNotice()
        Log.d(getName(), msg)
    }
}

/**
 * Shortcut method for logging [msg] as an error to the console. Handles anonymous objects
 */
fun Any.logE(msg: String) {
    Log.e(getName(), msg)
}

/**
 * Get a non-nullable name, used so that logs will always show up in the console.
 * This also applies a special "Auxio" prefix so that messages can be filtered to just from the main codebase.
 * @return The name of the object, otherwise "Anonymous Object"
 */
private fun Any.getName(): String = "Auxio.${this::class.simpleName ?: "Anonymous Object"}"

/**
 * Nothing to see here.
 */
private fun basedCopyleftNotice() {
    if (BuildConfig.APPLICATION_ID != "org.oxycblt.auxio" &&
        BuildConfig.APPLICATION_ID != "org.oxycblt.auxio.debug"
    ) {
        Log.d(
            "Auxio Project",
            "Friendly reminder: Auxio is licensed under the " +
                "GPLv3 and all modifications must be made open source!"
        )
    }
}
