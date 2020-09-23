package org.oxycblt.auxio.music.models

import android.net.Uri

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
}
