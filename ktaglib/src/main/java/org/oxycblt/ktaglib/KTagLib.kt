package org.oxycblt.ktaglib

import android.content.Context
import android.net.Uri

object KTagLib {
    init {
        System.loadLibrary("ktaglib")
    }

    /**
     * Open a file and extract a tag.
     *
     * Note: This method is blocking and should be handled as such if
     * calling from a coroutine.
     */
    fun open(context: Context, ref: FileRef): Tag? {
        val inputStream = AndroidInputStream(context, ref)
        val tag = openNative(inputStream)
        inputStream.close()
        return tag
    }

    private external fun openNative(ioStream: AndroidInputStream): Tag?
}

data class FileRef(
    val fileName: String,
    val uri: Uri
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
