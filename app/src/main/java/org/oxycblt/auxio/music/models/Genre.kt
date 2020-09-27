package org.oxycblt.auxio.music.models

data class Genre(
    val id: Long = -1,
    var name: String,
) {
    val artists = mutableListOf<Artist>()

    val numArtists: Int get() = artists.size
    val numAlbums: Int get() {
        var num = 0
        artists.forEach {
            num += it.numAlbums
        }
        return num
    }
}
