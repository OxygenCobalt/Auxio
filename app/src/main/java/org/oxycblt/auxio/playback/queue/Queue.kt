/*
 * Copyright (c) 2023 Auxio Project
 * Queue.kt is part of Auxio.
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

import kotlin.random.Random
import kotlin.random.nextInt
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song

/**
 * A heap-backed play queue.
 *
 * Whereas other queue implementations use a plain list, Auxio requires a more complicated data
 * structure in order to implement features such as gapless playback in ExoPlayer. This queue
 * implementation is instead based around an unorganized "heap" of [Song] instances, that are then
 * interpreted into different queues depending on the current playback configuration.
 *
 * In general, the implementation details don't need to be known for this data structure to be used,
 * except in special circumstances like [SavedState]. The functions exposed should be familiar for
 * any typical play queue.
 *
 * @author OxygenCobalt
 */
interface Queue {
    /** The index of the currently playing [Song] in the current mapping. */
    val index: Int
    /** The currently playing [Song]. */
    val currentSong: Song?
    /** Whether this queue is shuffled. */
    val isShuffled: Boolean
    /**
     * Resolve this queue into a more conventional list of [Song]s.
     *
     * @return A list of [Song] corresponding to the current queue mapping.
     */
    fun resolve(): List<Song>

    /**
     * Represents the possible changes that can occur during certain queue mutation events.
     *
     * @param type The [Type] of the change to the internal queue state.
     * @param instructions The update done to the resolved queue list.
     */
    data class Change(val type: Type, val instructions: UpdateInstructions) {
        enum class Type {
            /** Only the mapping has changed. */
            MAPPING,

            /** The mapping has changed, and the index also changed to align with it. */
            INDEX,

            /**
             * The current song has changed, possibly alongside the mapping and index depending on
             * the context.
             */
            SONG
        }
    }

    /**
     * An immutable representation of the queue state.
     *
     * @param heap The heap of [Song]s that are/were used in the queue. This can be modified with
     *   null values to represent [Song]s that were "lost" from the heap without having to change
     *   other values.
     * @param orderedMapping The mapping of the [heap] to an ordered queue.
     * @param shuffledMapping The mapping of the [heap] to a shuffled queue.
     * @param index The index of the currently playing [Song] at the time of serialization.
     * @param songUid The [Music.UID] of the [Song] that was originally at [index].
     */
    class SavedState(
        val heap: List<Song?>,
        val orderedMapping: List<Int>,
        val shuffledMapping: List<Int>,
        val index: Int,
        val songUid: Music.UID,
    ) {
        /**
         * Remaps the [heap] of this instance based on the given mapping function and copies it into
         * a new [SavedState].
         *
         * @param transform Code to remap the existing [Song] heap into a new [Song] heap. This
         *   **MUST** be the same size as the original heap. [Song] instances that could not be
         *   converted should be replaced with null in the new heap.
         * @throws IllegalStateException If the invariant specified by [transform] is violated.
         */
        inline fun remap(transform: (Song?) -> Song?) =
            SavedState(heap.map(transform), orderedMapping, shuffledMapping, index, songUid)
    }
}

class EditableQueue : Queue {
    @Volatile private var heap = mutableListOf<Song>()
    @Volatile private var orderedMapping = mutableListOf<Int>()
    @Volatile private var shuffledMapping = mutableListOf<Int>()
    @Volatile
    override var index = -1
        private set
    override val currentSong: Song?
        get() =
            shuffledMapping
                .ifEmpty { orderedMapping.ifEmpty { null } }
                ?.getOrNull(index)
                ?.let(heap::get)
    override val isShuffled: Boolean
        get() = shuffledMapping.isNotEmpty()

    override fun resolve() =
        if (currentSong != null) {
            shuffledMapping.map { heap[it] }.ifEmpty { orderedMapping.map { heap[it] } }
        } else {
            // Queue doesn't exist, return saner data.
            listOf()
        }

