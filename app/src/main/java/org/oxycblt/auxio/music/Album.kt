package org.oxycblt.auxio.music

import android.graphics.Bitmap

// Basic Abstraction for Song
data class Album(
    var songs: List<Song>
) {
    var title: String? = null
    var artist: String? = null
    var genre: String? = null
    var cover: Bitmap? = null
    var year: Int = 0

    init {
        // Iterate through the child songs and inherit the first valid value
        // for the Album Name, Artist, Genre, Year, and Cover
        for (song in songs) {
            if (song.album != null) {
                title = song.album
            }

            if (song.artist != null) {
                artist = song.artist
            }

            if (song.genre != null) {
                genre = song.genre
            }

            if (song.cover != null) {
                cover = song.cover
            }

            if (song.year != 0) {
                year = song.year
            }
        }

        // Also sort the songs by track
        songs = songs.sortedBy { it.track }
    }
}
