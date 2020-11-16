package org.oxycblt.auxio.playback.state

enum class LoopMode {
    NONE, ONCE, INFINITE;

    fun increment(): LoopMode {
        return when (this) {
            NONE -> ONCE
            ONCE -> INFINITE
            INFINITE -> NONE
        }
    }

    fun toConstant(): Int {
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

        fun fromConstant(constant: Int): LoopMode? {
            return when (constant) {
                CONSTANT_NONE -> NONE
                CONSTANT_ONCE -> ONCE
                CONSTANT_INFINITE -> INFINITE

                else -> null
            }
        }
    }
}
