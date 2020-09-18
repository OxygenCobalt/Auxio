package org.oxycblt.auxio.music.models

data class Genre(
    val id: Long = -1,
    var name: String,
) {
    val artists = mutableListOf<Artist>()

    fun finalize() {
        artists.sortByDescending { it.name }
    }
}
