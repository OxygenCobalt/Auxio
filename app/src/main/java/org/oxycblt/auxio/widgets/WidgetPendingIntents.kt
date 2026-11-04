/*
 * Copyright (c) 2026 Auxio Project
 * WidgetPendingIntents.kt is part of Auxio.
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
 
package org.oxycblt.auxio.widgets

import android.content.Context
import org.oxycblt.auxio.playback.service.PlaybackActions
import org.oxycblt.auxio.util.newBroadcastPendingIntent
import org.oxycblt.auxio.util.newMainPendingIntent

/**
 * Retrieve each action once per widget update, then share its handle across the responsive layouts.
 * Recreate this bundle on the next update so cancelled system tokens can be replaced normally.
 */
internal class WidgetPendingIntents(context: Context) {
    val main = context.newMainPendingIntent()
    val playPause = context.newBroadcastPendingIntent(PlaybackActions.ACTION_PLAY_PAUSE)
    val skipPrevious = context.newBroadcastPendingIntent(PlaybackActions.ACTION_SKIP_PREV)
    val skipNext = context.newBroadcastPendingIntent(PlaybackActions.ACTION_SKIP_NEXT)
    val repeat = context.newBroadcastPendingIntent(PlaybackActions.ACTION_INC_REPEAT_MODE)
    val shuffle = context.newBroadcastPendingIntent(PlaybackActions.ACTION_INVERT_SHUFFLE)
}
