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
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.util.concurrent.CancellationException
import kotlin.reflect.KClass
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
fun <T> unlikelyToBeNull(value: T?) =
    if (BuildConfig.DEBUG) {
        requireNotNull(value)
    } else {
        value!!
    }

fun Int.nonZeroOrNull() = if (this > 0) this else null

fun Int.inRangeOrNull(range: IntRange) = if (range.contains(this)) this else null

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
fun lazyReflectedField(clazz: KClass<*>, field: String) = lazy {
    clazz.java.getDeclaredField(field).also { it.isAccessible = true }
}

/** Lazily reflect to retrieve a [Method]. */
fun lazyReflectedMethod(clazz: KClass<*>, method: String) = lazy {
    clazz.java.getDeclaredMethod(method).also { it.isAccessible = true }
}

/**
 * An abstraction that allows cheap cooperative multi-threading in shared object contexts. Every new
 * task should call [newHandle], while every running task should call [check] or [yield] depending
 * on the situation to determine if it should continue. Failure to follow the expectations of this
 * class will result in bugs.
 *
 * @author OxygenCobalt
 */
class TaskGuard {
    private var currentHandle = 0L

    /**
     * Returns a new handle to the calling task while invalidating the handle of the previous task.
     */
    @Synchronized fun newHandle() = ++currentHandle

    /** Check if the given [handle] is still valid. */
    @Synchronized fun check(handle: Long) = handle == currentHandle

    /**
     * Alternative to [kotlinx.coroutines.yield], that achieves the same behavior but in a much
     * cheaper manner.
     */
    @Synchronized
    fun yield(handle: Long) {
        if (!check(handle)) {
            throw CancellationException()
        }
    }
}
