/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.playback.system

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import org.oxycblt.auxio.playback.state.PlaybackStateManager

/**
 * A [BroadcastReceiver] that forwards [Intent.ACTION_MEDIA_BUTTON] [Intent]s to [PlaybackService].
 * @author Alexander Capehart (OxygenCobalt)
 */
class MediaButtonReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val playbackManager = PlaybackStateManager.getInstance()
        if (playbackManager.queue.currentSong != null) {
            // We have a song, so we can assume that the service will start a foreground state.
            // At least, I hope. Again, *this is why we don't do this*. I cannot describe how
            // stupid this is with the state of foreground services on modern android. One
            // wrong action at the wrong time will result in the app crashing, and there is
            // nothing I can do about it.
            intent.component = ComponentName(context, PlaybackService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }
    }
}
