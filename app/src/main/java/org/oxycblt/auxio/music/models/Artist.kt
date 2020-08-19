package org.oxycblt.auxio.music.models

// Abstraction for mAlbums
data class Artist(
    private var albums: List<Album>
) {
    var name: String? = null

    init {
        // Like Album, iterate through the child albums and pick out the first valid for artist
        for (album in albums) {
            if (album.artist != null) {
                name = album.artist
            }
        }

        // Also sort the mAlbums by year
        albums = albums.sortedBy { it.year }
    }
}