    /**
     * Go to a particular index in the queue.
     *
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
     *
     * @param play The [Song] to play, or null to start from a random position.
     * @param queue The queue of [Song]s to play. Must contain [play]. This list will become the
     *   heap internally.
     * @param shuffled Whether to shuffle the queue or not. This changes the interpretation of
     *   [queue].
     */
    fun start(play: Song?, queue: List<Song>, shuffled: Boolean) {
        heap = queue.toMutableList()
        orderedMapping = MutableList(queue.size) { it }
        shuffledMapping = mutableListOf()
        index =
            play?.let(queue::indexOf) ?: if (shuffled) Random.Default.nextInt(queue.indices) else 0
        reorder(shuffled)
        check()
    }

    /**
     * Re-order the queue.
     *
     * @param shuffled Whether the queue should be shuffled or not.
     */
    fun reorder(shuffled: Boolean) {
        if (orderedMapping.isEmpty()) {
            // Nothing to do.
            return
        }

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
            shuffledMapping = orderedMapping.shuffled().toMutableList()
            shuffledMapping.add(0, shuffledMapping.removeAt(shuffledMapping.indexOf(trueIndex)))
            index = 0
        } else if (shuffledMapping.isNotEmpty()) {
            // Un-shuffling, song to preserve is in the shuffled mapping.
            index = orderedMapping.indexOf(shuffledMapping[index])
            shuffledMapping = mutableListOf()
        }
        check()
    }

    /**
     * Add [Song]s to the top of the queue. Will start playback if nothing is playing.
     *
     * @param songs The [Song]s to add.
     * @return A [Queue.Change] instance that reflects the changes made.
     */
    fun playNext(songs: List<Song>): Queue.Change {
        val heapIndices = songs.map(::addSongToHeap)
        if (shuffledMapping.isNotEmpty()) {
            // Add the new songs in front of the current index in the shuffled mapping and in front
            // of the analogous list song in the ordered mapping.
            val orderedIndex = orderedMapping.indexOf(shuffledMapping[index])
            orderedMapping.addAll(orderedIndex + 1, heapIndices)
            shuffledMapping.addAll(index + 1, heapIndices)
        } else {
            // Add the new song in front of the current index in the ordered mapping.
            orderedMapping.addAll(index + 1, heapIndices)
        }
        check()
        return Queue.Change(
            Queue.Change.Type.MAPPING, UpdateInstructions.Add(index + 1, songs.size))
    }

    /**
     * Add [Song]s to the end of the queue. Will start playback if nothing is playing.
     *
     * @param songs The [Song]s to add.
     * @return A [Queue.Change] instance that reflects the changes made.
     */
    fun addToQueue(songs: List<Song>): Queue.Change {
        val heapIndices = songs.map(::addSongToHeap)
        // Can simple append the new songs to the end of both mappings.
        orderedMapping.addAll(heapIndices)
        if (shuffledMapping.isNotEmpty()) {
            shuffledMapping.addAll(heapIndices)
        }
        check()
        return Queue.Change(
            Queue.Change.Type.MAPPING, UpdateInstructions.Add(index + 1, songs.size))
    }

    /**
     * Move a [Song] at the given position to a new position.
     *
     * @param src The position of the [Song] to move.
     * @param dst The destination position of the [Song].
     * @return A [Queue.Change] instance that reflects the changes made.
     */
    fun move(src: Int, dst: Int): Queue.Change {
        if (shuffledMapping.isNotEmpty()) {
            // Move songs only in the shuffled mapping. There is no sane analogous form of
            // this for the ordered mapping.
            shuffledMapping.add(dst, shuffledMapping.removeAt(src))
        } else {
            // Move songs in the ordered mapping.
            orderedMapping.add(dst, orderedMapping.removeAt(src))
        }

        when (index) {
            // We are moving the currently playing song, correct the index to it's new position.
            src -> index = dst
            // We have moved an song from behind the playing song to in front, shift back.
            in (src + 1)..dst -> index -= 1
            // We have moved an song from in front of the playing song to behind, shift forward.
            in dst until src -> index += 1
            else -> {
                // Nothing to do.
                check()
                return Queue.Change(Queue.Change.Type.MAPPING, UpdateInstructions.Move(src, dst))
            }
        }
        check()
        return Queue.Change(Queue.Change.Type.INDEX, UpdateInstructions.Move(src, dst))
    }

    /**
     * Remove a [Song] at the given position.
     *
     * @param at The position of the [Song] to remove.
     * @return A [Queue.Change] instance that reflects the changes made.
     */
    fun remove(at: Int): Queue.Change {
        if (shuffledMapping.isNotEmpty()) {
            // Remove the specified index in the shuffled mapping and the analogous song in the
            // ordered mapping.
            orderedMapping.removeAt(orderedMapping.indexOf(shuffledMapping[at]))
            shuffledMapping.removeAt(at)
        } else {
            // Remove the specified index in the shuffled mapping
            orderedMapping.removeAt(at)
        }

        // Note: We do not clear songs out from the heap, as that would require the backing data
        // of the player to be completely invalidated. It's generally easier to not remove the
        // song and retain player state consistency.

        val type =
            when {
                // We just removed the currently playing song.
                index == at -> Queue.Change.Type.SONG
                // Index was ahead of removed song, shift back to preserve consistency.
                index > at -> {
                    index -= 1
                    Queue.Change.Type.INDEX
                }
                // Nothing to do
                else -> Queue.Change.Type.MAPPING
            }
        check()
        return Queue.Change(type, UpdateInstructions.Remove(at))
    }

    /**
     * Convert the current state of this instance into a [Queue.SavedState].
     *
     * @return A new [Queue.SavedState] reflecting the exact state of the queue when called.
     */
    fun toSavedState() =
        currentSong?.let { song ->
            Queue.SavedState(
                heap.toList(), orderedMapping.toList(), shuffledMapping.toList(), index, song.uid)
        }

    /**
     * Update this instance from the given [Queue.SavedState].
     *
     * @param savedState A [Queue.SavedState] with a valid queue representation.
     */
    fun applySavedState(savedState: Queue.SavedState) {
        val adjustments = mutableListOf<Int?>()
        var currentShift = 0
        for (song in savedState.heap) {
            if (song != null) {
                adjustments.add(currentShift)
            } else {
                adjustments.add(null)
                currentShift -= 1
            }
        }

        heap = savedState.heap.filterNotNull().toMutableList()
        orderedMapping =
            savedState.orderedMapping.mapNotNullTo(mutableListOf()) { heapIndex ->
                adjustments[heapIndex]?.let { heapIndex + it }
            }
        shuffledMapping =
            savedState.shuffledMapping.mapNotNullTo(mutableListOf()) { heapIndex ->
                adjustments[heapIndex]?.let { heapIndex + it }
            }

        // Make sure we re-align the index to point to the previously playing song.
        index = savedState.index
        while (currentSong?.uid != savedState.songUid && index > -1) {
            index--
        }
        check()
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

    private fun check() {
        check(!(heap.isEmpty() && (orderedMapping.isNotEmpty() || shuffledMapping.isNotEmpty()))) {
            "Queue inconsistency detected: Empty heap with non-empty mappings" +
                "[ordered: ${orderedMapping.size}, shuffled: ${shuffledMapping.size}]"
        }

        check(shuffledMapping.isEmpty() || orderedMapping.size == shuffledMapping.size) {
            "Queue inconsistency detected: Ordered mapping size ${orderedMapping.size} " +
                "!= Shuffled mapping size ${shuffledMapping.size}"
        }

        check(orderedMapping.all { it in heap.indices }) {
            "Queue inconsistency detected: Ordered mapping indices out of heap bounds"
        }

        check(shuffledMapping.all { it in heap.indices }) {
            "Queue inconsistency detected: Shuffled mapping indices out of heap bounds"
        }
    }
}
