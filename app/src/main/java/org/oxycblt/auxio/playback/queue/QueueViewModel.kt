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
import org.oxycblt.auxio.util.logD

class QueueViewModel : ViewModel(), PlaybackStateManager.Callback {
    private val playbackManager = PlaybackStateManager.getInstance()

    data class QueueData(val queue: List<Song>, val nonTrivial: Boolean)

    private val _queue = MutableStateFlow(QueueData(listOf(), false))
    val queue: StateFlow<QueueData> = _queue

    init {
        playbackManager.addCallback(this)
    }

    /**
     * Go to an item in the queue using it's recyclerview adapter index. No-ops if out of bounds.
     */
    fun goto(adapterIndex: Int) {
        val index = adapterIndex + (playbackManager.queue.size - _queue.value.queue.size)
        logD(adapterIndex)
        logD(playbackManager.queue.size - _queue.value.queue.size)

        if (index in playbackManager.queue.indices) {
            playbackManager.goto(index)
        }
    }

    /** Remove a queue item using it's recyclerview adapter index. */
    fun removeQueueDataItem(adapterIndex: Int) {
        val index = adapterIndex + (playbackManager.queue.size - _queue.value.queue.size)
        if (index in playbackManager.queue.indices) {
            playbackManager.removeQueueItem(index)
        }
    }
    /** Move queue items using their recyclerview adapter indices. */
    fun moveQueueDataItems(adapterFrom: Int, adapterTo: Int): Boolean {
        val delta = (playbackManager.queue.size - _queue.value.queue.size)
        val from = adapterFrom + delta
        val to = adapterTo + delta
        if (from in playbackManager.queue.indices && to in playbackManager.queue.indices) {
            playbackManager.moveQueueItem(from, to)
            return true
        }

        return false
    }

    override fun onIndexMoved(index: Int) {
        _queue.value = QueueData(generateQueue(index, playbackManager.queue), false)
    }

    override fun onQueueChanged(queue: List<Song>) {
        _queue.value = QueueData(generateQueue(playbackManager.index, queue), false)
    }

    override fun onQueueReworked(index: Int, queue: List<Song>) {
        _queue.value = QueueData(generateQueue(index, queue), true)
    }

    override fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) {
        _queue.value = QueueData(generateQueue(index, queue), true)
    }

    private fun generateQueue(index: Int, queue: List<Song>) =
        queue.slice(index + 1..playbackManager.queue.lastIndex)

    override fun onCleared() {
        super.onCleared()
        playbackManager.removeCallback(this)
    }
}
