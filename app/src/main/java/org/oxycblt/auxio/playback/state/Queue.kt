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
 
package org.oxycblt.auxio.playback.state

import kotlin.random.Random
import kotlin.random.nextInt
import org.oxycblt.auxio.music.Song

/**
 * A heap-backed play queue.
 *
 * Whereas other queue implementations use a plain list, Auxio requires a more complicated data
 * structure in order to implement features such as gapless playback in ExoPlayer. This queue
 * implementation is instead based around an unorganized "heap" of [Song] instances, that are then
 * interpreted into different queues depending on the current playback configuration.
 *
 * In general, the implementation details don't need ot be known for this data structure to be used.
 * The functions exposed should be familiar for any typical play queue.
 *
 * @author OxygenCobalt
 */
class Queue {
    @Volatile private var heap = mutableListOf<Song>()
    @Volatile private var orderedMapping = mutableListOf<Int>()
    @Volatile private var shuffledMapping = mutableListOf<Int>()
    /** The index of the currently playing [Song] in the current mapping. */
    @Volatile
    var index = 0
        private set
    /** The currently playing [Song]. */
    val currentSong: Song?
        get() = shuffledMapping.ifEmpty { orderedMapping.ifEmpty { null } }?.let { heap[it[index]] }
    /** Whether this queue is shuffled. */
    val isShuffled: Boolean
        get() = shuffledMapping.isNotEmpty()

    /**
     * Resolve this queue into a more conventional list of [Song]s.
     * @return A list of [Song] corresponding to the current queue mapping.
     */
    fun resolve() = shuffledMapping.map { heap[it] }.ifEmpty { orderedMapping.map { heap[it] } }

    /**
     * Go to a particular index in the queue.
     * @param to The index of the [Song] to start playing, in the current queue mapping.
     * @return true if the queue jumped to that position, false otherwise.
     */
    fun goto(to: Int): Boolean {
        if (to !in orderedMapping.indices) {
            return false
        }
        index = to
        return true
    }

    /**
     * Start a new queue configuration.
     * @param song The [Song] to play, or null to start from a random position.
     * @param queue The queue of [Song]s to play. Must contain [song]. This list will become the
     * heap internally.
     * @param shuffled Whether to shuffle the queue or not. This changes the interpretation of
     * [queue].
     */
    fun start(song: Song?, queue: List<Song>, shuffled: Boolean) {
        heap = queue.toMutableList()
        orderedMapping = MutableList(queue.size) { it }
        shuffledMapping = mutableListOf()
        index =
            song?.let(queue::indexOf) ?: if (shuffled) Random.Default.nextInt(queue.indices) else 0
        reorder(shuffled)
    }

    /**
     * Re-order the queue.
     * @param shuffled Whether the queue should be shuffled or not.
     */
    fun reorder(shuffled: Boolean) {
        if (shuffled) {
            val trueIndex =
                if (shuffledMapping.isNotEmpty()) {
                    // Re-shuffling, song to preserve is in the shuffled mapping
                    shuffledMapping[index]
                } else {
                    // First shuffle, song to preserve is in the ordered mapping
                    orderedMapping[index]
                }

            // Since we are re-shuffling existing songs, we use the previous mapping size
            // instead of the total queue size.
            shuffledMapping = MutableList(orderedMapping.size) { it }.apply { shuffle() }
            shuffledMapping.add(0, shuffledMapping.removeAt(shuffledMapping.indexOf(trueIndex)))
            index = 0
        } else if (shuffledMapping.isNotEmpty()) {
            // Un-shuffling, song to preserve is in the shuffled mapping.
            index = orderedMapping.indexOf(shuffledMapping[index])
            shuffledMapping = mutableListOf()
        }
    }

    /**
     * Add [Song]s to the top of the queue. Will start playback if nothing is playing.
     * @param songs The [Song]s to add.
     * @return [ChangeResult.MAPPING] if added to an existing queue, or [ChangeResult.SONG] if there
     * was no prior playback and these enqueued [Song]s start new playback.
     */
    fun playNext(songs: List<Song>): ChangeResult {
        if (orderedMapping.isEmpty()) {
            // No playback, start playing these songs.
            start(songs[0], songs, false)
            return ChangeResult.SONG
        }

        val heapIndices = songs.map(::addSongToHeap)
        if (shuffledMapping.isNotEmpty()) {
            // Add the new songs in front of the current index in the shuffled mapping and in front
            // of the analogous list song in the ordered mapping.
            val orderedIndex = orderedMapping.indexOf(shuffledMapping[index])
            orderedMapping.addAll(orderedIndex, heapIndices)
            shuffledMapping.addAll(index, heapIndices)
        } else {
            // Add the new song in front of the current index in the ordered mapping.
            orderedMapping.addAll(index, heapIndices)
        }
        return ChangeResult.MAPPING
    }

