package org.oxycblt.auxio.music.models

import android.net.Uri

// Abstraction for Song
data class Album(
    val id: Long = 0L,
    var name: String = "",
    val artistName: String = "", // only used for sorting. Use artist.name instead.
    val coverUri: Uri = Uri.EMPTY,
    val year: String = ""
) {
    lateinit var artist: Artist

    val songs = mutableListOf<Song>()
    val numSongs: Int get() = songs.size

    fun finalize() {
        songs.sortBy { it.track }
    }
}
