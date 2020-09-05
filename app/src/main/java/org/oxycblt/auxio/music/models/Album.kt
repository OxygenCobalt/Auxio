package org.oxycblt.auxio.music.models

import android.net.Uri

// Abstraction for Song
data class Album(
    val id: Long = 0L,
    var title: String = "",
    val artistName: String = "", // Only used for sorting. Use artist for everything else.
    val coverUri: Uri = Uri.EMPTY,
    val year: Int = 0,
    var numSongs: Int = 0
) {
    lateinit var artist: Artist

    val songs = mutableListOf<Song>()

    fun finalize() {
        songs.sortBy { it.track }
    }
}
