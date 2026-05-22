package org.oxycblt.auxio.playback.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PendingIntentRequestCodePolicyTest {
    @Test
    fun `request code is deterministic for same action`() {
        val action = PlaybackActions.ACTION_SKIP_NEXT
        assertEquals(
            PendingIntentRequestCodePolicy.forAction(action),
            PendingIntentRequestCodePolicy.forAction(action),
        )
    }

    @Test
    fun `distinct actions produce distinct request codes`() {
        val a = PendingIntentRequestCodePolicy.forAction(PlaybackActions.ACTION_SKIP_PREV)
        val b = PendingIntentRequestCodePolicy.forAction(PlaybackActions.ACTION_SKIP_NEXT)
        assertNotEquals(a, b)
    }
}
