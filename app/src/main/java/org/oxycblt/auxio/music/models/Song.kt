package org.oxycblt.auxio.music.models

import android.text.format.DateUtils
import org.oxycblt.auxio.music.removeDurationZeroes

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
    val formattedDuration: String = DateUtils.formatElapsedTime(seconds).removeDurationZeroes()
}
