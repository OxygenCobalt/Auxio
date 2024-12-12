package org.oxycblt.ktaglib

import java.io.InputStream

object KTagLib {
    // Used to load the 'ktaglib' library on application startup.
    init {
        System.loadLibrary("ktaglib")
    }

    /**
     * A native method that is implemented by the 'ktaglib' native library,
     * which is packaged with this application.
     */
    external fun load(fileRef: FileRef): Tag?
}

data class FileRef(
    val fileName: String,
    val inputStream: InputStream
)

data class Tag(
    val id3v2: Map<String, String>,
    val vorbis: Map<String, String>,
    val coverData: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Tag

        if (id3v2 != other.id3v2) return false
        if (vorbis != other.vorbis) return false
        if (!coverData.contentEquals(other.coverData)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id3v2.hashCode()
        result = 31 * result + vorbis.hashCode()
        result = 31 * result + coverData.contentHashCode()
        return result
    }
}
