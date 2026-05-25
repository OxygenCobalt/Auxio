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
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE),
                hasCurrentSong = true,
                isFocusHeld = true,
            )
        )
        assertTrue(
            MediaButtonActionMapper.shouldForward(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT),
                hasCurrentSong = true,
                isFocusHeld = true,
            )
        )
        assertTrue(
            MediaButtonActionMapper.shouldForward(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK),
                hasCurrentSong = true,
                isFocusHeld = true,
            )
        )
    }

    @Test
    fun `rejects non down repeated and unsupported keys`() {
        val repeated = KeyEvent(0L, 0L, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT, 2)
        assertFalse(
            MediaButtonActionMapper.shouldForward(
                repeated,
                hasCurrentSong = true,
                isFocusHeld = true,
            )
        )
        assertFalse(
            MediaButtonActionMapper.shouldForward(
                KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT),
                hasCurrentSong = true,
                isFocusHeld = true,
            )
        )
        assertFalse(
            MediaButtonActionMapper.shouldForward(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_VOLUME_UP),
                hasCurrentSong = true,
                isFocusHeld = true,
            )
        )
        val repeatedHook = KeyEvent(0L, 0L, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK, 1)
        assertFalse(
            MediaButtonActionMapper.shouldForward(
                repeatedHook,
                hasCurrentSong = true,
                isFocusHeld = true,
            )
        )
        assertFalse(
            MediaButtonActionMapper.shouldForward(
                KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK),
                hasCurrentSong = true,
                isFocusHeld = true,
            )
        )
    }

    @Test
    fun `pause and stop are ignored when queue is inert but toggle actions still forward`() {
        assertFalse(
            MediaButtonActionMapper.shouldForward(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE),
                hasCurrentSong = false,
                isFocusHeld = true,
            )
        )
        assertFalse(
            MediaButtonActionMapper.shouldForward(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_STOP),
                hasCurrentSong = false,
                isFocusHeld = true,
            )
        )
        assertTrue(
            MediaButtonActionMapper.shouldForward(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY),
                hasCurrentSong = false,
                isFocusHeld = true,
            )
        )
        assertTrue(
            MediaButtonActionMapper.shouldForward(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK),
                hasCurrentSong = false,
                isFocusHeld = true,
            )
        )
    }

    @Test
    fun `rejects media keys when focus is not held`() {
        assertFalse(
            MediaButtonActionMapper.shouldForward(
                KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT),
                hasCurrentSong = true,
                isFocusHeld = false,
            )
        )
    }
}
