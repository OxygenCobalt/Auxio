/*
 * Copyright (c) 2024 Auxio Project
 * NativeInputStream.kt is part of Auxio.
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
 
package org.oxycblt.musikr.metadata

/**
 * Java interface for the read-only methods in TagLib's IOStream API.
 *
 * The vast majority of IO shim between Taglib/KTaglib should occur here to minimize JNI calls.
 */
interface NativeInputStream {
    fun name(): String

    fun readBlock(length: Long): ByteArray

    fun isOpen(): Boolean

    fun seekFromBeginning(offset: Long)

    fun seekFromCurrent(offset: Long)

    fun seekFromEnd(offset: Long)

    fun clear()

    fun tell(): Long

    fun length(): Long
}
