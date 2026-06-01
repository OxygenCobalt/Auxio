package org.oxycblt.auxio.car.overlay

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.SystemClock
import android.view.KeyEvent

/**
 * Integration seam between the overlay and Auxio playback.
 *
 * Codex should replace [FallbackMediaKeyPlaybackBridge] with the real Auxio player/session/controller.
 * Generic media keys are only a fallback because they may control the wrong app on TS18 firmware.
 */
internal interface CarOverlayPlaybackBridge {
    fun previous()
    fun playPause()
    fun next()
    fun openAuxio()

    companion object {
        fun create(context: Context): CarOverlayPlaybackBridge {
            // TODO: Replace with a real bridge to Auxio's playback controller/session.
            return FallbackMediaKeyPlaybackBridge(context.applicationContext)
        }
    }
}

internal class FallbackMediaKeyPlaybackBridge(
    private val context: Context,
) : CarOverlayPlaybackBridge {
    private val audioManager: AudioManager? =
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    override fun previous() = dispatch(KeyEvent.KEYCODE_MEDIA_PREVIOUS)

    override fun playPause() = dispatch(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)

    override fun next() = dispatch(KeyEvent.KEYCODE_MEDIA_NEXT)

    override fun openAuxio() {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            context.startActivity(launchIntent)
        }
    }

    private fun dispatch(keyCode: Int) {
        val now = SystemClock.uptimeMillis()
        val down = KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0)
        val up = KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode, 0)
        audioManager?.dispatchMediaKeyEvent(down)
        audioManager?.dispatchMediaKeyEvent(up)
    }
}
