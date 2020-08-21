package org.oxycblt.auxio.music.models

// Abstraction for Song
data class Album(
    val id: Long = 0L,
    val title: String? = null,
    val artistName: String? = null,
    val year: Int = 0,
    var numSongs: Int = 0
) {
    lateinit var artist: Artist

    val songs = mutableListOf<Song>()
}
