package org.oxycblt.auxio.playback.state

// Enum for instruction how the queue should function.
// ALL SONGS -> Play from all songs
// IN_ARTIST -> Play from the songs of the artist
// IN_ALBUM -> Play from the songs of the album
enum class PlaybackMode {
    IN_ARTIST, IN_GENRE, IN_ALBUM, ALL_SONGS;

    fun toConstant(): Int {
        return when (this) {
            IN_ARTIST -> CONSTANT_IN_ARTIST
            IN_GENRE -> CONSTANT_IN_GENRE
            IN_ALBUM -> CONSTANT_IN_ALBUM
            ALL_SONGS -> CONSTANT_ALL_SONGS
        }
    }

    companion object {
        const val CONSTANT_IN_ARTIST = 0xA040
        const val CONSTANT_IN_GENRE = 0xA041
        const val CONSTANT_IN_ALBUM = 0x4042
        const val CONSTANT_ALL_SONGS = 0x4043

        fun fromConstant(constant: Int): PlaybackMode? {
            return when (constant) {
                CONSTANT_IN_ARTIST -> IN_ARTIST
                CONSTANT_IN_ALBUM -> IN_ALBUM
                CONSTANT_IN_GENRE -> IN_GENRE
                CONSTANT_ALL_SONGS -> ALL_SONGS

                else -> null
            }
        }
    }
}
