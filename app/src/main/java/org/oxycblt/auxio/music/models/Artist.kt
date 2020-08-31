package org.oxycblt.auxio.music.models

// Abstraction for mAlbums
data class Artist(
    val id: Long = 0,
    var name: String = "",
    val genres: MutableList<Genre> = mutableListOf(Genre())
) {
    val albums = mutableListOf<Album>()
    var numAlbums = 0
}
