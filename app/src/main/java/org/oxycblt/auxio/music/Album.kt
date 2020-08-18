package org.oxycblt.auxio.music

// Basic Abstraction for Song
data class Album (
    var mSongs: List<Song>
) {
    private var mTitle: String? = null
    private var mArtist: String? = null
    //private var mGenre: String? = null
    private var mYear: Int = 0

    // Immutable backings as the member variables are mutable
    val title: String? get() = mTitle
    val artist: String? get() = mArtist
    //val genre: String? get() = genre
    val year: Int get() = mYear

    val songs: List<Song> get() = mSongs

    init {
        // Iterate through the child songs and inherit the first valid value
        // for the Album name & year, otherwise it will revert to its defaults
        for (song in mSongs) {
            if (song.album != null) {
                mTitle = song.album
            }

            if (song.artist != null) {
                mArtist = song.artist
            }

            /*
            if (song.genre != null) {
                mGenre = song.genre
            }
             */

            if (song.year != 0) {
                mYear = song.year
            }
        }

        // Also sort the songs by track
        mSongs = songs.sortedBy { it.track }
    }
}