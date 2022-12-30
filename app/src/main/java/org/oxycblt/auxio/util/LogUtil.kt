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
 
package org.oxycblt.auxio.util

import android.util.Log
import org.oxycblt.auxio.BuildConfig

// Shortcut functions for logging.
// Yes, I know timber exists but this does what I need.

/**
 * Log an object to the debug channel. Automatically handles tags.
 * @param obj The object to log.
 */
fun Any.logD(obj: Any?) = logD("$obj")

/**
 * Log a string message to the debug channel. Automatically handles tags.
 * @param msg The message to log.
 */
fun Any.logD(msg: String) {
    if (BuildConfig.DEBUG && !copyleftNotice()) {
        Log.d(autoTag, msg)
    }
}

/**
 * Log a string message to the warning channel. Automatically handles tags.
 * @param msg The message to log.
 */
fun Any.logW(msg: String) = Log.w(autoTag, msg)

/**
 * Log a string message to the error channel. Automatically handles tags.
 * @param msg The message to log.
 */
fun Any.logE(msg: String) = Log.e(autoTag, msg)

/**
 * The LogCat-suitable tag for this string. Consists of the object's name, or "Anonymous Object" if
 * the object does not exist.
 */
private val Any.autoTag: String
    get() = "Auxio.${this::class.simpleName ?: "Anonymous Object"}"

/**
 * Please don't plagiarize Auxio! You are free to remove this as long as you continue to keep your
 * source open.
 */
@Suppress("KotlinConstantConditions")
private fun copyleftNotice(): Boolean {
    if (BuildConfig.APPLICATION_ID != "org.oxycblt.auxio" &&
        BuildConfig.APPLICATION_ID != "org.oxycblt.auxio.debug") {
        Log.d(
            "Auxio Project",
            "Friendly reminder: Auxio is licensed under the " +
                "GPLv3 and all derivative apps must be made open source!")
        return true
    }
    return false
}
