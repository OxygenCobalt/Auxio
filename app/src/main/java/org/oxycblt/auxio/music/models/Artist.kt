package org.oxycblt.auxio.music.models

// Abstraction for albums
data class Artist(
    val id: Long = 0,
    var name: String = "",
    val genres: MutableList<Genre> = mutableListOf(Genre())
) {
    val albums = mutableListOf<Album>()
    var genre = ""

    val numAlbums: Int get() = albums.size
    var numSongs = 0

    fun finalize() {
        albums.sortByDescending { it.year }

        albums.forEach { album ->
            numSongs += album.numSongs
        }

        // If the artist has more than one genre, pick the most used one.
        genre = if (genres.size > 1) {
            val groupGenres = genres.groupBy { it.name }

            groupGenres.keys.sortedByDescending { groupGenres[it]?.size }[0]
        } else {
            genres[0].name
        }
    }
}
