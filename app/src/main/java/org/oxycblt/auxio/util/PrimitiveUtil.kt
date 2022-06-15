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

import android.os.Looper
import android.text.format.DateUtils
import androidx.core.math.MathUtils
import java.lang.reflect.Field
import java.lang.reflect.Method
import org.oxycblt.auxio.BuildConfig

/** Assert that we are on a background thread. */
fun requireBackgroundThread() {
    check(Looper.myLooper() != Looper.getMainLooper()) {
        "This operation must be ran on a background thread"
    }
}

/**
 * Sanitizes a nullable value that is not likely to be null. On debug builds, requireNotNull is
 * used, while on release builds, the unsafe assertion operator [!!] ]is used
 */
fun <T> unlikelyToBeNull(value: T?): T {
    return if (BuildConfig.DEBUG) {
        requireNotNull(value)
    } else {
        value!!
    }
}

/** Shortcut to clamp an integer between [min] and [max] */
fun Int.clamp(min: Int, max: Int): Int = MathUtils.clamp(this, min, max)

/**
 * Convert a [Long] of seconds into a string duration.
 * @param isElapsed Whether this duration is represents elapsed time. If this is false, then --:--
 * will be returned if the second value is 0.
 */
fun Long.formatDuration(isElapsed: Boolean): String {
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

/** Lazily reflect to retrieve a [Field]. */
inline fun <reified T : Any> lazyReflectedField(field: String): Lazy<Field> = lazy {
    T::class.java.getDeclaredField(field).also { it.isAccessible = true }
}

/** Lazily reflect to retrieve a [Method]. */
inline fun <reified T : Any> lazyReflectedMethod(
    methodName: String,
    vararg parameterTypes: Any
): Lazy<Method> = lazy {
    T::class
        .java
        .getDeclaredMethod(methodName, *parameterTypes.map { it::class.java }.toTypedArray())
        .also { it.isAccessible = true }
}
