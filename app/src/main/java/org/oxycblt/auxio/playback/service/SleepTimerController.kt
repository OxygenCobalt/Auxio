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
    private val stateMachine = SleepTimerStateMachine(readPersistedState())
    private var timerJob: Job? = null
    private var hasObservedPlayback = false

    val state: SleepTimerState
        get() = stateMachine.state

    /** Starts a timer while audio is playing, or cancels an existing timer. */
    fun toggle(): Boolean {
        if (state !is SleepTimerState.Idle) {
            cancel()
            return true
        }
        if (!playbackManager.progression.isPlaying) return false

        hasObservedPlayback = true
        stateMachine.start(System.currentTimeMillis(), playbackSettings.sleepTimerMinutes)
        persist()
        val deadline = (state as SleepTimerState.CountingDown).deadlineMillis
        schedule(deadline)
        publish()
        return true
    }

    /** Pausing playback cancels both the countdown and any deferred stop. */
    fun onPlaybackChanged(isPlaying: Boolean) {
        if (isPlaying) {
            hasObservedPlayback = true
            val countdown = state as? SleepTimerState.CountingDown
            if (countdown != null && timerJob == null) schedule(countdown.deadlineMillis)
            if (state !is SleepTimerState.Idle) publish()
        } else if (hasObservedPlayback && state !is SleepTimerState.Idle) {
            stateMachine.pause()
            clearJobAndPersist()
            publish()
        } else if (state !is SleepTimerState.Idle) {
            // Preserve persisted state until restored playback tells us whether it is resuming.
            publish()
        }
    }

    /** Ends playback at a natural track boundary if the countdown already expired. */
    fun onNaturalTrackEnded(): Boolean {
        if (!stateMachine.finishCurrentTrack()) return false
        clearJobAndPersist()
        publish()
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
        timerJob = null
        val remaining = deadline - System.currentTimeMillis()
        if (remaining <= 0L) {
            expire(deadline)
            return
        }

        timerJob =
            scope.launch {
                delay(remaining)
                timerJob = null
                expire(deadline)
            }
    }

    private fun expire(deadline: Long) {
        val countdown = state as? SleepTimerState.CountingDown ?: return
        if (countdown.deadlineMillis != deadline) return
        if (stateMachine.expire(System.currentTimeMillis(), playbackManager.progression.isPlaying)) {
            persist()
            publish()
        }
    }

    private fun cancel() {
        if (state is SleepTimerState.Idle) return
        stateMachine.cancel()
        clearJobAndPersist()
        publish()
    }

    private fun clearJobAndPersist() {
        timerJob?.cancel()
        timerJob = null
        persist()
    }

    private fun persist() {
        preferences.edit {
            when (val current = state) {
                SleepTimerState.Idle -> {
                    remove(KEY_DEADLINE_MILLIS)
                    remove(KEY_STOP_AFTER_TRACK)
                }
                is SleepTimerState.CountingDown -> {
                    putLong(KEY_DEADLINE_MILLIS, current.deadlineMillis)
                    remove(KEY_STOP_AFTER_TRACK)
                }
                SleepTimerState.StopAfterCurrentTrack -> {
                    remove(KEY_DEADLINE_MILLIS)
                    putBoolean(KEY_STOP_AFTER_TRACK, true)
                }
            }
        }
    }

    private fun readPersistedState(): SleepTimerState =
        when {
            preferences.getBoolean(KEY_STOP_AFTER_TRACK, false) ->
                SleepTimerState.StopAfterCurrentTrack
            preferences.getLong(KEY_DEADLINE_MILLIS, 0L) > 0L ->
                SleepTimerState.CountingDown(preferences.getLong(KEY_DEADLINE_MILLIS, 0L))
            else -> SleepTimerState.Idle
        }

    private fun publish() = onStateChanged(state)

    private companion object {
        const val PREFERENCES_NAME = "sleep_timer_state"
        const val KEY_DEADLINE_MILLIS = "deadline_wall_clock_ms"
        const val KEY_STOP_AFTER_TRACK = "stop_after_current_track"
    }
}
