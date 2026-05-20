package org.oxycblt.auxio.playback.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AudioFocusPolicyTest {
    @Test
    fun `duck event while playing lowers volume and remembers playback`() {
        val decision =
            AudioFocusPolicy.decide(
                AudioFocusPolicy.Event.LOSS_TRANSIENT_CAN_DUCK,
                AudioFocusPolicy.State(),
                isPlaying = true,
            )

        assertFalse(decision.pause)
        assertEquals(0.2f, decision.volume)
        assertEquals(true, decision.rememberTransientPlayback)
    }

    @Test
    fun `duck event while not playing keeps volume and does not remember playback`() {
        val decision =
            AudioFocusPolicy.decide(
                AudioFocusPolicy.Event.LOSS_TRANSIENT_CAN_DUCK,
                AudioFocusPolicy.State(),
                isPlaying = false,
            )

        assertFalse(decision.pause)
        assertEquals(1f, decision.volume)
        assertEquals(null, decision.rememberTransientPlayback)
    }

    @Test
    fun `transient loss while playing pauses and remembers playback`() {
        val decision =
            AudioFocusPolicy.decide(
                AudioFocusPolicy.Event.LOSS_TRANSIENT,
                AudioFocusPolicy.State(),
                isPlaying = true,
            )

        assertTrue(decision.pause)
        assertEquals(1f, decision.volume)
        assertEquals(true, decision.rememberTransientPlayback)
    }

    @Test
    fun `transient loss while not playing pauses and does not remember playback`() {
        val decision =
            AudioFocusPolicy.decide(
                AudioFocusPolicy.Event.LOSS_TRANSIENT,
                AudioFocusPolicy.State(),
                isPlaying = false,
            )

        assertTrue(decision.pause)
        assertEquals(1f, decision.volume)
        assertEquals(false, decision.rememberTransientPlayback)
    }

    @Test
    fun `gain resumes only when previously active`() {
        val resumeDecision =
            AudioFocusPolicy.decide(
                AudioFocusPolicy.Event.GAIN,
                AudioFocusPolicy.State(wasPlayingBeforeTransientLoss = true),
                isPlaying = false,
            )
        val noResumeDecision =
            AudioFocusPolicy.decide(
                AudioFocusPolicy.Event.GAIN,
                AudioFocusPolicy.State(wasPlayingBeforeTransientLoss = false),
                isPlaying = false,
            )

        assertTrue(resumeDecision.resume)
        assertFalse(noResumeDecision.resume)
    }

    @Test
    fun `permanent loss pauses and clears resume memory`() {
        val decision =
            AudioFocusPolicy.decide(
                AudioFocusPolicy.Event.LOSS,
                AudioFocusPolicy.State(wasPlayingBeforeTransientLoss = true),
                isPlaying = true,
            )

        assertTrue(decision.pause)
        assertFalse(decision.resume)
        assertEquals(1f, decision.volume)
        assertEquals(false, decision.rememberTransientPlayback)
    }

    @Test
    fun `loss gain churn does not retain stale resume state`() {
        val afterTransient =
            AudioFocusPolicy.decide(
                AudioFocusPolicy.Event.LOSS_TRANSIENT,
                AudioFocusPolicy.State(),
                isPlaying = true,
            )
        val stateAfterTransient =
            AudioFocusPolicy.State(
                wasPlayingBeforeTransientLoss = afterTransient.rememberTransientPlayback ?: false
            )
        val afterGain =
            AudioFocusPolicy.decide(AudioFocusPolicy.Event.GAIN, stateAfterTransient, isPlaying = false)
        val stateAfterGain =
            AudioFocusPolicy.State(
                wasPlayingBeforeTransientLoss = afterGain.rememberTransientPlayback ?: false
            )
        val secondGain =
            AudioFocusPolicy.decide(AudioFocusPolicy.Event.GAIN, stateAfterGain, isPlaying = false)

        assertTrue(afterGain.resume)
        assertEquals(false, afterGain.rememberTransientPlayback)
        assertFalse(secondGain.resume)
    }
}
