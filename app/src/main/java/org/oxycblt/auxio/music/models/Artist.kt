package org.oxycblt.auxio.music.models

// Abstraction for mAlbums
data class Artist(
    val id: Long = 0,
    val name: String? = null,
    val genres: MutableList<String?> = mutableListOf(null)
) {
    val albums = mutableListOf<Album>()
    var numAlbums = 0
}
