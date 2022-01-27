package org.oxycblt.auxio.playback.system

/**
 * Represents the current setting for ReplayGain.
 */
enum class ReplayGainMode {
    /** Do not apply ReplayGain. */
    OFF,
    /** Apply the track gain, falling back to the album gain if the track gain is not found. */
    TRACK,
    /** Apply the album gain, falling back to the track gain if the album gain is not found. */
    ALBUM,
    /** Apply the album gain only when playing from an album, defaulting to track gain otherwise. */
    DYNAMIC;

    /**
     * Converts this type to an integer constant.
     */
    fun toInt(): Int {
        return when (this) {
            OFF -> INT_OFF
            TRACK -> INT_TRACK
            ALBUM -> INT_ALBUM
            DYNAMIC -> INT_DYNAMIC
        }
    }

    companion object {
        private const val INT_OFF = 0xA110
        private const val INT_TRACK = 0xA111
        private const val INT_ALBUM = 0xA112
        private const val INT_DYNAMIC = 0xA113

        /**
         * Converts an integer constant to this type.
         */
        fun fromInt(value: Int): ReplayGainMode? {
            return when (value) {
                INT_OFF -> OFF
                INT_TRACK -> TRACK
                INT_ALBUM -> ALBUM
                INT_DYNAMIC -> DYNAMIC
                else -> null
            }
        }
    }
}
