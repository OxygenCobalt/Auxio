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

import android.util.Log
import java.io.FileInputStream
import java.nio.ByteBuffer
import org.oxycblt.musikr.fs.DeviceFile

internal class NativeInputStream(private val deviceFile: DeviceFile, fis: FileInputStream) {
    private val channel = fis.channel

    fun name() = requireNotNull(deviceFile.path.name)

    fun readBlock(length: Long): ByteArray? {
        try {
            val buffer = ByteBuffer.allocate(length.toInt())
            channel.read(buffer)
            return buffer.array()
        } catch (e: Exception) {
            Log.d("NativeInputStream", "Error reading block", e)
            return null
        }
    }

    fun isOpen(): Boolean {
        return channel.isOpen
    }

    fun seekFromBeginning(offset: Long): Boolean {
        try {
            channel.position(offset)
            return true
        } catch (e: Exception) {
            Log.d("NativeInputStream", "Error seeking from beginning", e)
            return false
        }
    }

    fun seekFromCurrent(offset: Long): Boolean {
        try {
            channel.position(channel.position() + offset)
            return true
        } catch (e: Exception) {
            Log.d("NativeInputStream", "Error seeking from current", e)
            return false
        }
    }

    fun seekFromEnd(offset: Long): Boolean {
        try {
            channel.position(channel.size() + offset)
            return true
        } catch (e: Exception) {
            Log.d("NativeInputStream", "Error seeking from end", e)
            return false
        }
    }

    fun tell() =
        try {
            channel.position()
        } catch (e: Exception) {
            Log.d("NativeInputStream", "Error getting position", e)
            Long.MIN_VALUE
        }

    fun length() =
        try {
            channel.size()
        } catch (e: Exception) {
            Log.d("NativeInputStream", "Error getting length", e)
            Long.MIN_VALUE
        }

    fun close() {
        channel.close()
    }
}
