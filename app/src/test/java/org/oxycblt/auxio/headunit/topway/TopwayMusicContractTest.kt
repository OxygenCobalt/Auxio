package org.oxycblt.auxio.headunit.topway

import kotlin.test.Test
import kotlin.test.assertEquals

class TopwayMusicContractTest {
    @Test
    fun `musicaArtist key is preserved exactly`() {
        assertEquals("musicaArtist", TopwayMusicContract.EXTRA_MUSIC_ARTIST)
    }

    @Test
    fun `command constants match decompile contract`() {
        assertEquals("com.tw.music.action.cmd", TopwayMusicContract.ACTION_CMD)
        assertEquals("prev", TopwayMusicContract.CMD_PREV)
        assertEquals("next", TopwayMusicContract.CMD_NEXT)
        assertEquals("pp", TopwayMusicContract.CMD_PLAY_PAUSE)
        assertEquals("update", TopwayMusicContract.CMD_UPDATE)
    }
}
