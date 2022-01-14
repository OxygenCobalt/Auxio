package org.oxycblt.auxio.playback.system

enum class ReplayGainMode {
    OFF,
    TRACK,
    ALBUM,
    DYNAMIC;

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
