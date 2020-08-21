package org.oxycblt.auxio.music.models

import android.graphics.Bitmap

// Abstraction for Song
data class Album(
    val id: Long = 0L,
    val title: String = "",
    val artistName: String = "",
    val cover: Bitmap? = null,
    val year: Int = 0,
    var numSongs: Int = 0
) {
    lateinit var artist: Artist

    val songs = mutableListOf<Song>()
}
