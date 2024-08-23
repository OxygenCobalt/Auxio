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

import android.content.Context
import android.os.Bundle
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

class PlaybackActionHandler
@Inject
constructor(
    @ApplicationContext private val context: Context,
    private val playbackManager: PlaybackStateManager,
    private val playbackSettings: PlaybackSettings
) : PlaybackStateManager.Listener, PlaybackSettings.Listener {

    interface Callback {
        fun onCustomLayoutChanged(layout: List<CommandButton>)
    }

    private var callback: Callback? = null

    fun attach(callback: Callback) {
        this.callback = callback
        playbackManager.addListener(this)
        playbackSettings.registerListener(this)
    }

    fun release() {
        callback = null
        playbackManager.removeListener(this)
        playbackSettings.unregisterListener(this)
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
