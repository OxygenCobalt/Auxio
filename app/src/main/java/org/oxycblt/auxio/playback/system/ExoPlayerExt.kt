/*
 * Copyright (c) 2024 Auxio Project
 * ExoPlayerExt.kt is part of Auxio.
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

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.RawQueue
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.util.logD

val ExoPlayer.song
    get() = currentMediaItem?.song

fun ExoPlayer.resolveQueue(): RawQueue {
    val heap = (0 until mediaItemCount).map { getMediaItemAt(it).song }
    val shuffledMapping = if (shuffleModeEnabled) unscrambleQueueIndices() else emptyList()
    logD(shuffledMapping)
    return RawQueue(heap, shuffledMapping, currentMediaItemIndex)
}

val ExoPlayer.repeat: RepeatMode
    get() =
        when (repeatMode) {
            Player.REPEAT_MODE_OFF -> RepeatMode.NONE
            Player.REPEAT_MODE_ONE -> RepeatMode.TRACK
            Player.REPEAT_MODE_ALL -> RepeatMode.ALL
            else -> throw IllegalStateException("Unknown repeat mode: $repeatMode")
        }

fun ExoPlayer.orderedQueue(queue: Collection<Song>, start: Song?) {
    clearMediaItems()
    shuffleModeEnabled = false
    setMediaItems(queue.map { it.toMediaItem() })
    if (start != null) {
        val startIndex = queue.indexOf(start)
        if (startIndex != -1) {
            seekTo(startIndex, C.TIME_UNSET)
        } else {
            throw IllegalArgumentException("Start song not in queue")
        }
    }
}

fun ExoPlayer.shuffledQueue(queue: Collection<Song>, start: Song?) {
    setMediaItems(queue.map { it.toMediaItem() })
    shuffleModeEnabled = true
    val startIndex =
        if (start != null) {
            queue.indexOf(start).also { check(it != -1) { "Start song not in queue" } }
        } else {
            -1
        }
    setShuffleOrder(BetterShuffleOrder(queue.size, startIndex))
    seekTo(currentTimeline.getFirstWindowIndex(shuffleModeEnabled), C.TIME_UNSET)
}

fun ExoPlayer.applyQueue(rawQueue: RawQueue) {
    setMediaItems(rawQueue.heap.map { it.toMediaItem() })
    if (rawQueue.isShuffled) {
        shuffleModeEnabled = true
        setShuffleOrder(BetterShuffleOrder(rawQueue.shuffledMapping.toIntArray()))
    } else {
        shuffleModeEnabled = false
    }
    seekTo(rawQueue.heapIndex, C.TIME_UNSET)
}

fun ExoPlayer.shuffled(shuffled: Boolean) {
    logD("Reordering queue to $shuffled")
    shuffleModeEnabled = shuffled
    if (shuffled) {
        // Have to manually refresh the shuffle seed and anchor it to the new current songs
        setShuffleOrder(BetterShuffleOrder(mediaItemCount, currentMediaItemIndex))
    }
}

fun ExoPlayer.playNext(songs: List<Song>) {
    addMediaItems(nextMediaItemIndex, songs.map { it.toMediaItem() })
}

fun ExoPlayer.addToQueue(songs: List<Song>) {
    addMediaItems(songs.map { it.toMediaItem() })
}

fun ExoPlayer.goto(index: Int) {
    val indices = unscrambleQueueIndices()
    if (indices.isEmpty()) {
        return
    }

    val trueIndex = indices[index]
    seekTo(trueIndex, C.TIME_UNSET)
}

fun ExoPlayer.move(from: Int, to: Int) {
    val indices = unscrambleQueueIndices()
    if (indices.isEmpty()) {
        return
    }

    val trueFrom = indices[from]
    val trueTo = indices[to]

    when {
        trueFrom > trueTo -> {
            moveMediaItem(trueFrom, trueTo)
            moveMediaItem(trueTo + 1, trueFrom)
        }
        trueTo > trueFrom -> {
            moveMediaItem(trueFrom, trueTo)
            moveMediaItem(trueTo - 1, trueFrom)
        }
    }
}

fun ExoPlayer.remove(at: Int) {
    val indices = unscrambleQueueIndices()
    if (indices.isEmpty()) {
        return
    }

    val trueIndex = indices[at]
    removeMediaItem(trueIndex)
}

fun ExoPlayer.unscrambleQueueIndices(): List<Int> {
    val timeline = currentTimeline
    if (timeline.isEmpty()) {
        return emptyList()
    }
    val queue = mutableListOf<Int>()

    // Add the active queue item.
    val currentMediaItemIndex = currentMediaItemIndex
    queue.add(currentMediaItemIndex)

    // Fill queue alternating with next and/or previous queue items.
    var firstMediaItemIndex = currentMediaItemIndex
    var lastMediaItemIndex = currentMediaItemIndex
    val shuffleModeEnabled = shuffleModeEnabled
    while ((firstMediaItemIndex != C.INDEX_UNSET || lastMediaItemIndex != C.INDEX_UNSET)) {
        // Begin with next to have a longer tail than head if an even sized queue needs to be
        // trimmed.
        if (lastMediaItemIndex != C.INDEX_UNSET) {
            lastMediaItemIndex =
                timeline.getNextWindowIndex(
                    lastMediaItemIndex, Player.REPEAT_MODE_OFF, shuffleModeEnabled)
            if (lastMediaItemIndex != C.INDEX_UNSET) {
                queue.add(lastMediaItemIndex)
            }
        }
        if (firstMediaItemIndex != C.INDEX_UNSET) {
            firstMediaItemIndex =
                timeline.getPreviousWindowIndex(
                    firstMediaItemIndex, Player.REPEAT_MODE_OFF, shuffleModeEnabled)
            if (firstMediaItemIndex != C.INDEX_UNSET) {
                queue.add(0, firstMediaItemIndex)
            }
        }
    }

    return queue
}

fun Song.toMediaItem() = MediaItem.Builder().setUri(uri).setTag(this).build()

private val MediaItem.song: Song
    get() = requireNotNull(localConfiguration).tag as Song
