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
import org.oxycblt.auxio.BuildConfig
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.reflect.KClass

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

/** Returns null if this value is 0. */
fun Int.nonZeroOrNull() = if (this > 0) this else null

/** Returns null if this value is 0. */
fun Long.nonZeroOrNull() = if (this > 0) this else null

/** Returns null if this value is not in [range]. */
fun Int.inRangeOrNull(range: IntRange) = if (range.contains(this)) this else null

/** Lazily reflect to retrieve a [Field]. */
fun lazyReflectedField(clazz: KClass<*>, field: String) = lazy {
    clazz.java.getDeclaredField(field).also { it.isAccessible = true }
}

/** Lazily reflect to retrieve a [Method]. */
fun lazyReflectedMethod(clazz: KClass<*>, method: String) = lazy {
    clazz.java.getDeclaredMethod(method).also { it.isAccessible = true }
}
