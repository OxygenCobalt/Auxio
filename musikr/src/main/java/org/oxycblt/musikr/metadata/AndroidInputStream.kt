/*
 * Copyright (c) 2024 Auxio Project
 * AndroidInputStream.kt is part of Auxio.
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

import android.content.Context
import android.net.Uri
import java.io.FileInputStream
import java.nio.ByteBuffer

internal class AndroidInputStream(context: Context, uri: Uri) : NativeInputStream {
    private val fd =
        requireNotNull(context.contentResolver.openFileDescriptor(uri, "r")) {
            "Failed to open file descriptor for $uri"
        }
    private val fis = FileInputStream(fd.fileDescriptor)
    private val channel = fis.channel

    override fun readBlock(length: Long): ByteArray {
        val buffer = ByteBuffer.allocate(length.toInt())
        channel.read(buffer)
        return buffer.array()
    }

    override fun isOpen(): Boolean {
        return channel.isOpen
    }

    override fun seekFromBeginning(offset: Long) {
        channel.position(offset)
    }

    override fun seekFromCurrent(offset: Long) {
        channel.position(channel.position() + offset)
    }

    override fun seekFromEnd(offset: Long) {
        channel.position(channel.size() - offset)
    }

    override fun clear() {
        // Nothing to clear
    }

    override fun tell() = channel.position()

    override fun length() = channel.size()

    fun close() {
        channel.close()
        fis.close()
        fd.close()
    }
}
