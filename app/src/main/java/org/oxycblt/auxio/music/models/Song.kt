package org.oxycblt.auxio.music.models

import org.oxycblt.auxio.music.toDuration

// Class containing all relevant values for a song.
data class Song(
    val id: Long,
    var name: String,
    val albumId: Long,
    val track: Int,
    val duration: Long
) {
    lateinit var album: Album

    val seconds = duration / 1000
    val formattedDuration: String = seconds.toDuration()
}
