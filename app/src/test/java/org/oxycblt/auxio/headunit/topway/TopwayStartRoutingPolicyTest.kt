package org.oxycblt.auxio.headunit.topway

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TopwayStartRoutingPolicyTest {
    @Test
    fun `known commands map only when a current song exists`() {
        assertEquals(TopwayServiceAction.PREVIOUS, decide(TopwayMusicContract.ACTION_PREV, hasSong = true).action)
        assertEquals(TopwayServiceAction.NEXT, decide(TopwayMusicContract.ACTION_NEXT, hasSong = true).action)
        assertEquals(TopwayServiceAction.PLAY_PAUSE, decide(TopwayMusicContract.ACTION_PLAY_PAUSE, hasSong = true).action)
        assertEquals(TopwayServiceAction.IGNORE, decide(TopwayMusicContract.ACTION_PREV, hasSong = false).action)
        assertEquals(TopwayServiceAction.IGNORE, decide(TopwayMusicContract.ACTION_NEXT, hasSong = false).action)
        assertEquals(TopwayServiceAction.IGNORE, decide(TopwayMusicContract.ACTION_PLAY_PAUSE, hasSong = false).action)
    }

    @Test
    fun `cmd update maps to widget update without requiring a current song`() {
        val decision = decide(TopwayMusicContract.ACTION_CMD, TopwayMusicContract.CMD_UPDATE, hasSong = false)
        assertEquals(TopwayServiceAction.WIDGET_UPDATE, decision.action)
        assertNull(decision.seekTargetMs)
    }

    @Test
    fun `unknown commands no-op and cannot imply fallback playback`() {
        assertEquals(TopwayServiceAction.IGNORE, decide(TopwayMusicContract.ACTION_CMD, "bad", hasSong = true).action)
        assertEquals(TopwayServiceAction.IGNORE, decide(TopwayMusicContract.ACTION_CMD, null, hasSong = true).action)
        assertEquals(TopwayServiceAction.IGNORE, decide("bad.action", null, hasSong = true).action)
    }

    @Test
    fun `launcher seek maps only to seek and clamps inside duration`() {
        val valid = decide(TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK, rawSeek = 2500, durationMs = 5_000L, hasSong = true)
        assertEquals(TopwayServiceAction.SEEK, valid.action)
        assertEquals(2500L, valid.seekTargetMs)

        val negative = decide(TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK, rawSeek = -1, durationMs = 5_000L, hasSong = true)
        assertEquals(0L, negative.seekTargetMs)

        val beyond = decide(TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK, rawSeek = 8_000L, durationMs = 5_000L, hasSong = true)
        assertEquals(5_000L, beyond.seekTargetMs)
    }

    @Test
    fun `launcher seek no-ops for missing song duration or bad extras`() {
        assertEquals(TopwayServiceAction.IGNORE, decide(TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK, rawSeek = 100, durationMs = null, hasSong = true).action)
        assertEquals(TopwayServiceAction.IGNORE, decide(TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK, rawSeek = 100, durationMs = 0L, hasSong = true).action)
        assertEquals(TopwayServiceAction.IGNORE, decide(TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK, rawSeek = "bad", durationMs = 5_000L, hasSong = true).action)
        assertEquals(TopwayServiceAction.IGNORE, decide(TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK, rawSeek = 100, durationMs = 5_000L, hasSong = false).action)
    }

    private fun decide(
        action: String?,
        cmd: String? = null,
        rawSeek: Any? = null,
        durationMs: Long? = 10_000L,
        hasSong: Boolean,
    ) = TopwayStartRoutingPolicy.decide(action, cmd, rawSeek, durationMs, hasSong)
}
