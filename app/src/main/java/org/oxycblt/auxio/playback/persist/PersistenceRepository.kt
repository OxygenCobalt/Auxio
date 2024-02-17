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
        val heapItems: List<QueueHeapItem>
        val mappingItems: List<QueueShuffledMappingItem>
        try {
            playbackState = playbackStateDao.getState() ?: return null
            heapItems = queueDao.getHeap()
            mappingItems = queueDao.getShuffledMapping()
        } catch (e: Exception) {
            logE("Unable read playback state")
            logE(e.stackTraceToString())
            return null
        }

        val heap = heapItems.map { deviceLibrary.findSong(it.uid) }
        val shuffledMapping = mappingItems.map { it.index }
        val parent = playbackState.parentUid?.let { musicRepository.find(it) as? MusicParent }

        return PlaybackStateManager.SavedState(
            positionMs = playbackState.positionMs,
            repeatMode = playbackState.repeatMode,
            parent = parent,
            heap = heap,
            shuffledMapping = shuffledMapping,
            index = playbackState.index,
            songUid = playbackState.songUid)
    }

    override suspend fun saveState(state: PlaybackStateManager.SavedState?): Boolean {
        try {
            playbackStateDao.nukeState()
            queueDao.nukeHeap()
            queueDao.nukeShuffledMapping()
        } catch (e: Exception) {
            logE("Unable to clear previous state")
            logE(e.stackTraceToString())
            return false
        }

        logD("Successfully cleared previous state")
        if (state != null) {
            // Transform saved state into raw state, which can then be written to the database.
            val playbackState =
                PlaybackState(
                    id = 0,
                    index = state.index,
                    positionMs = state.positionMs,
                    repeatMode = state.repeatMode,
                    songUid = state.songUid,
                    parentUid = state.parent?.uid)

            // Convert the remaining queue information do their database-specific counterparts.
            val heap =
                state.heap.mapIndexed { i, song -> QueueHeapItem(i, requireNotNull(song).uid) }

            val shuffledMapping =
                state.shuffledMapping.mapIndexed { i, index -> QueueShuffledMappingItem(i, index) }

            try {
                playbackStateDao.insertState(playbackState)
                queueDao.insertHeap(heap)
                queueDao.insertShuffledMapping(shuffledMapping)
            } catch (e: Exception) {
                logE("Unable to write new state")
                logE(e.stackTraceToString())
                return false
            }

            logD("Successfully wrote new state")
        }

        return true
    }
}
