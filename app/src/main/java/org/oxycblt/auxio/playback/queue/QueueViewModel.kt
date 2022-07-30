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
import org.oxycblt.auxio.ui.recycler.Item

class QueueViewModel : ViewModel(), PlaybackStateManager.Callback {
    private val playbackManager = PlaybackStateManager.getInstance()

    data class QueueSong(val song: Song, val previous: Boolean) : Item() {
        override val id: Long
            get() = song.id
    }

    private val _queue = MutableStateFlow(listOf<QueueSong>())
    val queue: StateFlow<List<QueueSong>> = _queue

    data class QueueInstructions(val replace: Boolean, val scrollTo: Int?)
    var instructions: QueueInstructions? = null

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

        return false
    }

    fun finishInstructions() {
        instructions = null
    }

    override fun onIndexMoved(index: Int) {
        instructions = QueueInstructions(false, min(index + 1, playbackManager.queue.lastIndex))
        _queue.value = generateQueue(index, playbackManager.queue)
    }

    override fun onQueueChanged(queue: List<Song>) {
        instructions = QueueInstructions(false, null)
        _queue.value = generateQueue(playbackManager.index, queue)
    }

    override fun onQueueReworked(index: Int, queue: List<Song>) {
        instructions = QueueInstructions(true, min(index + 1, playbackManager.queue.lastIndex))
        _queue.value = generateQueue(index, queue)
    }

    override fun onNewPlayback(index: Int, queue: List<Song>, parent: MusicParent?) {
        instructions = QueueInstructions(true, min(index + 1, playbackManager.queue.lastIndex))
        _queue.value = generateQueue(index, queue)
    }

    private fun generateQueue(index: Int, queue: List<Song>): List<QueueSong> {
        val before = queue.slice(0..index).map { QueueSong(it, true) }
        val after =
            queue.slice(index + 1..playbackManager.queue.lastIndex).map { QueueSong(it, false) }
        return before + after
    }

    override fun onCleared() {
        super.onCleared()
        playbackManager.removeCallback(this)
    }
}
