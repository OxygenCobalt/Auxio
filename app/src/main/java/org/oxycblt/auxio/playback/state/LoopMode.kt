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
}
