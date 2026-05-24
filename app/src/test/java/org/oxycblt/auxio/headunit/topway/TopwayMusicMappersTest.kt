package org.oxycblt.auxio.headunit.topway

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TopwayMusicMappersTest {
    @Test
    fun `maps direct and cmd actions`() {
        assertEquals(TopwayMappedCommand.PREV, TopwayMusicCommandMapper.map(TopwayMusicContract.ACTION_PREV, null))
        assertEquals(TopwayMappedCommand.NEXT, TopwayMusicCommandMapper.map(TopwayMusicContract.ACTION_NEXT, null))
        assertEquals(TopwayMappedCommand.PLAY_PAUSE, TopwayMusicCommandMapper.map(TopwayMusicContract.ACTION_PLAY_PAUSE, null))
        assertEquals(TopwayMappedCommand.UPDATE, TopwayMusicCommandMapper.map(TopwayMusicContract.ACTION_CMD, TopwayMusicContract.CMD_UPDATE))
        assertEquals(TopwayMappedCommand.UNKNOWN, TopwayMusicCommandMapper.map(TopwayMusicContract.ACTION_CMD, "bad"))
    }

    @Test
    fun `seek mapper clamps and rejects invalid inputs`() {
        assertEquals(1000L, TopwayMusicSeekMapper.mapSeekTargetMs(1000, 5000L))
        assertEquals(0L, TopwayMusicSeekMapper.mapSeekTargetMs(-1, 5000L))
        assertEquals(5000L, TopwayMusicSeekMapper.mapSeekTargetMs(8000, 5000L))
        assertNull(TopwayMusicSeekMapper.mapSeekTargetMs(null, 5000L))
        assertNull(TopwayMusicSeekMapper.mapSeekTargetMs(100, 0L))
    }
}
