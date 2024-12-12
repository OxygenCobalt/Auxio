package org.oxycblt.ktaglib

import android.content.Context
import java.io.FileInputStream
import java.nio.ByteBuffer

class AndroidInputStream(
    context: Context,
    fileRef: FileRef
) : NativeInputStream {
    private val fileName = fileRef.fileName
    private val fd = requireNotNull(context.contentResolver.openFileDescriptor(fileRef.uri, "r")) {
        "Failed to open file descriptor for ${fileRef.fileName}"
    }
    private val fis = FileInputStream(fd.fileDescriptor)
    private val channel = fis.channel

    override fun name() = fileName

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