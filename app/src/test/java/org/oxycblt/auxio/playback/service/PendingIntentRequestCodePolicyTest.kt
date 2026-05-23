package org.oxycblt.auxio.playback.service

import org.junit.Assert.assertTrue
import org.oxycblt.auxio.headunit.HeadUnitEntryPoints
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

    @Test
    fun `head-unit entry actions use unique request codes`() {
        val requestCodes =
            listOf(
                HeadUnitEntryPoints.ACTION_OPEN_NOW_PLAYING,
                HeadUnitEntryPoints.ACTION_SHUFFLE_ALL,
                HeadUnitEntryPoints.ACTION_OPEN_QUEUE,
                HeadUnitEntryPoints.ACTION_OPEN_RECENTLY_ADDED,
                HeadUnitEntryPoints.ACTION_OPEN_PLAYLISTS,
            ).map { PendingIntentRequestCodePolicy.forAction(it) }
        assertEquals(requestCodes.size, requestCodes.toSet().size)
        assertTrue(requestCodes.none { it == 0 })
    }
}
