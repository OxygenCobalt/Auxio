/*
 * Copyright (c) 2023 Auxio Project
 * PersistenceRepository.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.persist

import javax.inject.Inject
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.MusicRepository
import org.oxycblt.auxio.playback.queue.Queue
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logE

/**
 * Manages the persisted playback state in a structured manner.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
interface PersistenceRepository {
    /** Read the previously persisted [PlaybackStateManager.SavedState]. */
    suspend fun readState(): PlaybackStateManager.SavedState?

    /**
     * Persist a new [PlaybackStateManager.SavedState].
     *
     * @param state The [PlaybackStateManager.SavedState] to persist.
     */
    suspend fun saveState(state: PlaybackStateManager.SavedState?): Boolean
}

class PersistenceRepositoryImpl
@Inject
constructor(
    private val playbackStateDao: PlaybackStateDao,
    private val queueDao: QueueDao,
    private val musicRepository: MusicRepository
) : PersistenceRepository {

    override suspend fun readState(): PlaybackStateManager.SavedState? {
        val deviceLibrary = musicRepository.deviceLibrary ?: return null
        val playbackState: PlaybackState
        val heap: List<QueueHeapItem>
        val mapping: List<QueueMappingItem>
        try {
            playbackState = playbackStateDao.getState() ?: return null
            heap = queueDao.getHeap()
            mapping = queueDao.getMapping()
        } catch (e: Exception) {
            logE("Unable to load playback state data")
            logE(e.stackTraceToString())
            return null
        }

        val orderedMapping = mutableListOf<Int>()
        val shuffledMapping = mutableListOf<Int>()
        for (entry in mapping) {
            orderedMapping.add(entry.orderedIndex)
            shuffledMapping.add(entry.shuffledIndex)
        }

        val parent = playbackState.parentUid?.let { musicRepository.find(it) as? MusicParent }
        logD("Read playback state")

        return PlaybackStateManager.SavedState(
            parent = parent,
            queueState =
                Queue.SavedState(
                    heap.map { deviceLibrary.findSong(it.uid) },
                    orderedMapping,
                    shuffledMapping,
                    playbackState.index,
                    playbackState.songUid),
            positionMs = playbackState.positionMs,
            repeatMode = playbackState.repeatMode)
    }

    override suspend fun saveState(state: PlaybackStateManager.SavedState?): Boolean {
        // Only bother saving a state if a song is actively playing from one.
        // This is not the case with a null state.
        try {
            playbackStateDao.nukeState()
            queueDao.nukeHeap()
            queueDao.nukeMapping()
        } catch (e: Exception) {
            logE("Unable to clear previous state")
            logE(e.stackTraceToString())
            return false
        }
        logD("Cleared state")
        if (state != null) {
            // Transform saved state into raw state, which can then be written to the database.
            val playbackState =
                PlaybackState(
                    id = 0,
                    index = state.queueState.index,
                    positionMs = state.positionMs,
                    repeatMode = state.repeatMode,
                    songUid = state.queueState.songUid,
                    parentUid = state.parent?.uid)

            // Convert the remaining queue information do their database-specific counterparts.
            val heap =
                state.queueState.heap.mapIndexed { i, song ->
                    QueueHeapItem(i, requireNotNull(song).uid)
                }
            val mapping =
                state.queueState.orderedMapping.zip(state.queueState.shuffledMapping).mapIndexed {
                    i,
                    pair ->
                    QueueMappingItem(i, pair.first, pair.second)
                }
            try {
                playbackStateDao.insertState(playbackState)
                queueDao.insertHeap(heap)
                queueDao.insertMapping(mapping)
            } catch (e: Exception) {
                logE("Unable to write new state")
                logE(e.stackTraceToString())
                return false
            }
            logD("Wrote state")
        }
        return true
    }
}
