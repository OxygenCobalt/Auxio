package org.oxycblt.musikr.metadata

/**
 * Java interface for the read-only methods in TagLib's IOStream API.
 *
 * The vast majority of IO shim between Taglib/KTaglib should occur here
 * to minimize JNI calls.
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