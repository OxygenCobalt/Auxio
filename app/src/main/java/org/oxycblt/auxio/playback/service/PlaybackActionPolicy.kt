/*
 * Copyright (c) 2024 Auxio Project
 * PlaybackActionPolicy.kt is part of Auxio.
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

import org.oxycblt.auxio.headunit.topway.TopwayMusicContract
import org.oxycblt.auxio.widgets.WidgetProvider

/** Pure policy for actions handled by [SystemPlaybackReceiver]. */
object PlaybackActionPolicy {
    val supportedActions: Set<String> =
        setOf(
            PlaybackActions.ACTION_PLAY_PAUSE,
            PlaybackActions.ACTION_INC_REPEAT_MODE,
            PlaybackActions.ACTION_INVERT_SHUFFLE,
            PlaybackActions.ACTION_SKIP_PREV,
            PlaybackActions.ACTION_SKIP_NEXT,
            PlaybackActions.ACTION_EXIT,
            WidgetProvider.ACTION_WIDGET_UPDATE,
        ) + TopwayMusicContract.INCOMING_ACTIONS

    fun isSupportedAction(action: String?): Boolean = action != null && action in supportedActions
}
