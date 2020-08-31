package org.oxycblt.auxio.music.models

import android.text.format.DateUtils

// Class containing all relevant values for a song.
data class Song(
    val id: Long,
    val title: String,
    val albumName: String,
    val track: Int,
    val duration: Long
) {
    lateinit var album: Album

    val seconds = duration / 1000
    val formattedDuration = DateUtils.formatElapsedTime(seconds)
}
