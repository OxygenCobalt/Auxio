/*
 * Copyright (c) 2021 Auxio Project
 * WidgetState.kt is part of Auxio.
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

import android.graphics.Bitmap
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.LoopMode

/*
 * An immutable condensed variant of the current playback state, used so that PlaybackStateManager
 * does not need to be queried directly.
 */
data class WidgetState(
    val song: Song,
    val albumArt: Bitmap?,
    val isPlaying: Boolean,
    val isShuffled: Boolean,
    val loopMode: LoopMode,
)
