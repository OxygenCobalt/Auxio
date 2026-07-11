/*
 * Copyright (c) 2026 Auxio Project
 * SmartShuffle.kt is part of Auxio.
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

package org.oxycblt.auxio.smartshuffle

import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.musikr.Song
import timber.log.Timber as L

@Singleton
class SmartShuffle @Inject constructor(private val store: SmartShuffleStore) {
    private val random = Random.Default
    private val model = store.load()
    private val saveExecutor =
        Executors.newSingleThreadScheduledExecutor { runnable ->
            Thread(runnable, "smart-shuffle-save").apply { isDaemon = true }
        }
    private val scheduleLock = Any()
    private var pendingSave: Future<*>? = null
    private var saveGeneration = 0L

    private val _likesRevision = MutableStateFlow(0)
    /** Bumps whenever an explicit/strong like is recorded so UI can refresh. */
    val likesRevision: StateFlow<Int>
        get() = _likesRevision

    /**
     * Build a learned playback order.
     *
     * Complexity: O(n log n) sort + O(prefix * window) picks for the head of the queue, then an
     * O(n) append of the remaining score-ordered tail. Avoids full-library O(n²) selection.
     */
    @Synchronized
    fun queue(songs: Collection<Song>, random: Random = this.random): List<Song> {
        model.reconcile(songs)
        scheduleSave()

        val eligible = songs.filterNot { model.isExcluded(it) }
        // If everything is banned, fall back so playback can still start.
        val source = if (eligible.isNotEmpty()) eligible else songs.toList()
        if (source.isEmpty()) {
            return emptyList()
        }

        val ranked =
            source
                .map { it to model.score(it) }
                .sortedByDescending { it.second }

        val prefixLen = minOf(ranked.size, SMART_PREFIX_LEN)
        val prefixPool = ranked.take(minOf(ranked.size, CANDIDATE_POOL))
        val prefix =
            buildPrefix(
                songs = prefixPool.map { it.first },
                scores = prefixPool.associate { it.first to it.second },
                count = prefixLen,
                random = random,
            )
        val prefixSet = prefix.toHashSet()
        val tail =
            ranked
                .asSequence()
                .map { it.first }
                .filterNot { it in prefixSet }
                .toMutableList()
        // Light exploration in the long tail without another O(n²) pass.
        if (tail.size > 1) {
            val swaps = (tail.size * TAIL_SWAP_FRACTION).toInt().coerceAtLeast(0)
            repeat(swaps) {
                val a = random.nextInt(tail.size)
                val b = random.nextInt(tail.size)
                val tmp = tail[a]
                tail[a] = tail[b]
                tail[b] = tmp
            }
        }
        return prefix + tail
    }

    @Synchronized
    fun markSeen(song: Song) {
        model.markSeen(song)
        scheduleSave()
    }

    @Synchronized
    fun recordEarlySkip(song: Song, globalStreak: Int) {
        model.recordEarlySkip(song, globalStreak)
        // Exclusion changes are important — flush soon.
        if (model.isExcluded(song)) {
            flushAsync()
        } else {
            scheduleSave()
        }
    }

    @Synchronized
    fun recordMidAbandon(song: Song) {
        model.recordMidAbandon(song)
        scheduleSave()
    }

    @Synchronized
    fun recordSolidListen(song: Song) {
        model.recordSolidListen(song)
        scheduleSave()
    }

    @Synchronized
    fun recordStrongLike(song: Song) {
        model.recordStrongLike(song)
        flushAsync()
        _likesRevision.value = _likesRevision.value + 1
    }

    @Synchronized
    fun like(song: Song) {
        model.recordStrongLike(song)
        flushAsync()
        _likesRevision.value = _likesRevision.value + 1
    }

    @Synchronized
    fun isLiked(song: Song): Boolean = model.isLiked(song)

    @Synchronized
    fun isLiked(uid: String): Boolean = model.isLiked(uid)

    @Synchronized
    fun recordReplay(song: Song) {
        model.recordReplay(song)
        scheduleSave()
    }

    @Synchronized
    fun forgive(uid: String) {
        model.forgive(uid)
        flushAsync()
    }

    @Synchronized
    fun removeSong(uid: String) {
        model.removeSong(uid)
        flushAsync()
    }

    @Synchronized
    fun undesirableCount(): Int = model.undesirableEntries().size

    @Synchronized
    fun undesirableEntries(): List<Pair<String, SongStats>> = model.undesirableEntries()

    /** Synchronously persist; use on service teardown. */
    fun flush() {
        while (true) {
            val (generation, future) =
                synchronized(scheduleLock) {
                    val generation = ++saveGeneration
                    pendingSave?.cancel(false)
                    pendingSave = null
                    generation to saveExecutor.submit { persist(generation) }
                }
            try {
                future.get()
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                L.w(e, "Interrupted while flushing smart shuffle model")
                return
            } catch (e: Exception) {
                L.w(e, "Unable to flush smart shuffle model")
                return
            }
            if (synchronized(scheduleLock) { generation == saveGeneration }) {
                return
            }
        }
    }

    private fun flushAsync() {
        scheduleSave(0L)
    }

    private fun scheduleSave(delayMs: Long = SAVE_DEBOUNCE_MS) {
        synchronized(scheduleLock) {
            val generation = ++saveGeneration
            pendingSave?.cancel(false)
            pendingSave =
                saveExecutor.schedule(
                    { persist(generation) },
                    delayMs,
                    TimeUnit.MILLISECONDS,
                )
        }
    }

    private fun persist(generation: Long) {
        val snapshot = snapshotModel()
        try {
            store.save(snapshot)
        } catch (e: Exception) {
            L.w(e, "Unable to save smart shuffle model")
        } finally {
            synchronized(scheduleLock) {
                if (generation == saveGeneration) {
                    pendingSave = null
                }
            }
        }
    }

    @Synchronized
    private fun snapshotModel(): SmartShuffleModel {
        return SmartShuffleModel(
            featureScores = model.featureScores.toMutableMap(),
            songStats = model.songStats.mapValues { (_, stats) -> stats.copy() }.toMutableMap(),
            songsSinceLastStrongLike = model.songsSinceLastStrongLike,
        )
    }

    /**
     * Xikipedia-style 42/40/18 selection over a mutable pool using swap-remove (O(1) removal) and a
     * bounded window for max/weighted picks.
     */
    private fun buildPrefix(
        songs: List<Song>,
        scores: Map<Song, Double>,
        count: Int,
        random: Random,
    ): List<Song> {
        if (songs.isEmpty() || count <= 0) return emptyList()
        val arr = songs.toTypedArray()
        val sc = DoubleArray(arr.size) { scores[arr[it]] ?: 0.0 }
        var n = arr.size
        val out = ArrayList<Song>(minOf(count, n))

        while (out.size < count && n > 0) {
            val idx =
                when (random.nextInt(100)) {
                    in 0 until 42 -> maxIndex(sc, n)
                    in 42 until 82 -> weightedIndex(sc, n, random)
                    else -> random.nextInt(n)
                }
            out.add(removeAt(arr, sc, idx, n - 1))
            n--
        }
        return out
    }

    private fun maxIndex(sc: DoubleArray, n: Int): Int {
        var best = 0
        var bestScore = sc[0]
        for (i in 1 until n) {
            if (sc[i] > bestScore) {
                best = i
                bestScore = sc[i]
            }
        }
        return best
    }

    private fun weightedIndex(sc: DoubleArray, n: Int, random: Random): Int {
        val window = minOf(n, PICK_WINDOW)
        if (window == n) {
            var total = 0.0
            for (i in 0 until n) total += sc[i].coerceAtLeast(0.0) + 1.0
            var cursor = random.nextDouble(total)
            for (i in 0 until n) {
                cursor -= sc[i].coerceAtLeast(0.0) + 1.0
                if (cursor <= 0.0) return i
            }
            return n - 1
        }
        // Sample a window of live indices so weighted pick stays O(window).
        val sample = IntArray(window)
        val chosen = HashSet<Int>(window)
        var filled = 0
        while (filled < window) {
            val i = random.nextInt(n)
            if (chosen.add(i)) {
                sample[filled++] = i
            }
        }
        var total = 0.0
        for (i in 0 until window) total += sc[sample[i]].coerceAtLeast(0.0) + 1.0
        var cursor = random.nextDouble(total)
        for (i in 0 until window) {
            cursor -= sc[sample[i]].coerceAtLeast(0.0) + 1.0
            if (cursor <= 0.0) return sample[i]
        }
        return sample[window - 1]
    }

    private fun removeAt(arr: Array<Song>, sc: DoubleArray, idx: Int, last: Int): Song {
        val song = arr[idx]
        arr[idx] = arr[last]
        sc[idx] = sc[last]
        return song
    }

    private companion object {
        const val CANDIDATE_POOL = 3000
        const val SMART_PREFIX_LEN = 512
        const val PICK_WINDOW = 256
        const val TAIL_SWAP_FRACTION = 0.08
        const val SAVE_DEBOUNCE_MS = 750L
    }
}
