package org.oxycblt.auxio.music.models

// Class containing all relevant values for a song.
data class Song(
    val id: Long,
    val name: String?,
    val artist: String?,
    val album: String?,
    val year: Int,
    val track: Int,
    val duration: Long,
    val coverData: ByteArray? = null
)
