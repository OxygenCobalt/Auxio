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

import org.oxycblt.auxio.BuildConfig

object PlaybackActions {
    const val ACTION_INC_REPEAT_MODE = BuildConfig.APPLICATION_ID + ".action.LOOP"
    const val ACTION_INVERT_SHUFFLE = BuildConfig.APPLICATION_ID + ".action.SHUFFLE"
    const val ACTION_SKIP_PREV = BuildConfig.APPLICATION_ID + ".action.PREV"
    const val ACTION_PLAY_PAUSE = BuildConfig.APPLICATION_ID + ".action.PLAY_PAUSE"
    const val ACTION_SKIP_NEXT = BuildConfig.APPLICATION_ID + ".action.NEXT"
    const val ACTION_EXIT = BuildConfig.APPLICATION_ID + ".action.EXIT"
}
