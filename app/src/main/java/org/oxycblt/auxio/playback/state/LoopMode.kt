package org.oxycblt.auxio.playback.state

/**
 * Enum that determines the playback repeat mode.
 * @author OxygenCobalt
 */
enum class LoopMode {
    NONE, ONCE, INFINITE;

    /**
     * Increment the LoopMode, e.g from [NONE] to [ONCE]
     */
    fun increment(): LoopMode {
        return when (this) {
            NONE -> ONCE
            ONCE -> INFINITE
            INFINITE -> NONE
        }
    }

    /**
     * Convert the LoopMode to an int constant that is saved in PlaybackStateDatabase
     * @return The int constant for this mode
     */
    fun toInt(): Int {
        return when (this) {
            NONE -> CONSTANT_NONE
            ONCE -> CONSTANT_ONCE
            INFINITE -> CONSTANT_INFINITE
        }
    }

    companion object {
        const val CONSTANT_NONE = 0xA050
        const val CONSTANT_ONCE = 0xA051
        const val CONSTANT_INFINITE = 0xA052

        /**
         * Convert an int [constant] into a LoopMode
         * @return The corresponding LoopMode. Null if it corresponds to nothing.
         */
        fun fromInt(constant: Int): LoopMode? {
            return when (constant) {
                CONSTANT_NONE -> NONE
                CONSTANT_ONCE -> ONCE
                CONSTANT_INFINITE -> INFINITE

                else -> null
            }
        }
    }
}
