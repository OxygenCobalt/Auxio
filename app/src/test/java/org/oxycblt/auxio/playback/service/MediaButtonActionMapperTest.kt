/*
 * Copyright (c) 2024 Auxio Project
 * MediaButtonActionMapperTest.kt is part of Auxio.
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
                action = KeyEvent.ACTION_DOWN,
                keyCode = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                repeatCount = 0,
                hasCurrentSong = true,
                isFocusHeld = true,
            )
        )
        assertTrue(
            MediaButtonActionMapper.shouldForward(
                action = KeyEvent.ACTION_DOWN,
                keyCode = KeyEvent.KEYCODE_MEDIA_NEXT,
                repeatCount = 0,
                hasCurrentSong = true,
                isFocusHeld = true,
            )
        )
        assertTrue(
            MediaButtonActionMapper.shouldForward(
                action = KeyEvent.ACTION_DOWN,
                keyCode = KeyEvent.KEYCODE_HEADSETHOOK,
                repeatCount = 0,
                hasCurrentSong = true,
                isFocusHeld = true,
            )
        )
    }

    @Test
    fun `rejects non down repeated and unsupported keys`() {
        assertFalse(
            MediaButtonActionMapper.shouldForward(
                action = KeyEvent.ACTION_DOWN,
                keyCode = KeyEvent.KEYCODE_MEDIA_NEXT,
                repeatCount = 2,
                hasCurrentSong = true,
                isFocusHeld = true,
            )
        )
        assertFalse(
            MediaButtonActionMapper.shouldForward(
                action = KeyEvent.ACTION_UP,
                keyCode = KeyEvent.KEYCODE_MEDIA_NEXT,
                repeatCount = 0,
                hasCurrentSong = true,
                isFocusHeld = true,
            )
        )
        assertFalse(
            MediaButtonActionMapper.shouldForward(
                action = KeyEvent.ACTION_DOWN,
                keyCode = KeyEvent.KEYCODE_VOLUME_UP,
                repeatCount = 0,
                hasCurrentSong = true,
                isFocusHeld = true,
            )
        )
        assertFalse(
            MediaButtonActionMapper.shouldForward(
                action = KeyEvent.ACTION_DOWN,
                keyCode = KeyEvent.KEYCODE_HEADSETHOOK,
                repeatCount = 1,
                hasCurrentSong = true,
                isFocusHeld = true,
            )
        )
        assertFalse(
            MediaButtonActionMapper.shouldForward(
                action = KeyEvent.ACTION_UP,
                keyCode = KeyEvent.KEYCODE_HEADSETHOOK,
                repeatCount = 0,
                hasCurrentSong = true,
                isFocusHeld = true,
            )
        )
    }

    @Test
    fun `pause and stop are ignored when queue is inert but toggle actions still forward`() {
        assertFalse(
            MediaButtonActionMapper.shouldForward(
                action = KeyEvent.ACTION_DOWN,
                keyCode = KeyEvent.KEYCODE_MEDIA_PAUSE,
                repeatCount = 0,
                hasCurrentSong = false,
                isFocusHeld = true,
            )
        )
        assertFalse(
            MediaButtonActionMapper.shouldForward(
                action = KeyEvent.ACTION_DOWN,
                keyCode = KeyEvent.KEYCODE_MEDIA_STOP,
                repeatCount = 0,
                hasCurrentSong = false,
                isFocusHeld = true,
            )
        )
        assertTrue(
            MediaButtonActionMapper.shouldForward(
                action = KeyEvent.ACTION_DOWN,
                keyCode = KeyEvent.KEYCODE_MEDIA_PLAY,
                repeatCount = 0,
                hasCurrentSong = false,
                isFocusHeld = true,
            )
        )
        assertTrue(
            MediaButtonActionMapper.shouldForward(
                action = KeyEvent.ACTION_DOWN,
                keyCode = KeyEvent.KEYCODE_HEADSETHOOK,
                repeatCount = 0,
                hasCurrentSong = false,
                isFocusHeld = true,
            )
        )
    }

    @Test
    fun `rejects media keys when focus is not held`() {
        assertFalse(
            MediaButtonActionMapper.shouldForward(
                action = KeyEvent.ACTION_DOWN,
                keyCode = KeyEvent.KEYCODE_MEDIA_NEXT,
                repeatCount = 0,
                hasCurrentSong = true,
                isFocusHeld = false,
            )
        )
    }
}
