/*
 * Copyright (c) 2024 Auxio Project
 * TopwayBridgeExtrasPolicyTest.kt is part of Auxio.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
