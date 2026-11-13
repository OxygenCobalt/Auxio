/*
 * Copyright (c) 2026 Auxio Project
 * SleepTimerController.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package org.oxycblt.auxio.playback.service

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.playback.SleepTimerState
import org.oxycblt.auxio.playback.state.PlaybackStateManager

/** Owns the one sleep timer for the active playback session. */
internal class SleepTimerController(
    context: Context,
    private val playbackManager: PlaybackStateManager,
    private val playbackSettings: PlaybackSettings,
    private val scope: CoroutineScope,
    private val onStateChanged: (SleepTimerState) -> Unit,
) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private var deadlineMillis = preferences.getLong(KEY_DEADLINE_MILLIS, 0L)
    private var stopAfterCurrentTrack = preferences.getBoolean(KEY_STOP_AFTER_TRACK, false)
    private var timerJob: Job? = null
    private var hasObservedPlayback = false

    val state: SleepTimerState
        get() =
            when {
                stopAfterCurrentTrack -> SleepTimerState.StopAfterCurrentTrack
                deadlineMillis > 0L -> SleepTimerState.CountingDown(deadlineMillis)
                else -> SleepTimerState.Idle
            }

    /** Starts a timer while audio is playing, or cancels an existing timer. */
    fun toggle(): Boolean {
        if (deadlineMillis > 0L || stopAfterCurrentTrack) {
            cancel()
            return true
        }
        if (!playbackManager.progression.isPlaying) return false

        hasObservedPlayback = true
        deadlineMillis =
            System.currentTimeMillis() + playbackSettings.sleepTimerMinutes * MILLIS_PER_MINUTE
        stopAfterCurrentTrack = false
        persist()
        schedule(deadlineMillis)
        publish()
        return true
    }

    /** Pausing playback cancels both the countdown and any deferred stop. */
    fun onPlaybackChanged(isPlaying: Boolean) {
        if (isPlaying) {
            hasObservedPlayback = true
            if (deadlineMillis > 0L && timerJob == null) {
                schedule(deadlineMillis)
            }
            if (state !is SleepTimerState.Idle) publish()
        } else if (hasObservedPlayback && state !is SleepTimerState.Idle) {
            cancel()
        } else if (state !is SleepTimerState.Idle) {
            // Preserve a persisted timer until restored playback tells us whether it is resuming.
            publish()
        }
    }

    /** Ends playback at a natural track boundary if the countdown already expired. */
    fun onNaturalTrackEnded(): Boolean {
        if (!stopAfterCurrentTrack) return false
        clearState()
        playbackManager.endSession()
        return true
    }

    /** Clears timer state when playback ends for any other reason. */
    fun onSessionEnded() {
        cancel()
    }

    /** Stop the coroutine when the service releases; persisted state can be restored on reattach. */
    fun release() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun schedule(deadline: Long) {
        timerJob?.cancel()
        val remaining = deadline - System.currentTimeMillis()
        if (remaining <= 0L) {
            expire(deadline)
            return
        }

        timerJob =
            scope.launch {
                delay(remaining)
                timerJob = null
                if (deadlineMillis == deadline) expire(deadline)
            }
    }

    private fun expire(deadline: Long) {
        if (deadlineMillis != deadline) return
        if (!playbackManager.progression.isPlaying) {
            cancel()
            return
        }

        deadlineMillis = 0L
        stopAfterCurrentTrack = true
        persist()
        publish()
    }

    private fun cancel() {
        if (state is SleepTimerState.Idle) return
        clearState()
    }

    private fun clearState() {
        timerJob?.cancel()
        timerJob = null
        deadlineMillis = 0L
        stopAfterCurrentTrack = false
        preferences.edit {
            remove(KEY_DEADLINE_MILLIS)
            remove(KEY_STOP_AFTER_TRACK)
        }
        publish()
    }

    private fun persist() {
        preferences.edit {
            if (deadlineMillis > 0L) putLong(KEY_DEADLINE_MILLIS, deadlineMillis)
            else remove(KEY_DEADLINE_MILLIS)
            putBoolean(KEY_STOP_AFTER_TRACK, stopAfterCurrentTrack)
        }
    }

    private fun publish() = onStateChanged(state)

    private companion object {
        const val PREFERENCES_NAME = "sleep_timer_state"
        const val KEY_DEADLINE_MILLIS = "deadline_wall_clock_ms"
        const val KEY_STOP_AFTER_TRACK = "stop_after_current_track"
        const val MILLIS_PER_MINUTE = 60_000L
    }
}
