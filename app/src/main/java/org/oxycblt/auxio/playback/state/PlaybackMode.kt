package org.oxycblt.auxio.playback.state

// Enum that instructs how the queue should be constructed
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

        /**
         * Get a [PlaybackMode] for an int constant
         * @return The mode, null if there isnt one for this.
         */
        fun fromInt(constant: Int): PlaybackMode? {
            return when (constant) {
                CONSTANT_IN_ARTIST -> IN_ARTIST
                CONSTANT_IN_ALBUM -> IN_ALBUM
                CONSTANT_IN_GENRE -> IN_GENRE
                CONSTANT_ALL_SONGS -> ALL_SONGS

                else -> null
            }
        }

        /**
         * Get the value of a [PlaybackMode] from a string. Returns [ALL_SONGS] as a fallback.
         */
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
