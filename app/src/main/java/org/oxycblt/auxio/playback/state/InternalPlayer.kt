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
 
package org.oxycblt.auxio.playback.state

import android.net.Uri
import org.oxycblt.auxio.music.Song

/** Represents a class capable of managing the internal player. */
interface InternalPlayer {
    /** The audio session ID of the player instance. */
    val audioSessionId: Int

    /** Whether the player should rewind instead of going to the previous song. */
    val shouldRewindWithPrev: Boolean

    /** Called when a new song should be loaded into the player. */
    fun loadSong(song: Song?)

    /** Seek to [positionMs] in the player. */
    fun seekTo(positionMs: Long)

    /** Called when the playing state is changed. */
    fun onPlayingChanged(isPlaying: Boolean)

    /**
     * Called when [PlaybackStateManager] desires some [Action] to be completed. Returns true if the
     * action was consumed, false otherwise.
     */
    fun onAction(action: Action): Boolean

    sealed class Action {
        object RestoreState : Action()
        object ShuffleAll : Action()
        data class Open(val uri: Uri) : Action()
    }
}
