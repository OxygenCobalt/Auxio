package org.oxycblt.auxio.music.models

// Class containing all relevant values for a song.
data class Song(
    val id: Long,
    val title: String,
    val albumName: String,
    val track: Int,
    val duration: Long
) {
    lateinit var album: Album
}
