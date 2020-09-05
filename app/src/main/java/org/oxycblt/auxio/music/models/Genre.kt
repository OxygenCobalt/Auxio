package org.oxycblt.auxio.music.models

data class Genre(
    val id: Long = 0,
    var name: String = "",
) {
    val artists = mutableListOf<Artist>()
    var numArtists = 0

    fun finalize() {
        artists.sortByDescending { it.name }
        numArtists = artists.size
    }
}
