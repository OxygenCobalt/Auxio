package org.oxycblt.auxio.music

// Class containing all relevant values for a song.
data class Song(
    val name: String?,
    val artist: String?,
    val album: String?,
    //val genre: String?,
    val year: Int,
    val track: Int,
    val duration: Long,
    val id: Long?
)

