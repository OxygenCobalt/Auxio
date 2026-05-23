/*
 * Copyright (c) 2026 Auxio Project
 * WidgetRenderStateTest.kt is part of Auxio.
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

package org.oxycblt.auxio.widgets

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.oxycblt.auxio.R

class WidgetRenderStateTest {
    @Test
    fun fromPlayback_noTitle_returnsNoSession() {
        assertTrue(WidgetRenderState.fromPlayback(title = null, artist = "a", album = "b", isPlaying = true, hasArtwork = false) is WidgetRenderState.NoSession)
    }

    @Test
    fun fromPlayback_withTitle_returnsActive() {
        val state =
            WidgetRenderState.fromPlayback(
                title = "Song",
                artist = "Artist",
                album = "Album",
                albumArtist = "Album Artist",
                isPlaying = false,
                hasArtwork = true,
            )
        assertTrue(state is WidgetRenderState.Active)
        state as WidgetRenderState.Active
        assertEquals("Song", state.title)
        assertEquals("Artist", state.artist)
        assertTrue(state.subtitle.contains("Artist"))
    }

    @Test
    fun fromPlayback_duplicateArtistAndAlbumArtist_deduplicatesSubtitle() {
        val state =
            WidgetRenderState.fromPlayback(
                title = "Song",
                artist = "Artist",
                album = "Album",
                albumArtist = "Artist",
                isPlaying = false,
                hasArtwork = true,
            )
        state as WidgetRenderState.Active
        assertEquals("Artist", state.subtitle)
    }

    @Test
    fun playPauseIcon_reflectsPlayingState() {
        assertEquals(R.drawable.ic_pause_24, WidgetRenderState.playPauseIcon(true))
        assertEquals(R.drawable.ic_play_24, WidgetRenderState.playPauseIcon(false))
    }

    @Test
    fun playPauseBackground_reflectsPlayingState() {
        assertEquals(R.drawable.ui_remote_fab_container_playing, WidgetRenderState.playPauseBackground(true))
        assertEquals(R.drawable.ui_remote_fab_container_paused, WidgetRenderState.playPauseBackground(false))
    }
}