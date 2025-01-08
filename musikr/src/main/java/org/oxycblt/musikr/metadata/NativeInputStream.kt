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

import java.io.FileInputStream
import java.nio.ByteBuffer

internal class NativeInputStream(fis: FileInputStream) {
    private val channel = fis.channel

    fun readBlock(length: Long): ByteArray {
        val buffer = ByteBuffer.allocate(length.toInt())
        channel.read(buffer)
        return buffer.array()
    }

    fun isOpen(): Boolean {
        return channel.isOpen
    }

    fun seekFromBeginning(offset: Long) {
        channel.position(offset)
    }

    fun seekFromCurrent(offset: Long) {
        channel.position(channel.position() + offset)
    }

    fun seekFromEnd(offset: Long) {
        channel.position(channel.size() + offset)
    }

    fun tell() = channel.position()

    fun length() = channel.size()

    fun close() {
        channel.close()
    }
}
