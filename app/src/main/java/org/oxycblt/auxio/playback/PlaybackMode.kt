package org.oxycblt.auxio.playback

// Enum for instruction how the queue should function.
// ALL SONGS -> Play from all songs
// IN_GENRE -> Play from the genre
// IN_ARTIST -> Play from the songs of the artist
// IN_ALBUM -> Play from the songs of the album
enum class PlaybackMode {
    IN_ARTIST, IN_ALBUM, ALL_SONGS;

    // Make a slice of all the values that this ShowMode covers.
    // ex. SHOW_ARTISTS would return SHOW_ARTISTS, SHOW_ALBUMS, and SHOW_SONGS
    fun getChildren(): List<PlaybackMode> {
        val vals = values()

        return vals.slice(vals.indexOf(this) until vals.size)
    }
}
