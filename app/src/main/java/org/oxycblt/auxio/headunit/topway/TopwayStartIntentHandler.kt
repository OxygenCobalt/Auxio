package org.oxycblt.auxio.headunit.topway

import android.content.Intent

interface TopwayStartCallbacks {
    val hasCurrentSong: Boolean
    val currentDurationMs: Long?
    fun previous()
    fun next()
    fun playPause()
    fun widgetUpdate()
    fun seekTo(positionMs: Long)
    fun ignore()
}

object TopwayStartIntentHandler {
    fun handle(intent: Intent?, callbacks: TopwayStartCallbacks): Boolean {
        if (intent == null || !TopwayMusicContract.isIncomingAction(intent.action)) return false
        val decision =
            TopwayStartRoutingPolicy.decide(
                action = intent.action,
                cmd = intent.getStringExtra(TopwayMusicContract.EXTRA_CMD),
                rawSeek = intent.extras?.get(TopwayMusicContract.EXTRA_WIDGET_PROGRESS),
                durationMs = callbacks.currentDurationMs,
                hasCurrentSong = callbacks.hasCurrentSong,
            )
        when (decision.action) {
            TopwayServiceAction.PREVIOUS -> callbacks.previous()
            TopwayServiceAction.NEXT -> callbacks.next()
            TopwayServiceAction.PLAY_PAUSE -> callbacks.playPause()
            TopwayServiceAction.WIDGET_UPDATE -> callbacks.widgetUpdate()
            TopwayServiceAction.SEEK ->
                decision.seekTargetMs?.let { callbacks.seekTo(it) } ?: callbacks.ignore()
            TopwayServiceAction.IGNORE -> callbacks.ignore()
        }
        return true
    }
}
