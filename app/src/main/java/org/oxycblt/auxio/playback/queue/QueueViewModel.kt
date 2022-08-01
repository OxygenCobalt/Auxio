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
import kotlin.math.min
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.state.PlaybackStateManager

/**
 * Class enabling more advanced queue list functionality and queue editing.
 * @author OxygenCobalt
 */
class QueueViewModel : ViewModel(), PlaybackStateManager.Callback {
    private val playbackManager = PlaybackStateManager.getInstance()

    private val _queue = MutableStateFlow(listOf<Song>())
    val queue: StateFlow<List<Song>> = _queue

    private val _index = MutableStateFlow(playbackManager.index)
    val index: StateFlow<Int>
        get() = _index

    var replaceQueue: Boolean? = null
    var scrollTo: Int? = null

    init {
        playbackManager.addCallback(this)
    }

    /**
     * Go to an item in the queue using it's recyclerview adapter index. No-ops if out of bounds.
     */
    fun goto(adapterIndex: Int) {
        if (adapterIndex !in playbackManager.queue.indices) {
            return
        }

        playbackManager.goto(adapterIndex)
    }

    /** Remove a queue item using it's recyclerview adapter index. */
    fun removeQueueDataItem(adapterIndex: Int) {
        if (adapterIndex <= playbackManager.index ||
            adapterIndex !in playbackManager.queue.indices) {
            return
        }

        playbackManager.removeQueueItem(adapterIndex)
    }

    /** Move queue items using their recyclerview adapter indices. */
    fun moveQueueDataItems(adapterFrom: Int, adapterTo: Int): Boolean {
        if (adapterFrom <= playbackManager.index || adapterTo <= playbackManager.index) {
            return false
        }

        playbackManager.moveQueueItem(adapterFrom, adapterTo)

        return true
    }

    fun finishReplace() {
        replaceQueue = null
    }

    fun finishScrollTo() {
        scrollTo = null
    }

    override fun onIndexMoved(index: Int) {
        replaceQueue = null
        scrollTo = min(index + 1, playbackManager.queue.lastIndex)
        _index.value = index
    }

    override fun onQueueChanged(queue: List<Song>) {
        replaceQueue = false
        scrollTo = null
        _queue.value = playbackManager.queue.toMutableList()
    }

    override fun onQueueReworked(index: Int, queue: List<Song>) {
        replaceQueue = true
        scrollTo = min(index + 1, playbackManager.queue.lastIndex)
        _queue.value = playbackManager.queue.toMutableList()
        _index.value = index
    }

    override fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) {
        replaceQueue = true
        scrollTo = min(index + 1, playbackManager.queue.lastIndex)
        _queue.value = playbackManager.queue.toMutableList()
        _index.value = index
    }

    override fun onCleared() {
        super.onCleared()
        playbackManager.removeCallback(this)
    }
}
