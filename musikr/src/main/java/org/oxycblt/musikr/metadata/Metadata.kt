package org.oxycblt.musikr.metadata

internal data class Metadata(
    val id3v2: Map<String, List<String>>,
    val xiph: Map<String, List<String>>,
    val mp4: Map<String, List<String>>,
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
