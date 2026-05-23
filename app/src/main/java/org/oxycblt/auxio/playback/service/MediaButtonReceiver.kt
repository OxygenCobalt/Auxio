/*
 * Copyright (c) 2022 Auxio Project
 * MediaButtonReceiver.kt is part of Auxio.
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
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.oxycblt.auxio.AuxioService
import org.oxycblt.auxio.IntegerTable
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import timber.log.Timber as L

/**
 * A [BroadcastReceiver] that forwards [Intent.ACTION_MEDIA_BUTTON] [Intent]s to
 * [PlaybackServiceFragment].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class MediaButtonReceiver : BroadcastReceiver() {
    @Inject lateinit var playbackManager: PlaybackStateManager

    // TODO: Figure this out
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_MEDIA_BUTTON) {
            return
        }

        val event =
            IntentCompat.getParcelableExtra(intent, Intent.EXTRA_KEY_EVENT, KeyEvent::class.java)
        val hasCurrentSong = playbackManager.currentSong != null
        val isFocusHeld = playbackManager.isAudioFocusHeld
        if (
            !MediaButtonActionMapper.shouldForward(
                event,
                hasCurrentSong = hasCurrentSong,
                isFocusHeld =
                    AudioFocusPolicy.shouldHandleMediaButton(
                        isFocusHeld = isFocusHeld,
                        hasCurrentSong = hasCurrentSong,
                        sessionOngoing = hasCurrentSong,
                    ),
            )
        ) {
            L.d("Ignoring media button event after policy evaluation: $event")
            return
        }

        L.d("Delivering media button intent $intent")
        intent.component = ComponentName(context, AuxioService::class.java)
        intent.putExtra(
            AuxioService.INTENT_KEY_START_ID,
            IntegerTable.START_ID_MEDIA_BUTTON,
        )
        ContextCompat.startForegroundService(context, intent)
    }
}
