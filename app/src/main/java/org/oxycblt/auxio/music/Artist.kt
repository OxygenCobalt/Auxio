package org.oxycblt.auxio.music

// Abstraction for mAlbums
data class Artist(
    private var mAlbums: List<Album>
) {
    private var mName: String? = null
    //private var mGenre: String? = null

    // Immutable backings as the member variables are mutable
    val name: String? get() = mName
    //val genre: String? get() = mGenre

    val albums: List<Album> get() = mAlbums

    init {
        // Like album, iterate through the child albums and pick out the first valid
        // tag for Album/Genre
        for (album in mAlbums) {
            if (album.artist != null) {
                mName = album.artist
            }

            /*
            if (album.genre != null) {
                mGenre = album.genre
            }
             */
        }

        // Also sort the mAlbums by year
        mAlbums = mAlbums.sortedBy { it.year }
    }
}