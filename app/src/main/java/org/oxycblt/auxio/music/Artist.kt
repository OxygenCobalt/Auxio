package org.oxycblt.auxio.music

// Abstraction for mAlbums
data class Artist(
    private var albums: List<Album>
) {
    var name: String? = null
    var genre: String? = null

    init {
        // Like Album, iterate through the child albums and pick out the first valid
        // tag for Album/Genre
        for (album in albums) {
            if (album.artist != null) {
                name = album.artist
            }

            if (album.genre != null) {
                genre = album.genre
            }
        }

        // Also sort the mAlbums by year
        albums = albums.sortedBy { it.year }
    }
}
