/*
 * Copyright (c) 2024 Auxio Project
 * MediaButtonActionMapper.kt is part of Auxio.
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

/** Pure policy for deciding whether a media button key event should be forwarded to playback. */
object MediaButtonActionMapper {
    fun shouldForward(event: KeyEvent?, hasCurrentSong: Boolean, isFocusHeld: Boolean): Boolean {
        if (event == null || event.action != KeyEvent.ACTION_DOWN) {
            return false
        }
        if (event.repeatCount > 0) {
            return false
        }
        if (!isFocusHeld) {
            return false
        }
        if (!hasCurrentSong && isPauseOrStop(event.keyCode)) {
            return false
        }
        return isSupportedMediaKey(event.keyCode)
    }

    private fun isPauseOrStop(keyCode: Int): Boolean =
        keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE || keyCode == KeyEvent.KEYCODE_MEDIA_STOP

    private fun isSupportedMediaKey(keyCode: Int): Boolean =
        when (keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_HEADSETHOOK,
            KeyEvent.KEYCODE_MEDIA_PLAY,
            KeyEvent.KEYCODE_MEDIA_PAUSE,
            KeyEvent.KEYCODE_MEDIA_NEXT,
            KeyEvent.KEYCODE_MEDIA_PREVIOUS,
            KeyEvent.KEYCODE_MEDIA_STOP -> true
            else -> false
        }
}
