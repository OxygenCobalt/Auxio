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
