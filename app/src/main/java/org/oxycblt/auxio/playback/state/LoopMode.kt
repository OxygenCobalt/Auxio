package org.oxycblt.auxio.playback.state

/**
 * Enum that determines the playback repeat mode.
 * @author OxygenCobalt
 */
enum class LoopMode {
    NONE, ALL, TRACK;

    /**
     * Increment the LoopMode, e.g from [NONE] to [ALL]
     */
    fun increment(): LoopMode {
        return when (this) {
            NONE -> ALL
            ALL -> TRACK
            TRACK -> NONE
        }
    }

    /**
     * Convert the LoopMode to an int constant that is saved in PlaybackStateDatabase
     * @return The int constant for this mode
     */
    fun toInt(): Int {
        return when (this) {
            NONE -> CONST_NONE
            ALL -> CONST_ALL
            TRACK -> CONST_TRACK
        }
    }

    companion object {
        const val CONST_NONE = 0xA100
        const val CONST_ALL = 0xA101
        const val CONST_TRACK = 0xA102

        /**
         * Convert an int [constant] into a LoopMode, or null if it isnt valid.
         */
        fun fromInt(constant: Int): LoopMode? {
            return when (constant) {
                CONST_NONE -> NONE
                CONST_ALL -> ALL
                CONST_TRACK -> TRACK

                else -> null
            }
        }
    }
}
