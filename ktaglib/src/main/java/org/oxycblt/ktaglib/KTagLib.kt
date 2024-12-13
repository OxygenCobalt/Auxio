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
    fun open(context: Context, ref: FileRef): Metadata? {
        val inputStream = AndroidInputStream(context, ref)
        val tag = openNative(inputStream)
        inputStream.close()
        return tag
    }

    private external fun openNative(ioStream: AndroidInputStream): Metadata?
}

data class FileRef(
    val fileName: String,
    val uri: Uri
)

data class Metadata(
    val id3v2: Map<String, String>,
    val xiph: Map<String, String>,
    val mp4: Map<String, String>,
    val cover: ByteArray?,
    val properties: Properties
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Metadata

        if (id3v2 != other.id3v2) return false
        if (xiph != other.xiph) return false
        if (mp4 != other.mp4) return false
        if (cover != null) {
            if (other.cover == null) return false
            if (!cover.contentEquals(other.cover)) return false
        } else if (other.cover != null) return false
        if (properties != other.properties) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id3v2.hashCode()
        result = 31 * result + xiph.hashCode()
        result = 31 * result + mp4.hashCode()
        result = 31 * result + (cover?.contentHashCode() ?: 0)
        result = 31 * result + properties.hashCode()
        return result
    }
}

data class Properties(
    val mimeType: String,
    val durationMs: Long,
    val bitrate: Int,
    val sampleRate: Int,
)
