/*
 * Copyright (c) 2024 Auxio Project
 * PlaybackActionHandler.kt is part of Auxio.
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

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.media3.common.Player
import androidx.media3.session.CommandButton
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionCommands
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.ActionMode
import org.oxycblt.auxio.playback.PlaybackSettings
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.Progression
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.widgets.WidgetComponent
import org.oxycblt.auxio.widgets.WidgetProvider

class PlaybackActionHandler
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val playbackManager: PlaybackStateManager,
    private val playbackSettings: PlaybackSettings,
    private val widgetComponent: WidgetComponent
) : PlaybackStateManager.Listener, PlaybackSettings.Listener {

    interface Callback {
        fun onCustomLayoutChanged(layout: List<CommandButton>)
    }

    private val systemReceiver =
        SystemPlaybackReceiver(playbackManager, playbackSettings, widgetComponent)
    private var callback: Callback? = null

    @SuppressLint("WrongConstant")
    fun attach(callback: Callback) {
        this.callback = callback
        playbackManager.addListener(this)
        playbackSettings.registerListener(this)
        ContextCompat.registerReceiver(
            context, systemReceiver, systemReceiver.intentFilter, ContextCompat.RECEIVER_EXPORTED)
    }

    fun release() {
        callback = null
        playbackManager.removeListener(this)
        playbackSettings.unregisterListener(this)
        context.unregisterReceiver(systemReceiver)
        widgetComponent.release()
    }

    fun withCommands(commands: SessionCommands) =
        commands
            .buildUpon()
            .add(SessionCommand(PlaybackActions.ACTION_INC_REPEAT_MODE, Bundle.EMPTY))
            .add(SessionCommand(PlaybackActions.ACTION_INVERT_SHUFFLE, Bundle.EMPTY))
            .add(SessionCommand(PlaybackActions.ACTION_EXIT, Bundle.EMPTY))
            .build()

    fun handleCommand(command: SessionCommand): Boolean {
        when (command.customAction) {
            PlaybackActions.ACTION_INC_REPEAT_MODE ->
                playbackManager.repeatMode(playbackManager.repeatMode.increment())
            PlaybackActions.ACTION_INVERT_SHUFFLE ->
                playbackManager.shuffled(!playbackManager.isShuffled)
            PlaybackActions.ACTION_EXIT -> playbackManager.endSession()
            else -> return false
        }
        return true
    }

    fun createCustomLayout(): List<CommandButton> {
        val actions = mutableListOf<CommandButton>()

        when (playbackSettings.notificationAction) {
            ActionMode.REPEAT -> {
                actions.add(
                    CommandButton.Builder()
                        .setIconResId(playbackManager.repeatMode.icon)
                        .setDisplayName(context.getString(R.string.desc_change_repeat))
                        .setSessionCommand(
                            SessionCommand(PlaybackActions.ACTION_INC_REPEAT_MODE, Bundle()))
                        .setEnabled(true)
                        .build())
            }
            ActionMode.SHUFFLE -> {
                actions.add(
                    CommandButton.Builder()
                        .setIconResId(
                            if (playbackManager.isShuffled) R.drawable.ic_shuffle_on_24
                            else R.drawable.ic_shuffle_off_24)
                        .setDisplayName(context.getString(R.string.lbl_shuffle))
                        .setSessionCommand(
                            SessionCommand(PlaybackActions.ACTION_INVERT_SHUFFLE, Bundle()))
                        .setEnabled(true)
                        .build())
            }
            else -> {}
        }

        actions.add(
            CommandButton.Builder()
                .setIconResId(R.drawable.ic_skip_prev_24)
                .setDisplayName(context.getString(R.string.desc_skip_prev))
                .setPlayerCommand(Player.COMMAND_SEEK_TO_PREVIOUS)
                .setEnabled(true)
                .build())

        actions.add(
            CommandButton.Builder()
                .setIconResId(R.drawable.ic_close_24)
                .setDisplayName(context.getString(R.string.desc_exit))
                .setSessionCommand(SessionCommand(PlaybackActions.ACTION_EXIT, Bundle()))
                .setEnabled(true)
                .build())

        return actions
    }

    override fun onPauseOnRepeatChanged() {
        super.onPauseOnRepeatChanged()
        callback?.onCustomLayoutChanged(createCustomLayout())
    }

    override fun onProgressionChanged(progression: Progression) {
        super.onProgressionChanged(progression)
        callback?.onCustomLayoutChanged(createCustomLayout())
    }

    override fun onRepeatModeChanged(repeatMode: RepeatMode) {
        super.onRepeatModeChanged(repeatMode)
        callback?.onCustomLayoutChanged(createCustomLayout())
    }

    override fun onQueueReordered(queue: List<Song>, index: Int, isShuffled: Boolean) {
        super.onQueueReordered(queue, index, isShuffled)
        callback?.onCustomLayoutChanged(createCustomLayout())
    }

    override fun onNotificationActionChanged() {
        super.onNotificationActionChanged()
        callback?.onCustomLayoutChanged(createCustomLayout())
    }
}

object PlaybackActions {
    const val ACTION_INC_REPEAT_MODE = BuildConfig.APPLICATION_ID + ".action.LOOP"
    const val ACTION_INVERT_SHUFFLE = BuildConfig.APPLICATION_ID + ".action.SHUFFLE"
    const val ACTION_SKIP_PREV = BuildConfig.APPLICATION_ID + ".action.PREV"
    const val ACTION_PLAY_PAUSE = BuildConfig.APPLICATION_ID + ".action.PLAY_PAUSE"
    const val ACTION_SKIP_NEXT = BuildConfig.APPLICATION_ID + ".action.NEXT"
    const val ACTION_EXIT = BuildConfig.APPLICATION_ID + ".action.EXIT"
}

/**
 * A [BroadcastReceiver] for receiving playback-specific [Intent]s from the system that require an
 * active [IntentFilter] to be registered.
 */
class SystemPlaybackReceiver(
    private val playbackManager: PlaybackStateManager,
    private val playbackSettings: PlaybackSettings,
    private val widgetComponent: WidgetComponent
) : BroadcastReceiver() {
    private var initialHeadsetPlugEventHandled = false

    val intentFilter =
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
}
