package org.oxycblt.auxio.widgets

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.oxycblt.auxio.R

class WidgetRenderStateTest {
    @Test
    fun fromPlayback_noTitle_returnsNoSession() {
        assertTrue(WidgetRenderState.fromPlayback(null, "a", "b", true, false) is WidgetRenderState.NoSession)
    }

    @Test
    fun fromPlayback_withTitle_returnsActive() {
        val state = WidgetRenderState.fromPlayback("Song", "Artist", "Album", false, true)
        assertTrue(state is WidgetRenderState.Active)
        state as WidgetRenderState.Active
        assertEquals("Song", state.title)
        assertEquals("Artist", state.artist)
    }

    @Test
    fun playPauseIcon_reflectsPlayingState() {
        assertEquals(R.drawable.ic_pause_24, WidgetRenderState.playPauseIcon(true))
        assertEquals(R.drawable.ic_play_24, WidgetRenderState.playPauseIcon(false))
    }
}
