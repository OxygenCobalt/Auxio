package org.oxycblt.auxio.playback.service

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class PendingIntentRequestCodePolicyTest {
    @Test
    fun `same action returns stable request code`() {
        val action = "org.oxycblt.auxio.action.OPEN_QUEUE"
        assertEquals(
            PendingIntentRequestCodePolicy.forAction(action),
            PendingIntentRequestCodePolicy.forAction(action),
        )
    }

    @Test
    fun `different actions use different request codes`() {
        assertNotEquals(
            PendingIntentRequestCodePolicy.forAction("a"),
            PendingIntentRequestCodePolicy.forAction("b"),
        )
    }
}
