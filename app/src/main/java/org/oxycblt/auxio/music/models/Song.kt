package org.oxycblt.auxio.music.models

import android.text.format.DateUtils

// Class containing all relevant values for a song.
data class Song(
    val id: Long,
    var name: String,
    val albumName: String, // Only used for sorting. Use album.title for everything else.
    val track: Int,
    val duration: Long
) {
    lateinit var album: Album

    val seconds = duration / 1000
    val formattedDuration = DateUtils.formatElapsedTime(seconds)
}
