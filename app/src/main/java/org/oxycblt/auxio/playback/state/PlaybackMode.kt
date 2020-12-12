package org.oxycblt.auxio.playback.state

import java.lang.IllegalArgumentException

// Enum for instruction how the queue should function.
// ALL SONGS -> Play from all songs
// IN_ARTIST -> Play from the songs of the artist
// IN_ALBUM -> Play from the songs of the album
enum class PlaybackMode {
    IN_ARTIST, IN_GENRE, IN_ALBUM, ALL_SONGS;

    fun toInt(): Int {
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
        const val CONSTANT_IN_ALBUM = 0xA042
        const val CONSTANT_ALL_SONGS = 0xA043

        fun fromInt(constant: Int): PlaybackMode? {
            return when (constant) {
                CONSTANT_IN_ARTIST -> IN_ARTIST
                CONSTANT_IN_ALBUM -> IN_ALBUM
                CONSTANT_IN_GENRE -> IN_GENRE
                CONSTANT_ALL_SONGS -> ALL_SONGS

                else -> null
            }
        }

        fun valueOfOrFallback(value: String?): PlaybackMode {
            if (value == null) {
                return ALL_SONGS
            }

            return try {
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                ALL_SONGS
            }
        }
    }
}
