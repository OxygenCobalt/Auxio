/*
 * Copyright (c) 2023 Auxio Project
 * MusicModeTest.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music

import org.junit.Assert.assertEquals
import org.junit.Test

class MusicModeTest {
    @Test
    fun intCode() {
        assertEquals(MusicMode.SONGS, MusicMode.fromIntCode(MusicMode.SONGS.intCode))
        assertEquals(MusicMode.ALBUMS, MusicMode.fromIntCode(MusicMode.ALBUMS.intCode))
        assertEquals(MusicMode.ARTISTS, MusicMode.fromIntCode(MusicMode.ARTISTS.intCode))
        assertEquals(MusicMode.GENRES, MusicMode.fromIntCode(MusicMode.GENRES.intCode))
    }
}
