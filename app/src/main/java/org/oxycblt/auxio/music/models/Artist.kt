package org.oxycblt.auxio.music.models

// Abstraction for mAlbums
data class Artist(
    val id: Long = 0,
    var name: String = "",
    val genres: MutableList<Genre> = mutableListOf(Genre())
) {
    val albums = mutableListOf<Album>()

    val numAlbums: Int get() = albums.size
    var numSongs = 0

    fun finalize() {
        albums.sortByDescending { it.year }

        albums.forEach { album ->
            numSongs += album.numSongs
        }
    }
}
