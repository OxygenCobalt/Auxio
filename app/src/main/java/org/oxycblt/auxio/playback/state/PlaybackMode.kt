package org.oxycblt.auxio.playback.state

/**
 * Enum that indicates how the queue should be constructed.
 * @author OxygenCobalt
 */
enum class PlaybackMode {
    /** Construct the queue from the genre's songs */
    IN_GENRE,
    /** Construct the queue from the artist's songs */
    IN_ARTIST,
    /** Construct the queue from the album's songs */
    IN_ALBUM,
    /** Construct the queue from all songs */
    ALL_SONGS;

    /**
     * Convert the mode into an int constant, to be saved in PlaybackStateDatabase
     * @return The constant for this mode,
     */
    fun toInt(): Int {
        return when (this) {
            IN_ARTIST -> CONST_IN_ARTIST
            IN_GENRE -> CONST_IN_GENRE
            IN_ALBUM -> CONST_IN_ALBUM
            ALL_SONGS -> CONST_ALL_SONGS
        }
    }

    companion object {
        const val CONST_IN_GENRE = 0xA103
        const val CONST_IN_ARTIST = 0xA104
        const val CONST_IN_ALBUM = 0xA105
        const val CONST_ALL_SONGS = 0xA106

        /**
         * Get a [PlaybackMode] for an int [constant]
         * @return The mode, null if there isnt one for this.
         */
        fun fromInt(constant: Int): PlaybackMode? {
            return when (constant) {
                CONST_IN_ARTIST -> IN_ARTIST
                CONST_IN_ALBUM -> IN_ALBUM
                CONST_IN_GENRE -> IN_GENRE
                CONST_ALL_SONGS -> ALL_SONGS

                else -> null
            }
        }
    }
}
