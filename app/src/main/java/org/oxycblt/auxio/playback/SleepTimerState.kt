package org.oxycblt.auxio.playback

/** State shared by the playback service and timer widgets. */
sealed interface SleepTimerState {
    data object Idle : SleepTimerState

    /** The absolute wall-clock deadline; widgets translate this to a monotonic chronometer base. */
    data class CountingDown(val deadlineMillis: Long) : SleepTimerState

    /** The countdown elapsed and playback must stop at the next natural track boundary. */
    data object StopAfterCurrentTrack : SleepTimerState
}
