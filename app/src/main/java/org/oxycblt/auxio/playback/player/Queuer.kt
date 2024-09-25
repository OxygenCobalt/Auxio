/*
 * Copyright (c) 2024 Auxio Project
 * Queuer.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player.RepeatMode
import androidx.media3.exoplayer.ExoPlayer

interface Queuer {
    val currentMediaItem: MediaItem?
    val currentMediaItemIndex: Int
    val shuffleModeEnabled: Boolean

    @get:RepeatMode var repeatMode: Int

    fun attach()

    fun release()

    fun goto(mediaItemIndex: Int)

    fun seekToNext()

    fun hasNextMediaItem(): Boolean

    fun seekToPrevious()

    fun seekToPreviousMediaItem()

    fun hasPreviousMediaItem(): Boolean

    fun moveMediaItem(fromIndex: Int, toIndex: Int)

    fun removeMediaItem(index: Int)

    // EXTENSIONS
    fun computeHeap(): List<MediaItem>

    fun computeMapping(): List<Int>

    fun computeFirstMediaItemIndex(): Int

    fun prepareNew(mediaItems: List<MediaItem>, startIndex: Int?, shuffled: Boolean)

    fun prepareSaved(mediaItems: List<MediaItem>, mapping: List<Int>, index: Int, shuffled: Boolean)

    fun discard()

    fun addTopMediaItems(mediaItems: List<MediaItem>)

    fun addBottomMediaItems(mediaItems: List<MediaItem>)

    fun shuffled(shuffled: Boolean)

    interface Listener {
        fun onAutoTransition()
    }

    interface Factory {
        fun create(exoPlayer: ExoPlayer, listener: Listener): Queuer
    }
}
