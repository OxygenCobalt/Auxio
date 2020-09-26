package org.oxycblt.auxio.music.models

// Abstraction for albums
data class Artist(
    val id: Long = -1,
    var name: String,
    val givenGenres: MutableList<Genre> = mutableListOf()
) {
    val albums = mutableListOf<Album>()
    lateinit var genres: Map<String, List<Genre>>

    val numAlbums: Int get() = albums.size
    val numSongs: Int
        get() {
            var num = 0
            albums.forEach {
                num += it.numSongs
            }
            return num
        }

    fun finalizeGenres() {
        genres = givenGenres.groupBy { it.name }
    }
}
