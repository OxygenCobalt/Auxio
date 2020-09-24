package org.oxycblt.auxio.music.models

// Abstraction for albums
data class Artist(
    val id: Long = -1,
    var name: String,
    val genres: MutableList<Genre> = mutableListOf()
) {
    val albums = mutableListOf<Album>()
    var genre = ""

    val numAlbums: Int get() = albums.size
    val numSongs: Int
        get() {
            var num = 0
            albums.forEach {
                num += it.numSongs
            }
            return num
        }

    fun finalizeGenre() {
        // If the artist has more than one genre, pick the most "Prominent" one.
        genre = if (genres.size > 1) {
            val groupGenres = genres.groupBy { it.name }

            groupGenres.keys.sortedByDescending { groupGenres[it]?.size }[0]
        } else {
            genres[0].name
        }
    }
}
