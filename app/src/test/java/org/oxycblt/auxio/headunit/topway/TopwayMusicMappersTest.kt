/*
 * Copyright (c) 2024 Auxio Project
 * TopwayMusicMappersTest.kt is part of Auxio.
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

class TopwayMusicMappersTest {
    @Test
    fun `maps direct and cmd actions`() {
        assertEquals(
            TopwayMappedCommand.PREV,
            TopwayMusicCommandMapper.map(TopwayMusicContract.ACTION_PREV, null))
        assertEquals(
            TopwayMappedCommand.NEXT,
            TopwayMusicCommandMapper.map(TopwayMusicContract.ACTION_NEXT, null))
        assertEquals(
            TopwayMappedCommand.PLAY_PAUSE,
            TopwayMusicCommandMapper.map(TopwayMusicContract.ACTION_PLAY_PAUSE, null))
        assertEquals(
            TopwayMappedCommand.UPDATE,
            TopwayMusicCommandMapper.map(
                TopwayMusicContract.ACTION_CMD, TopwayMusicContract.CMD_UPDATE))
        assertEquals(
            TopwayMappedCommand.UNKNOWN,
            TopwayMusicCommandMapper.map(TopwayMusicContract.ACTION_CMD, "bad"))
        assertEquals(
            TopwayMappedCommand.UNKNOWN,
            TopwayMusicCommandMapper.map(TopwayMusicContract.ACTION_CMD, null))
        assertEquals(
            TopwayMappedCommand.PREV,
            TopwayMusicCommandMapper.map(
                TopwayMusicContract.ACTION_CMD, TopwayMusicContract.CMD_PREV))
        assertEquals(
            TopwayMappedCommand.NEXT,
            TopwayMusicCommandMapper.map(
                TopwayMusicContract.ACTION_CMD, TopwayMusicContract.CMD_NEXT))
        assertEquals(
            TopwayMappedCommand.PLAY_PAUSE,
            TopwayMusicCommandMapper.map(
                TopwayMusicContract.ACTION_CMD, TopwayMusicContract.CMD_PLAY_PAUSE))
    }

    @Test
    fun `seek mapper clamps and rejects invalid inputs`() {
        assertEquals(1000L, TopwayMusicSeekMapper.mapSeekTargetMs(1000, 5000L))
        assertEquals(0L, TopwayMusicSeekMapper.mapSeekTargetMs(-1, 5000L))
        assertEquals(5000L, TopwayMusicSeekMapper.mapSeekTargetMs(8000, 5000L))
        assertEquals(2000L, TopwayMusicSeekMapper.mapSeekTargetMs(2000L, 5000L))
        assertEquals(3000L, TopwayMusicSeekMapper.mapSeekTargetMs("3000", 5000L))
        assertNull(TopwayMusicSeekMapper.mapSeekTargetMs("bad", 5000L))
        assertNull(TopwayMusicSeekMapper.mapSeekTargetMs(null, 5000L))
        assertNull(TopwayMusicSeekMapper.mapSeekTargetMs(100, 0L))
        assertNull(TopwayMusicSeekMapper.mapSeekTargetMs(100, null))
    }
}
