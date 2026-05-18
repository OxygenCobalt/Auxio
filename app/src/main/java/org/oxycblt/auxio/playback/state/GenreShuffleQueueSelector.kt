package org.oxycblt.auxio.playback.state

import kotlin.random.Random

/**
 * Pure helper for selecting a genre-overlap queue while preserving the current item.
 */
object GenreShuffleQueueSelector {
    data class Selection<T>(
        val queue: List<T>,
        val currentIndex: Int,
    )

    fun <T, K> select(
        current: T,
        allSongs: List<T>,
        currentGenres: Set<K>,
        songGenres: (T) -> Set<K>,
        songId: (T) -> Any,
        random: Random,
    ): Selection<T> {
        val dedup = LinkedHashMap<Any, T>()
        for (song in allSongs) {
            val genres = songGenres(song)
            if (genres.isNotEmpty() && genres.any { it in currentGenres }) {
                dedup[songId(song)] = song
            }
        }

        dedup[songId(current)] = current

        val candidates = dedup.values.toMutableList()
        val currentId = songId(current)
        val remainder = candidates.filterNot { songId(it) == currentId }.shuffled(random)
        val queue = listOf(current) + remainder
        return Selection(queue = queue, currentIndex = 0)
    }
}
