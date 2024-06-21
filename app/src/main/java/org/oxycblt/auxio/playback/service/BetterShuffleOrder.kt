/*
 * Copyright (c) 2017 Auxio Project
 * BetterShuffleOrder.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.service

import androidx.media3.common.C
import androidx.media3.exoplayer.source.ShuffleOrder

/**
 * A ShuffleOrder that fixes the poorly defined default implementation of cloneAndInsert. Whereas
 * the default implementation will randomly spread out added media items, this implementation will
 * insert them in the order they are added contiguously.
 *
 * @author media3 team, Alexander Capehart (OxygenCobalt)
 */
class BetterShuffleOrder(private val shuffled: IntArray) : ShuffleOrder {
    private val indexInShuffled: IntArray = IntArray(shuffled.size)

    constructor(length: Int, startIndex: Int) : this(createShuffledList(length, startIndex))

    init {
        for (i in shuffled.indices) {
            indexInShuffled[shuffled[i]] = i
        }
    }

    override fun getLength(): Int {
        return shuffled.size
    }

    override fun getNextIndex(index: Int): Int {
        var shuffledIndex = indexInShuffled[index]
        return if (++shuffledIndex < shuffled.size) shuffled[shuffledIndex] else C.INDEX_UNSET
    }

    override fun getPreviousIndex(index: Int): Int {
        var shuffledIndex = indexInShuffled[index]
        return if (--shuffledIndex >= 0) shuffled[shuffledIndex] else C.INDEX_UNSET
    }

    override fun getLastIndex(): Int {
        return if (shuffled.isNotEmpty()) shuffled[shuffled.size - 1] else C.INDEX_UNSET
    }

    override fun getFirstIndex(): Int {
        return if (shuffled.isNotEmpty()) shuffled[0] else C.INDEX_UNSET
    }

    @Suppress("KotlinConstantConditions") // Bugged for this function
    override fun cloneAndInsert(insertionIndex: Int, insertionCount: Int): ShuffleOrder {
        if (shuffled.isEmpty()) {
            return BetterShuffleOrder(insertionCount, -1)
        }

        // TODO: Fix this scuffed hacky logic
        // TODO: Play next ordering needs to persist in unshuffle

        val newShuffled = IntArray(shuffled.size + insertionCount)
        val pivot: Int =
            if (insertionIndex < shuffled.size) {
                indexInShuffled[insertionIndex]
            } else {
                indexInShuffled.size
            }
        for (i in shuffled.indices) {
            var currentIndex = shuffled[i]
            if (currentIndex > insertionIndex) {
                currentIndex += insertionCount
            }

            if (i <= pivot) {
                newShuffled[i] = currentIndex
            } else if (i > pivot) {
                newShuffled[i + insertionCount] = currentIndex
            }
        }
        if (insertionIndex < shuffled.size) {
            for (i in 0 until insertionCount) {
                newShuffled[pivot + i + 1] = insertionIndex + i + 1
            }
        } else {
            for (i in 0 until insertionCount) {
                newShuffled[pivot + i] = insertionIndex + i
            }
        }
        return BetterShuffleOrder(newShuffled)
    }

    override fun cloneAndRemove(indexFrom: Int, indexToExclusive: Int): ShuffleOrder {
        val numberOfElementsToRemove = indexToExclusive - indexFrom
        val newShuffled = IntArray(shuffled.size - numberOfElementsToRemove)
        var foundElementsCount = 0
        for (i in shuffled.indices) {
            if (shuffled[i] in indexFrom until indexToExclusive) {
                foundElementsCount++
            } else {
                newShuffled[i - foundElementsCount] =
                    if (shuffled[i] >= indexFrom) shuffled[i] - numberOfElementsToRemove
                    else shuffled[i]
            }
        }
        return BetterShuffleOrder(newShuffled)
    }

    override fun cloneAndClear(): ShuffleOrder {
        return BetterShuffleOrder(0, -1)
    }

    companion object {
        private fun createShuffledList(length: Int, startIndex: Int): IntArray {
            val shuffled = IntArray(length)
            for (i in 0 until length) {
                val swapIndex = (0..i).random()
                shuffled[i] = shuffled[swapIndex]
                shuffled[swapIndex] = i
            }
            if (startIndex != -1) {
                val startIndexInShuffled = shuffled.indexOf(startIndex)
                val temp = shuffled[0]
                shuffled[0] = shuffled[startIndexInShuffled]
                shuffled[startIndexInShuffled] = temp
            }
            return shuffled
        }
    }
}
