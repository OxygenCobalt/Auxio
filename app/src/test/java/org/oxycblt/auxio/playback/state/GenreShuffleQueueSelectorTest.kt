package org.oxycblt.auxio.playback.state

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GenreShuffleQueueSelectorTest {
    data class FakeSong(val id: String, val genres: Set<String>)

    @Test
    fun select_includes_union_matches_excludes_unrelated_and_keeps_current_once() {
        val current = FakeSong("current", setOf("Pop", "Rock", "Pop-punk", "Emo"))
        val songs =
            listOf(
                current,
                FakeSong("pop-only", setOf("Pop")),
                FakeSong("rock-only", setOf("Rock")),
                FakeSong("pop-rock", setOf("Pop", "Rock")),
                FakeSong("emo-only", setOf("Emo")),
                FakeSong("jazz", setOf("Jazz")),
                FakeSong("unknown", emptySet()),
                FakeSong("duplicate-pop-rock", setOf("Pop", "Rock")),
            )

        val selection =
            GenreShuffleQueueSelector.select(
                current = current,
                allSongs = songs,
                currentGenres = current.genres,
                songGenres = { it.genres },
                songId = { if (it.id == "duplicate-pop-rock") "pop-rock" else it.id },
                random = Random(1234),
            )

        assertEquals(0, selection.currentIndex)
        assertEquals("current", selection.queue.first().id)

        val ids = selection.queue.map { it.id }
        assertFalse(ids.contains("jazz"))
        assertFalse(ids.contains("unknown"))
        assertEquals(1, ids.count { it == "current" })
        assertEquals(1, ids.count { it == "pop-rock" || it == "duplicate-pop-rock" })

        assertTrue(ids.contains("pop-only"))
        assertTrue(ids.contains("rock-only"))
        assertTrue(ids.contains("emo-only"))
    }

    @Test
    fun select_without_matching_genres_keeps_only_current() {
        val current = FakeSong("current", setOf("Pop"))
        val songs = listOf(FakeSong("jazz", setOf("Jazz")), FakeSong("unknown", emptySet()))

        val selection =
            GenreShuffleQueueSelector.select(
                current = current,
                allSongs = songs,
                currentGenres = current.genres,
                songGenres = { it.genres },
                songId = { it.id },
                random = Random(99),
            )

        assertEquals(listOf("current"), selection.queue.map { it.id })
        assertEquals(0, selection.currentIndex)
    }

    @Test
    fun select_is_deterministic_for_seeded_random() {
        val current = FakeSong("current", setOf("Pop", "Rock"))
        val songs =
            listOf(
                current,
                FakeSong("a", setOf("Pop")),
                FakeSong("b", setOf("Rock")),
                FakeSong("c", setOf("Pop", "Rock")),
            )

        val first =
            GenreShuffleQueueSelector.select(
                current = current,
                allSongs = songs,
                currentGenres = current.genres,
                songGenres = { it.genres },
                songId = { it.id },
                random = Random(7),
            )
        val second =
            GenreShuffleQueueSelector.select(
                current = current,
                allSongs = songs,
                currentGenres = current.genres,
                songGenres = { it.genres },
                songId = { it.id },
                random = Random(7),
            )

        assertEquals(first.queue.map { it.id }, second.queue.map { it.id })
    }
}
