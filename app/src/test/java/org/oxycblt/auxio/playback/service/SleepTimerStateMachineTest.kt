package org.oxycblt.auxio.playback.service

import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.oxycblt.auxio.playback.SleepTimerState

@OptIn(UnstableApi::class)
class SleepTimerStateMachineTest {
    @Test
    fun startsWithConfiguredDuration() {
        val machine = SleepTimerStateMachine()

        machine.start(nowMillis = 1_000L, durationMinutes = 15)

        assertEquals(
            SleepTimerState.CountingDown(901_000L),
            machine.state,
        )
    }

    @Test
    fun pauseCancelsCountdownAndPendingStop() {
        val machine = SleepTimerStateMachine(SleepTimerState.StopAfterCurrentTrack)

        machine.pause()

        assertEquals(SleepTimerState.Idle, machine.state)
    }

    @Test
    fun expiryWhilePlayingWaitsForNaturalBoundary() {
        val machine = SleepTimerStateMachine()
        machine.start(nowMillis = 0L, durationMinutes = 1)

        assertTrue(machine.expire(nowMillis = 60_000L, isPlaying = true))
        assertEquals(SleepTimerState.StopAfterCurrentTrack, machine.state)
        assertTrue(machine.finishCurrentTrack())
        assertEquals(SleepTimerState.Idle, machine.state)
    }

    @Test
    fun expiryWhileNotPlayingDoesNotLeavePendingStop() {
        val machine = SleepTimerStateMachine()
        machine.start(nowMillis = 0L, durationMinutes = 1)

        assertTrue(machine.expire(nowMillis = 60_000L, isPlaying = false))
        assertEquals(SleepTimerState.Idle, machine.state)
        assertFalse(machine.finishCurrentTrack())
    }

    @Test
    fun expiredTimerDoesNotConsumePendingStopBeforeDeadline() {
        val machine = SleepTimerStateMachine()
        machine.start(nowMillis = 0L, durationMinutes = 1)

        assertFalse(machine.expire(nowMillis = 59_999L, isPlaying = true))
        assertEquals(SleepTimerState.CountingDown(60_000L), machine.state)
    }

    @Test
    fun persistsStateForRestoration() {
        val expected = SleepTimerState.CountingDown(123_456L)
        val machine = SleepTimerStateMachine(expected)

        assertEquals(expected, machine.state)
    }

    @Test
    fun onlyNaturalTransitionsAreTrackBoundaries() {
        assertTrue(isNaturalTrackBoundary(Player.MEDIA_ITEM_TRANSITION_REASON_AUTO))
        assertTrue(isNaturalTrackBoundary(Player.MEDIA_ITEM_TRANSITION_REASON_REPEAT))
        assertFalse(isNaturalTrackBoundary(Player.MEDIA_ITEM_TRANSITION_REASON_SEEK))
        assertFalse(isNaturalTrackBoundary(Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED))
    }
}
