package org.oxycblt.auxio.playback

// Enum for instruction how the queue should function.
// ALL SONGS -> Play from all songs
// IN_GENRE -> Play from the genre
// IN_ARTIST -> Play from the songs of the artist
// IN_ALBUM -> Play from the songs of the album
enum class PlaybackMode {
    IN_ARTIST, IN_ALBUM, ALL_SONGS;
}
