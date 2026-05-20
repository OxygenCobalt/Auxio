package org.oxycblt.auxio.playback.service

/**
 * Pure decision helper for standard Android audio focus transitions.
 */
object AudioFocusPolicy {
    private const val DUCKED_VOLUME = 0.2f
    private const val NORMAL_VOLUME = 1f

    data class State(
        val wasPlayingBeforeTransientLoss: Boolean = false,
    )

    enum class Event {
        LOSS,
        LOSS_TRANSIENT,
        LOSS_TRANSIENT_CAN_DUCK,
        GAIN,
    }

    data class Decision(
        val pause: Boolean = false,
        val resume: Boolean = false,
        val volume: Float = NORMAL_VOLUME,
        val rememberTransientPlayback: Boolean? = null,
    )

    fun decide(event: Event, state: State, isPlaying: Boolean): Decision =
        when (event) {
            Event.LOSS ->
                Decision(
                    pause = true,
                    volume = NORMAL_VOLUME,
                    rememberTransientPlayback = false,
                )
            Event.LOSS_TRANSIENT ->
                Decision(
                    pause = true,
                    volume = NORMAL_VOLUME,
                    rememberTransientPlayback = isPlaying,
                )
            Event.LOSS_TRANSIENT_CAN_DUCK ->
                if (isPlaying) {
                    Decision(
                        pause = false,
                        volume = DUCKED_VOLUME,
                        rememberTransientPlayback = true,
                    )
                } else {
                    Decision(
                        pause = false,
                        volume = NORMAL_VOLUME,
                    )
                }
            Event.GAIN ->
                Decision(
                    resume = state.wasPlayingBeforeTransientLoss,
                    volume = NORMAL_VOLUME,
                    rememberTransientPlayback = false,
                )
        }
}