    /**
     * Add [Song]s to the end of the queue. Will start playback if nothing is playing.
     * @param songs The [Song]s to add.
     * @return [ChangeResult.MAPPING] if added to an existing queue, or [ChangeResult.SONG] if there
     * was no prior playback and these enqueued [Song]s start new playback.
     */
    fun addToQueue(songs: List<Song>): ChangeResult {
        if (orderedMapping.isEmpty()) {
            // No playback, start playing these songs.
            start(songs[0], songs, false)
            return ChangeResult.SONG
        }

        val heapIndices = songs.map(::addSongToHeap)
        // Can simple append the new songs to the end of both mappings.
        orderedMapping.addAll(heapIndices)
        if (shuffledMapping.isNotEmpty()) {
            shuffledMapping.addAll(heapIndices)
        }
        return ChangeResult.MAPPING
    }

    /**
     * Move a [Song] at the given position to a new position.
     * @param src The position of the [Song] to move.
     * @param dst The destination position of the [Song].
     * @return [ChangeResult.MAPPING] if the move occurred after the current index,
     * [ChangeResult.INDEX] if the move occurred before or at the current index, requiring it to be
     * mutated.
     */
    fun move(src: Int, dst: Int): ChangeResult {

        if (shuffledMapping.isNotEmpty()) {
            // Move songs only in the shuffled mapping. There is no sane analogous form of
            // this for the ordered mapping.
            shuffledMapping.add(dst, shuffledMapping.removeAt(src))
        } else {
            // Move songs in the ordered mapping.
            orderedMapping.add(dst, orderedMapping.removeAt(src))
        }

        // TODO: I really need to figure out how to get non-swap moves working.
        return when (index) {
            src -> {
                index = dst
                ChangeResult.INDEX
            }
            dst -> {
                index = src
                ChangeResult.INDEX
            }
            else -> ChangeResult.MAPPING
        }
    }

    /**
     * Remove a [Song] at the given position.
     * @param at The position of the [Song] to remove.
     * @return [ChangeResult.MAPPING] if the removed [Song] was after the current index,
     * [ChangeResult.INDEX] if the removed [Song] was before the current index, and
     * [ChangeResult.SONG] if the currently playing [Song] was removed.
     */
    fun remove(at: Int): ChangeResult {
        if (shuffledMapping.isNotEmpty()) {
            // Remove the specified index in the shuffled mapping and the analogous song in the
            // ordered mapping.
            orderedMapping.removeAt(orderedMapping.indexOf(shuffledMapping[at]))
            shuffledMapping.removeAt(at)
        } else {
            // Remove the spe
            orderedMapping.removeAt(at)
        }

        // Note: We do not clear songs out from the heap, as that would require the backing data
        // of the player to be completely invalidated. It's generally easier to not remove the
        // song and retain player state consistency.

        return when {
            // We just removed the currently playing song.
            index == at -> ChangeResult.SONG
            // Index was ahead of removed song, shift back to preserve consistency.
            index > at -> {
                index -= 1
                ChangeResult.INDEX
            }
            // Nothing to do
            else -> ChangeResult.MAPPING
        }
    }

    private fun addSongToHeap(song: Song): Int {
        // We want to first try to see if there are any "orphaned" songs in the queue
        // that we can re-use. This way, we can reduce the memory used up by songs that
        // were previously removed from the queue.
        val currentMapping = orderedMapping
        if (orderedMapping.isNotEmpty()) {
            // While we could iterate through the queue and then check the mapping, it's
            // faster if we first check the queue for all instances of this song, and then
            // do a exclusion of this set of indices with the current mapping in order to
            // obtain the orphaned songs.
            val orphanCandidates = mutableSetOf<Int>()
            for (entry in heap.withIndex()) {
                if (entry.value == song) {
                    orphanCandidates.add(entry.index)
                }
            }
            orphanCandidates.removeAll(currentMapping.toSet())
            if (orphanCandidates.isNotEmpty()) {
                // There are orphaned songs, return the first one we find.
                return orphanCandidates.first()
            }
        }

        // Nothing to re-use, add this song to the queue
        heap.add(song)
        return heap.lastIndex
    }

    /**
     * Represents the possible changes that can occur during certain queue mutation events. The
     * precise meanings of these differ somewhat depending on the type of mutation done.
     */
    enum class ChangeResult {
        /** Only the mapping has changed. */
        MAPPING,
        /** The mapping has changed, and the index also changed to align with it. */
        INDEX,
        /**
         * The current song has changed, possibly alongside the mapping and index depending on the
         * context.
         */
        SONG
    }
}
