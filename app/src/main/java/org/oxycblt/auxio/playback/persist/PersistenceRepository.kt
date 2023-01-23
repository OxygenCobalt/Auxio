/*
 * Copyright (c) 2023 Auxio Project
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

import android.content.Context
import org.oxycblt.auxio.music.MusicParent
import org.oxycblt.auxio.music.library.Library
import org.oxycblt.auxio.playback.queue.Queue
import org.oxycblt.auxio.playback.state.RepeatMode
import org.oxycblt.auxio.util.logD

/**
 * Manages the persisted playback state in a structured manner.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface PersistenceRepository {
    /**
     * Read the previously persisted [SavedState].
     * @param library The [Library] required to de-serialize the [SavedState].
     */
    suspend fun readState(library: Library): SavedState?

    /**
     * Persist a new [SavedState].
     * @param state The [SavedState] to persist.
     */
    suspend fun saveState(state: SavedState?)

    /**
     * A condensed representation of the playback state that can be persisted.
     * @param parent The [MusicParent] item currently being played from.
     * @param queueState The [Queue.SavedState]
     * @param positionMs The current position in the currently played song, in ms
     * @param repeatMode The current [RepeatMode].
     */
    data class SavedState(
        val parent: MusicParent?,
        val queueState: Queue.SavedState,
        val positionMs: Long,
        val repeatMode: RepeatMode,
    )

    companion object {
        /**
         * Get a framework-backed implementation.
         * @param context [Context] required.
         */
        fun from(context: Context): PersistenceRepository = RealPersistenceRepository(context)
    }
}

private class RealPersistenceRepository(private val context: Context) : PersistenceRepository {
    private val database: PersistenceDatabase by lazy { PersistenceDatabase.getInstance(context) }
    private val playbackStateDao: PlaybackStateDao by lazy { database.playbackStateDao() }
    private val queueDao: QueueDao by lazy { database.queueDao() }

    override suspend fun readState(library: Library): PersistenceRepository.SavedState? {
        val playbackState = playbackStateDao.getState() ?: return null
        val heap = queueDao.getHeap()
        val mapping = queueDao.getMapping()

        val orderedMapping = mutableListOf<Int>()
        val shuffledMapping = mutableListOf<Int>()
        for (entry in mapping) {
            orderedMapping.add(entry.orderedIndex)
            shuffledMapping.add(entry.shuffledIndex)
        }

        val parent = playbackState.parentUid?.let { library.find<MusicParent>(it) }

        return PersistenceRepository.SavedState(
            parent = parent,
            queueState =
                Queue.SavedState(
                    heap.map { library.find(it.uid) },
                    orderedMapping,
                    shuffledMapping,
                    playbackState.index,
                    playbackState.songUid),
            positionMs = playbackState.positionMs,
            repeatMode = playbackState.repeatMode)
    }

    override suspend fun saveState(state: PersistenceRepository.SavedState?) {
        // Only bother saving a state if a song is actively playing from one.
        // This is not the case with a null state.
        playbackStateDao.nukeState()
        queueDao.nukeHeap()
        queueDao.nukeMapping()
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
            playbackStateDao.insertState(playbackState)

            // Convert the remaining queue information do their database-specific counterparts.
            val heap =
                state.queueState.heap.mapIndexed { i, song ->
                    QueueHeapItem(i, requireNotNull(song).uid)
                }
            queueDao.insertHeap(heap)
            val mapping =
                state.queueState.orderedMapping.zip(state.queueState.shuffledMapping).mapIndexed {
                    i,
                    pair ->
                    QueueMappingItem(i, pair.first, pair.second)
                }
            queueDao.insertMapping(mapping)
            logD("Wrote state")
        }
    }
}
