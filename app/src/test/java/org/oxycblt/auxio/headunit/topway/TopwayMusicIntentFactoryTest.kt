/*
 * Copyright (c) 2024 Auxio Project
 * TopwayMusicIntentFactoryTest.kt is part of Auxio.
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
import org.oxycblt.auxio.headunit.compat.HeadUnitMetadataSnapshot

class TopwayMusicIntentFactoryTest {
    @Test
    fun `metadata intent uses Topway action and keys`() {
        val snapshot =
            HeadUnitMetadataSnapshot(
                "t",
                "s",
                "a",
                "aa",
                "al",
                "d",
                1000L,
                "id",
                "uri",
                false,
                null,
            )
        val intent = TopwayMusicIntentFactory.metadataIntent(snapshot)
        assertEquals(TopwayMusicContract.ACTION_MUSIC_INFO, intent.action)
        assertEquals("t", intent.getStringExtra(TopwayMusicContract.EXTRA_MUSIC_TITLE))
        assertEquals("a", intent.getStringExtra(TopwayMusicContract.EXTRA_MUSIC_ARTIST))
    }

    @Test
    fun `progress intent clamps negative values and keeps milliseconds`() {
        val intent = TopwayMusicIntentFactory.progressIntent(-1L, 5_000L)
        assertEquals(TopwayMusicContract.ACTION_PROGRESS_DURATION, intent.action)
        assertEquals(0L, intent.getLongExtra(TopwayMusicContract.EXTRA_PROGRESS, -1L))
        assertEquals(5_000L, intent.getLongExtra(TopwayMusicContract.EXTRA_DURATION, -1L))
    }
}
