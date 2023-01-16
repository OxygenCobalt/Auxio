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
import kotlin.reflect.KClass
import org.oxycblt.auxio.BuildConfig

/**
 * Sanitizes a value that is unlikely to be null. On debug builds, this aliases to [requireNotNull],
 * otherwise, it aliases to the unchecked dereference operator (!!). This can be used as a minor
 * optimization in certain cases.
 */
fun <T> unlikelyToBeNull(value: T?) =
    if (BuildConfig.DEBUG) {
        requireNotNull(value)
    } else {
        value!!
    }

/**
 * Require that the given data is a specific type [T].
 * @param data The data to check.
 * @return A data casted to [T].
 * @throws IllegalStateException If the data cannot be casted to [T].
 */
inline fun <reified T> requireIs(data: Any?): T {
    check(data is T) { "Unexpected datatype: ${data?.let { it::class.simpleName }}" }
    return data
}

/**
 * Aliases a check to ensure that the given number is non-zero.
 * @return The given number if it's non-zero, null otherwise.
 */
fun Int.nonZeroOrNull() = if (this > 0) this else null

/**
 * Aliases a check to ensure that the given number is non-zero.
 * @return The same number if it's non-zero, null otherwise.
 */
fun Long.nonZeroOrNull() = if (this > 0) this else null

/**
 * Aliases a check to ensure a given value is in a specified range.
 * @param range The valid range of values for this number.
 * @return The same number if it is in the range, null otherwise.
 */
fun Int.inRangeOrNull(range: IntRange) = if (range.contains(this)) this else null

/**
 * Lazily set up a reflected field. Automatically handles visibility changes. Adapted from Material
 * Files: https://github.com/zhanghai/MaterialFiles
 * @param clazz The [KClass] to reflect into.
 * @param field The name of the field to obtain.
 */
fun lazyReflectedField(clazz: KClass<*>, field: String) = lazy {
    clazz.java.getDeclaredField(field).also { it.isAccessible = true }
}
/**
 * Lazily set up a reflected method. Automatically handles visibility changes. Adapted from Material
 * Files: https://github.com/zhanghai/MaterialFiles
 * @param clazz The [KClass] to reflect into.
 * @param method The name of the method to obtain.
 */
fun lazyReflectedMethod(clazz: KClass<*>, method: String) = lazy {
    clazz.java.getDeclaredMethod(method).also { it.isAccessible = true }
}

/**
 * Assert that the execution is currently on a background thread. This is helpful for functions that
 * don't necessarily require suspend, but still want to ensure that they are being called with a
 * co-routine.
 * @throws IllegalStateException If the execution is not on a background thread.
 */
fun requireBackgroundThread() {
    check(Looper.myLooper() != Looper.getMainLooper()) {
        "This operation must be ran on a background thread"
    }
}
