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
 
package org.oxycblt.auxio.playback.queue

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackStateManager

/**
 * A [ViewModel] that manages the current queue state and allows navigation through the queue.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class QueueViewModel : ViewModel(), PlaybackStateManager.Listener {
    private val playbackManager = PlaybackStateManager.getInstance()

    private val _queue = MutableStateFlow(listOf<Song>())
    /** The current queue. */
    val queue: StateFlow<List<Song>> = _queue

    private val _index = MutableStateFlow(playbackManager.index)
    /** The index of the currently playing song in the queue. */
    val index: StateFlow<Int>
        get() = _index

    /** Whether to replace or diff the queue list when updating it. Is null if not specified. */
    var replaceQueue: Boolean? = null
    /** Flag to scroll to a particular queue item. Is null if no command has been specified. */
    var scrollTo: Int? = null

    init {
        playbackManager.addListener(this)
    }

    /**
     * Start playing the the queue item at the given index.
     * @param adapterIndex The index of the queue item to play. Does nothing if the index is out of
     * range.
     */
    fun goto(adapterIndex: Int) {
        if (adapterIndex !in playbackManager.queue.indices) {
            // Invalid input. Nothing to do.
            return
        }
        playbackManager.goto(adapterIndex)
    }

    /**
     * Remove a queue item at the given index.
     * @param adapterIndex The index of the queue item to play. Does nothing if the index is out of
     * range.
     */
    fun removeQueueDataItem(adapterIndex: Int) {
        if (adapterIndex <= playbackManager.index ||
            adapterIndex !in playbackManager.queue.indices) {
            // Invalid input. Nothing to do.
            // TODO: Allow editing played queue items.
            return
        }
        playbackManager.removeQueueItem(adapterIndex)
    }

    /**
     * Move a queue item from one index to another index.
     * @param adapterFrom The index of the queue item to move.
     * @param adapterTo The destination index for the queue item.
     * @return true if the items were moved, false otherwise.
     */
    fun moveQueueDataItems(adapterFrom: Int, adapterTo: Int): Boolean {
        if (adapterFrom <= playbackManager.index || adapterTo <= playbackManager.index) {
            // Invalid input. Nothing to do.
            return false
        }
        playbackManager.moveQueueItem(adapterFrom, adapterTo)
        return true
    }

    /** Finish a replace flag specified by [replaceQueue]. */
    fun finishReplace() {
        replaceQueue = null
    }

    /** Finish a scroll operation started by [scrollTo]. */
    fun finishScrollTo() {
        scrollTo = null
    }

    override fun onIndexMoved(index: Int) {
        // Index moved -> Scroll to new index
        replaceQueue = null
        scrollTo = index
        _index.value = index
    }

    override fun onQueueChanged(queue: List<Song>) {
        // Queue changed trivially due to item move -> Diff queue, stay at current index.
        replaceQueue = false
        scrollTo = null
        _queue.value = playbackManager.queue.toMutableList()
    }

    override fun onQueueReworked(index: Int, queue: List<Song>) {
        // Queue changed completely -> Replace queue, update index
        replaceQueue = true
        scrollTo = index
        _queue.value = playbackManager.queue.toMutableList()
        _index.value = index
    }

    override fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) {
        // Entirely new queue -> Replace queue, update index
        replaceQueue = true
        scrollTo = index
        _queue.value = playbackManager.queue.toMutableList()
        _index.value = index
    }

    override fun onCleared() {
        super.onCleared()
        playbackManager.removeListener(this)
    }
}
