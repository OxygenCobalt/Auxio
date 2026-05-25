/*
 * Copyright (c) 2026 Auxio Project
 * GenreShuffleQueueSelector.kt is part of Auxio.
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

/** Pure helper for selecting a genre-overlap queue while preserving the current item. */
object GenreShuffleQueueSelector {
    data class Selection<T>(val queue: List<T>, val currentIndex: Int)

    fun <T, K> select(
        current: T,
        allSongs: List<T>,
        currentGenres: Set<K>,
        songGenres: (T) -> Set<K>,
        songId: (T) -> Any,
        random: Random,
    ): Selection<T> {
        val currentId = songId(current)
        val dedup = LinkedHashMap<Any, T>(allSongs.size)
        for (song in allSongs) {
            val id = songId(song)
            if (id == currentId) {
                continue
            }
            val genres = songGenres(song)
            if (genres.isNotEmpty() && genres.any { it in currentGenres }) {
                dedup.putIfAbsent(id, song)
            }
        }

        val shuffledCandidates = dedup.values.toMutableList().apply { shuffle(random) }
        val queue = listOf(current) + shuffledCandidates
        return Selection(queue = queue, currentIndex = 0)
    }
}
