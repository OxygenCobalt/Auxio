/*
 * Copyright (c) 2021 Auxio Project
 * LangUtil.kt is part of Auxio.
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

import java.security.MessageDigest
import java.util.UUID
import kotlin.reflect.KClass
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.music.info.Date

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
 * Aliases a check to ensure that the given number is non-zero.
 *
 * @return The given number if it's non-zero, null otherwise.
 */
fun Int.positiveOrNull() = if (this > 0) this else null

/**
 * Aliases a check to ensure that the given number is non-zero.
 *
 * @return The same number if it's non-zero, null otherwise.
 */
fun Long.positiveOrNull() = if (this > 0) this else null

/**
 * Aliases a check to ensure that the given number is non-zero.
 *
 * @return The same number if it's non-zero, null otherwise.
 */
fun Float.nonZeroOrNull() = if (this != 0f) this else null

/**
 * Aliases a check to ensure a given value is in a specified range.
 *
 * @param range The valid range of values for this number.
 * @return The same number if it is in the range, null otherwise.
 */
fun Int.inRangeOrNull(range: IntRange) = if (range.contains(this)) this else null

/**
 * Lazily set up a reflected field. Automatically handles visibility changes. Adapted from Material
 * Files: https://github.com/zhanghai/MaterialFiles
 *
 * @param clazz The [KClass] to reflect into.
 * @param field The name of the field to obtain.
 */
fun lazyReflectedField(clazz: KClass<*>, field: String) = lazy {
    clazz.java.getDeclaredField(field).also { it.isAccessible = true }
}
/**
 * Lazily set up a reflected method. Automatically handles visibility changes. Adapted from Material
 * Files: https://github.com/zhanghai/MaterialFiles
 *
 * @param clazz The [KClass] to reflect into.
 * @param method The name of the method to obtain.
 */
fun lazyReflectedMethod(clazz: KClass<*>, method: String, vararg params: KClass<*>) = lazy {
    clazz.java.getDeclaredMethod(method, *params.map { it.java }.toTypedArray()).also {
        it.isAccessible = true
    }
}

/**
 * Convert a [String] to a [UUID].
 *
 * @return A [UUID] converted from the [String] value, or null if the value was not valid.
 * @see UUID.fromString
 */
fun String.toUuidOrNull(): UUID? =
    try {
        UUID.fromString(this)
    } catch (e: IllegalArgumentException) {
        null
    }

/**
 * Update a [MessageDigest] with a lowercase [String].
 *
 * @param string The [String] to hash. If null, it will not be hashed.
 */
fun MessageDigest.update(string: String?) {
    if (string != null) {
        update(string.lowercase().toByteArray())
    } else {
        update(0)
    }
}

/**
 * Update a [MessageDigest] with the string representation of a [Date].
 *
 * @param date The [Date] to hash. If null, nothing will be done.
 */
fun MessageDigest.update(date: Date?) {
    if (date != null) {
        update(date.toString().toByteArray())
    } else {
        update(0)
    }
}

/**
 * Update a [MessageDigest] with the lowercase versions of all of the input [String]s.
 *
 * @param strings The [String]s to hash. If a [String] is null, it will not be hashed.
 */
fun MessageDigest.update(strings: List<String?>) {
    strings.forEach(::update)
}

/**
 * Update a [MessageDigest] with the little-endian bytes of a [Int].
 *
 * @param n The [Int] to write. If null, nothing will be done.
 */
fun MessageDigest.update(n: Int?) {
    if (n != null) {
        update(byteArrayOf(n.toByte(), n.shr(8).toByte(), n.shr(16).toByte(), n.shr(24).toByte()))
    } else {
        update(0)
    }
}
