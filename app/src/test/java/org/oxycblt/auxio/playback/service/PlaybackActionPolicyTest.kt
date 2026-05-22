package org.oxycblt.auxio.playback.service

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackActionPolicyTest {
    @Test
    fun `known playback actions are supported`() {
        assertTrue(PlaybackActionPolicy.isSupportedAction(PlaybackActions.ACTION_PLAY_PAUSE))
        assertTrue(PlaybackActionPolicy.isSupportedAction(PlaybackActions.ACTION_SKIP_NEXT))
        assertTrue(PlaybackActionPolicy.isSupportedAction(PlaybackActions.ACTION_EXIT))
    }

    @Test
    fun `unknown or null actions are rejected safely`() {
        assertFalse(PlaybackActionPolicy.isSupportedAction(null))
        assertFalse(PlaybackActionPolicy.isSupportedAction("org.oxycblt.auxio.action.UNKNOWN"))
    }
}
