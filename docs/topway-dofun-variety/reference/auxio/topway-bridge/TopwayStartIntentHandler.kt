/*
 * Copyright (c) 2024 Auxio Project
 * TopwayStartIntentHandler.kt is part of Auxio.
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
