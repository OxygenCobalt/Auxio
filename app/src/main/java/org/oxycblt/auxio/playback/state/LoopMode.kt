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
            NONE -> CONST_NONE
            ONCE -> CONST_ONCE
            INFINITE -> CONST_INFINITE
        }
    }

    companion object {
        const val CONST_NONE = 0xA100
        const val CONST_ONCE = 0xA101
        const val CONST_INFINITE = 0xA102

        /**
         * Convert an int [constant] into a LoopMode
         * @return The corresponding LoopMode. Null if it corresponds to nothing.
         */
        fun fromInt(constant: Int): LoopMode? {
            return when (constant) {
                CONST_NONE -> NONE
                CONST_ONCE -> ONCE
                CONST_INFINITE -> INFINITE

                else -> null
            }
        }
    }
}
