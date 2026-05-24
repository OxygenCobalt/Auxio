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
import kotlin.math.toIntExact
import javax.inject.Inject
import org.oxycblt.auxio.headunit.topway.TopwayMappedCommand
import org.oxycblt.auxio.headunit.topway.TopwayMusicCommandMapper
import org.oxycblt.auxio.headunit.topway.TopwayMusicContract
import org.oxycblt.auxio.headunit.topway.TopwayMusicSeekMapper
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.widgets.WidgetComponent
import org.oxycblt.auxio.widgets.WidgetProvider
import org.oxycblt.auxio.widgets.WidgetUtil
import timber.log.Timber as L

/**
 * A [BroadcastReceiver] for receiving playback-specific [Intent]s from the system that require an
 * active [IntentFilter] to be registered.
 */
class SystemPlaybackReceiver
private constructor(
    private val context: Context,
    private val playbackManager: PlaybackStateManager,
    private val playbackSettings: PlaybackSettings,
    private val widgetComponent: WidgetComponent,
    private val onExitRequested: () -> Unit,
) : BroadcastReceiver() {
    private var initialHeadsetPlugEventHandled = false

    class Factory
    @Inject
    constructor(
        private val playbackManager: PlaybackStateManager,
        private val playbackSettings: PlaybackSettings,
    ) {
        fun create(
            context: Context,
            widgetComponent: WidgetComponent,
            onExitRequested: () -> Unit,
        ) =
            SystemPlaybackReceiver(
                context,
                playbackManager,
                playbackSettings,
                widgetComponent,
                onExitRequested,
            )
    }

    @Suppress("WrongConstant")
    fun attach() {
        ContextCompat.registerReceiver(
            context,
            this,
            INTENT_FILTER,
            ContextCompat.RECEIVER_EXPORTED,
        )
    }

    fun release() {
        context.unregisterReceiver(this)
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (
            !isSystemAction(intent.action) &&
                !PlaybackActionPolicy.isSupportedAction(intent.action)
        ) {
            L.w("Ignoring unsupported playback action: ${intent.action}")
            return
        }

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
                L.d("Received headset plug event")
                when (intent.getIntExtra("state", -1)) {
                    0 -> pauseFromHeadsetPlug()
                    1 -> playFromHeadsetPlug()
                }

                initialHeadsetPlugEventHandled = true
            }
            AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                L.d("Received Headset noise event")
                pauseFromHeadsetPlug()
            }

            // --- AUXIO EVENTS ---
            PlaybackActions.ACTION_PLAY_PAUSE -> {
                L.d("Received play event")
                playbackManager.playing(!playbackManager.progression.isPlaying)
            }
            PlaybackActions.ACTION_INC_REPEAT_MODE -> {
                L.d("Received repeat mode event")
                playbackManager.repeatMode(playbackManager.repeatMode.increment())
            }
            PlaybackActions.ACTION_INVERT_SHUFFLE -> {
                L.d("Received shuffle event")
                playbackManager.shuffled(!playbackManager.isShuffled)
            }
            PlaybackActions.ACTION_SKIP_PREV -> {
                L.d("Received skip previous event")
                playbackManager.prev()
            }
            PlaybackActions.ACTION_SKIP_NEXT -> {
                L.d("Received skip next event")
                playbackManager.next()
            }
            PlaybackActions.ACTION_EXIT -> {
                L.d("Received exit event")
                onExitRequested()
            }
            WidgetProvider.ACTION_WIDGET_UPDATE -> {
                L.d("Received widget update event")
                widgetComponent.update()
            }
            TopwayMusicContract.ACTION_PREV,
            TopwayMusicContract.ACTION_NEXT,
            TopwayMusicContract.ACTION_PLAY_PAUSE,
            TopwayMusicContract.ACTION_CMD -> {
                when (
                    TopwayMusicCommandMapper.map(
                        intent.action,
                        intent.getStringExtra(TopwayMusicContract.EXTRA_CMD),
                    )
                ) {
                    TopwayMappedCommand.PREV -> playbackManager.prev()
                    TopwayMappedCommand.NEXT -> playbackManager.next()
                    TopwayMappedCommand.PLAY_PAUSE -> {
                        if (playbackManager.currentSong != null) {
                            playbackManager.playing(!playbackManager.progression.isPlaying)
                        } else {
                            L.d("Ignoring Topway play/pause with no current song")
                        }
                    }
                    TopwayMappedCommand.UPDATE -> {
                        if (WidgetUtil.hasWidgets(context, WidgetProvider::class.java)) {
                            widgetComponent.update()
                        } else {
                            L.d("Ignoring Topway widget update with no widget instances")
                        }
                    }
                    TopwayMappedCommand.UNKNOWN -> L.w("Ignoring unknown Topway command")
                }
            }
            TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK -> {
                val rawSeek =
                    (intent.extras?.get(TopwayMusicContract.EXTRA_WIDGET_PROGRESS) as? Int)
                        ?: (intent.extras?.get(TopwayMusicContract.EXTRA_WIDGET_PROGRESS) as? Long)
                            ?.toInt()
                val seekTarget =
                    TopwayMusicSeekMapper.mapSeekTargetMs(
                        rawSeek,
                        playbackManager.currentSong?.durationMs,
                    ) ?: return
                if (playbackManager.currentSong != null) {
                    playbackManager.seekTo(seekTarget)
                }
            }
        }
    }

    private fun playFromHeadsetPlug() {
        // ACTION_HEADSET_PLUG will fire when this BroadcastReceiver is initially attached,
        // which would result in unexpected playback. Work around it by dropping the first
        // call to this function, which should come from that Intent.
        if (
            playbackSettings.headsetAutoplay &&
                playbackManager.currentSong != null &&
                initialHeadsetPlugEventHandled
        ) {
            L.d("Device connected, resuming")
            playbackManager.playing(true)
        }
    }

    private fun pauseFromHeadsetPlug() {
        if (playbackManager.currentSong != null) {
            L.d("Device disconnected, pausing")
            playbackManager.playing(false)
        }
    }

    private fun isSystemAction(action: String?): Boolean =
        action == AudioManager.ACTION_AUDIO_BECOMING_NOISY ||
            action == AudioManager.ACTION_HEADSET_PLUG

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
                addAction(PlaybackActions.ACTION_EXIT)
                addAction(WidgetProvider.ACTION_WIDGET_UPDATE)
                addAction(TopwayMusicContract.ACTION_CMD)
                addAction(TopwayMusicContract.ACTION_PREV)
                addAction(TopwayMusicContract.ACTION_NEXT)
                addAction(TopwayMusicContract.ACTION_PLAY_PAUSE)
                addAction(TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK)
            }
    }
}
