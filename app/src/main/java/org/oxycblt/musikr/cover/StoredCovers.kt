package org.oxycblt.musikr.cover

import java.io.InputStream

interface StoredCovers {
    suspend fun read(cover: Cover.Single): InputStream?

    interface Editor {
        suspend fun write(data: ByteArray): Cover.Single?
    }

    companion object {
        suspend fun buildOn(): Editor = TODO()
        fun new(): Editor = TODO()
    }
}
