package org.oxycblt.auxio.playback.service

import android.view.KeyEvent

/**
 * Pure policy for deciding whether a media button key event should be forwarded to playback.
 */
object MediaButtonActionMapper {
    fun shouldForward(event: KeyEvent?, hasCurrentSong: Boolean): Boolean {
        if (event == null || event.action != KeyEvent.ACTION_DOWN) {
            return false
        }
        if (event.repeatCount > 0) {
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
        keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE ||
            keyCode == KeyEvent.KEYCODE_MEDIA_PLAY ||
            keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE ||
            keyCode == KeyEvent.KEYCODE_MEDIA_NEXT ||
            keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS ||
            keyCode == KeyEvent.KEYCODE_MEDIA_STOP
}
