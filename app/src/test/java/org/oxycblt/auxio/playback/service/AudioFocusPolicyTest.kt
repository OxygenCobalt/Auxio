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
}

