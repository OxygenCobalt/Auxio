package org.oxycblt.auxio.music.models

import android.net.Uri
import org.oxycblt.auxio.music.toDuration

// Abstraction for Song
data class Album(
    val id: Long = -1,
    var name: String,
    val artistName: String,
    val coverUri: Uri = Uri.EMPTY,
    val year: Int = 0
) {
    lateinit var artist: Artist

    val songs = mutableListOf<Song>()
    val numSongs: Int get() = songs.size
    val totalDuration: String get() {
        var seconds: Long = 0
        songs.forEach {
            seconds += it.seconds
        }
        return seconds.toDuration()
    }
}
