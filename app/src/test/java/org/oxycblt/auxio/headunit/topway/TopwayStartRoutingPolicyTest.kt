/*
 * Copyright (c) 2024 Auxio Project
 * TopwayStartRoutingPolicyTest.kt is part of Auxio.
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

class TopwayStartRoutingPolicyTest {
    @Test
    fun `known commands map only when a current song exists`() {
        assertEquals(
            TopwayServiceAction.PREVIOUS,
            decide(TopwayMusicContract.ACTION_PREV, hasSong = true).action)
        assertEquals(
            TopwayServiceAction.NEXT,
            decide(TopwayMusicContract.ACTION_NEXT, hasSong = true).action)
        assertEquals(
            TopwayServiceAction.PLAY_PAUSE,
            decide(TopwayMusicContract.ACTION_PLAY_PAUSE, hasSong = true).action)
        assertEquals(
            TopwayServiceAction.IGNORE,
            decide(TopwayMusicContract.ACTION_PREV, hasSong = false).action)
        assertEquals(
            TopwayServiceAction.IGNORE,
            decide(TopwayMusicContract.ACTION_NEXT, hasSong = false).action)
        assertEquals(
            TopwayServiceAction.IGNORE,
            decide(TopwayMusicContract.ACTION_PLAY_PAUSE, hasSong = false).action)
    }

    @Test
    fun `cmd update maps to widget update without requiring a current song`() {
        val decision =
            decide(TopwayMusicContract.ACTION_CMD, TopwayMusicContract.CMD_UPDATE, hasSong = false)
        assertEquals(TopwayServiceAction.WIDGET_UPDATE, decision.action)
        assertNull(decision.seekTargetMs)
    }

    @Test
    fun `unknown commands no-op and cannot imply fallback playback`() {
        assertEquals(
            TopwayServiceAction.IGNORE,
            decide(TopwayMusicContract.ACTION_CMD, "bad", hasSong = true).action)
        assertEquals(
            TopwayServiceAction.IGNORE,
            decide(TopwayMusicContract.ACTION_CMD, null, hasSong = true).action)
        assertEquals(TopwayServiceAction.IGNORE, decide("bad.action", null, hasSong = true).action)
    }

    @Test
    fun `launcher seek maps only to seek and clamps inside duration`() {
        val valid =
            decide(
                TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK,
                rawSeek = 2500,
                durationMs = 5_000L,
                hasSong = true)
        assertEquals(TopwayServiceAction.SEEK, valid.action)
        assertEquals(2500L, valid.seekTargetMs)

        val negative =
            decide(
                TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK,
                rawSeek = -1,
                durationMs = 5_000L,
                hasSong = true)
        assertEquals(0L, negative.seekTargetMs)

        val beyond =
            decide(
                TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK,
                rawSeek = 8_000L,
                durationMs = 5_000L,
                hasSong = true)
        assertEquals(5_000L, beyond.seekTargetMs)
    }

    @Test
    fun `launcher seek no-ops for missing song duration or bad extras`() {
        assertEquals(
            TopwayServiceAction.IGNORE,
            decide(
                    TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK,
                    rawSeek = 100,
                    durationMs = null,
                    hasSong = true)
                .action)
        assertEquals(
            TopwayServiceAction.IGNORE,
            decide(
                    TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK,
                    rawSeek = 100,
                    durationMs = 0L,
                    hasSong = true)
                .action)
        assertEquals(
            TopwayServiceAction.IGNORE,
            decide(
                    TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK,
                    rawSeek = "bad",
                    durationMs = 5_000L,
                    hasSong = true)
                .action)
        assertEquals(
            TopwayServiceAction.IGNORE,
            decide(
                    TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK,
                    rawSeek = 100,
                    durationMs = 5_000L,
                    hasSong = false)
                .action)
    }

    private fun decide(
        action: String?,
        cmd: String? = null,
        rawSeek: Any? = null,
        durationMs: Long? = 10_000L,
        hasSong: Boolean,
    ) = TopwayStartRoutingPolicy.decide(action, cmd, rawSeek, durationMs, hasSong)
}
