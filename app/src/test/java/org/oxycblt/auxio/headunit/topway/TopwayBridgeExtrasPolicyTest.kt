package org.oxycblt.auxio.headunit.topway

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.oxycblt.auxio.AuxioService

class TopwayBridgeExtrasPolicyTest {
    @Test
    fun `sanitizer forwards only allowlisted Topway extras`() {
        val extras =
            TopwayBridgeExtrasPolicy.sanitizeIncomingExtras(
                mapOf(
                    TopwayMusicContract.EXTRA_CMD to TopwayMusicContract.CMD_UPDATE,
                    TopwayMusicContract.EXTRA_WIDGET_PROGRESS to 1234,
                    "ignored" to "payload",
                ),
            )

        assertEquals(TopwayMusicContract.CMD_UPDATE, extras.cmd)
        assertEquals(1234, extras.widgetProgress)
    }

    @Test
    fun `sanitizer rejects unknown command types and long payloads`() {
        val badType =
            TopwayBridgeExtrasPolicy.sanitizeIncomingExtras(
                mapOf(TopwayMusicContract.EXTRA_CMD to 123),
            )
        assertNull(badType.cmd)

        val tooLong =
            TopwayBridgeExtrasPolicy.sanitizeIncomingExtras(
                mapOf(TopwayMusicContract.EXTRA_CMD to "this-command-is-definitely-too-long"),
            )
        assertNull(tooLong.cmd)
    }

    @Test
    fun `sanitizer ignores injected start id override attempts`() {
        val extras =
            TopwayBridgeExtrasPolicy.sanitizeIncomingExtras(
                mapOf(
                    AuxioService.INTENT_KEY_START_ID to -999,
                    TopwayMusicContract.EXTRA_CMD to TopwayMusicContract.CMD_PREV,
                ),
            )
        assertEquals(TopwayMusicContract.CMD_PREV, extras.cmd)
        assertNull(extras.widgetProgress)
    }
}
