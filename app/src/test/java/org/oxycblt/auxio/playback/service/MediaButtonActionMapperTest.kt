package org.oxycblt.auxio.playback.service

import android.view.KeyEvent
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MediaButtonActionMapperTest {
    @Test
    fun `forwards supported key down events`() {
        assertTrue(
            MediaButtonActionMapper.shouldForward(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE),
                hasCurrentSong = true,
            )
        )
        assertTrue(
            MediaButtonActionMapper.shouldForward(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT),
                hasCurrentSong = true,
            )
        )
    }

    @Test
    fun `rejects non down repeated and unsupported keys`() {
        val repeated = KeyEvent(0L, 0L, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT, 2)
        assertFalse(MediaButtonActionMapper.shouldForward(repeated, hasCurrentSong = true))
        assertFalse(MediaButtonActionMapper.shouldForward(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT), true))
        assertFalse(MediaButtonActionMapper.shouldForward(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_UP), true))
    }

    @Test
    fun `pause and stop are ignored when queue is inert`() {
        assertFalse(MediaButtonActionMapper.shouldForward(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE), false))
        assertFalse(MediaButtonActionMapper.shouldForward(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_STOP), false))
        assertTrue(MediaButtonActionMapper.shouldForward(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY), false))
    }
}
