package org.oxycblt.auxio.music

// Class containing all relevant values for a song
// TODO: Is broken into Artist, Album, and Song classes
data class RawMusic(
    val name: String?,
    val artist: String?,
    val album: String?,
    val year: Int,
    val track: Int,
    val duration: Long,
    val id: Long?
)
