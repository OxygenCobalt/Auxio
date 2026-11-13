/*
 * Copyright (c) 2026 Auxio Project
 * SleepTimerStateMachine.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package org.oxycblt.auxio.playback.service

import org.oxycblt.auxio.playback.SleepTimerState

/** Pure transitions for the persisted sleep-timer state. */
internal class SleepTimerStateMachine(initialState: SleepTimerState = SleepTimerState.Idle) {
    var state: SleepTimerState = initialState
        private set

    fun start(nowMillis: Long, durationMinutes: Int) {
        require(durationMinutes in MIN_DURATION_MINUTES..MAX_DURATION_MINUTES)
        state = SleepTimerState.CountingDown(nowMillis + durationMinutes * MILLIS_PER_MINUTE)
    }

    /** Pause cancels the timer rather than preserving or restarting it. */
    fun pause() {
        cancel()
    }

    fun cancel() {
        state = SleepTimerState.Idle
    }

    /** Returns true only when an expired timer should wait for a playing track to finish. */
    fun expire(nowMillis: Long, isPlaying: Boolean): Boolean {
        val countdown = state as? SleepTimerState.CountingDown ?: return false
        if (nowMillis < countdown.deadlineMillis) return false
        state =
            if (isPlaying) SleepTimerState.StopAfterCurrentTrack
            else SleepTimerState.Idle
        return true
    }

    /** A natural boundary consumes the pending stop; manual skips do not call this method. */
    fun finishCurrentTrack(): Boolean {
        if (state !is SleepTimerState.StopAfterCurrentTrack) return false
        state = SleepTimerState.Idle
        return true
    }

    private companion object {
        const val MIN_DURATION_MINUTES = 1
        const val MAX_DURATION_MINUTES = 180
        const val MILLIS_PER_MINUTE = 60_000L
    }
}
