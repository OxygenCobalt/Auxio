/*
 * Copyright (c) 2024 Auxio Project
 * TopwayMusicContractTest.kt is part of Auxio.
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
