package org.oxycblt.auxio.music.models

// Abstraction for Song
data class Album(
    var songs: List<Song>
) {
    var title: String? = null
    var artist: String? = null
    var year: Int = 0

    init {
        // Iterate through the child songs and inherit the first valid value
        // for the Album Name, Artist, and Year
        for (song in songs) {
            if (song.album != null) {
                title = song.album
            }

            if (song.artist != null) {
                artist = song.artist
            }

            if (song.year != 0) {
                year = song.year
            }
        }

        // Also sort the songs by track
        songs = songs.sortedBy { it.track }
    }
}
