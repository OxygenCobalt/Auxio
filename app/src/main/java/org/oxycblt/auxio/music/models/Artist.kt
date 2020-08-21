package org.oxycblt.auxio.music.models

// Abstraction for mAlbums
data class Artist(
    val id: Long = 0,
    val name: String = "",
    val genres: MutableList<String> = mutableListOf("")
) {
    val albums = mutableListOf<Album>()
    var numAlbums = 0
}
