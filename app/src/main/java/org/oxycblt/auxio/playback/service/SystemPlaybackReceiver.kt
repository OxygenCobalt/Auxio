/*
 * Copyright (c) 2024 Auxio Project
 * SystemPlaybackReceiver.kt is part of Auxio.
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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import androidx.core.content.ContextCompat
import javax.inject.Inject
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.widgets.WidgetComponent
import org.oxycblt.auxio.widgets.WidgetProvider

/**
 * A [BroadcastReceiver] for receiving playback-specific [Intent]s from the system that require an
 * active [IntentFilter] to be registered.
 */
class SystemPlaybackReceiver
private constructor(
    private val playbackManager: PlaybackStateManager,
    private val playbackSettings: PlaybackSettings,
    private val widgetComponent: WidgetComponent
) : BroadcastReceiver() {
    private var initialHeadsetPlugEventHandled = false

    class Factory
    @Inject
    constructor(
        private val playbackManager: PlaybackStateManager,
        private val playbackSettings: PlaybackSettings,
        private val widgetComponent: WidgetComponent
    ) {
        fun create(context: Context): SystemPlaybackReceiver {
            val receiver =
                SystemPlaybackReceiver(playbackManager, playbackSettings, widgetComponent)
            ContextCompat.registerReceiver(
                context, receiver, INTENT_FILTER, ContextCompat.RECEIVER_EXPORTED)
            return receiver
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            // --- SYSTEM EVENTS ---

            // Android has three different ways of handling audio plug events for some reason:
            // 1. ACTION_HEADSET_PLUG, which only works with wired headsets
            // 2. ACTION_ACL_CONNECTED, which allows headset autoplay but also requires
            // granting the BLUETOOTH/BLUETOOTH_CONNECT permissions, which is more or less
            // a non-starter since both require me to display a permission prompt
            // 3. Some internal framework thing that also handles bluetooth headsets
            // Just use ACTION_HEADSET_PLUG.
            AudioManager.ACTION_HEADSET_PLUG -> {
                logD("Received headset plug event")
                when (intent.getIntExtra("state", -1)) {
                    0 -> pauseFromHeadsetPlug()
                    1 -> playFromHeadsetPlug()
                }

                initialHeadsetPlugEventHandled = true
            }
            AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                logD("Received Headset noise event")
                pauseFromHeadsetPlug()
            }

            // --- AUXIO EVENTS ---
            PlaybackActions.ACTION_PLAY_PAUSE -> {
                logD("Received play event")
                playbackManager.playing(!playbackManager.progression.isPlaying)
            }
            PlaybackActions.ACTION_INC_REPEAT_MODE -> {
                logD("Received repeat mode event")
                playbackManager.repeatMode(playbackManager.repeatMode.increment())
            }
            PlaybackActions.ACTION_INVERT_SHUFFLE -> {
                logD("Received shuffle event")
                playbackManager.shuffled(!playbackManager.isShuffled)
            }
            PlaybackActions.ACTION_SKIP_PREV -> {
                logD("Received skip previous event")
                playbackManager.prev()
            }
            PlaybackActions.ACTION_SKIP_NEXT -> {
                logD("Received skip next event")
                playbackManager.next()
            }
            PlaybackActions.ACTION_EXIT -> {
                logD("Received exit event")
                playbackManager.endSession()
            }
            WidgetProvider.ACTION_WIDGET_UPDATE -> {
                logD("Received widget update event")
                widgetComponent.update()
            }
        }
    }

    private fun playFromHeadsetPlug() {
        // ACTION_HEADSET_PLUG will fire when this BroadcastReceiver is initially attached,
        // which would result in unexpected playback. Work around it by dropping the first
        // call to this function, which should come from that Intent.
        if (playbackSettings.headsetAutoplay &&
            playbackManager.currentSong != null &&
            initialHeadsetPlugEventHandled) {
            logD("Device connected, resuming")
            playbackManager.playing(true)
        }
    }

    private fun pauseFromHeadsetPlug() {
        if (playbackManager.currentSong != null) {
            logD("Device disconnected, pausing")
            playbackManager.playing(false)
        }
    }

    private companion object {
        val INTENT_FILTER =
            IntentFilter().apply {
                addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
                addAction(AudioManager.ACTION_HEADSET_PLUG)
                addAction(PlaybackActions.ACTION_INC_REPEAT_MODE)
                addAction(PlaybackActions.ACTION_INVERT_SHUFFLE)
                addAction(PlaybackActions.ACTION_SKIP_PREV)
                addAction(PlaybackActions.ACTION_PLAY_PAUSE)
                addAction(PlaybackActions.ACTION_SKIP_NEXT)
                addAction(WidgetProvider.ACTION_WIDGET_UPDATE)
            }
    }
}
