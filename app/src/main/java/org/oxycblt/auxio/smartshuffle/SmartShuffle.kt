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
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random
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
    private var pendingSave: ScheduledFuture<*>? = null

    @Synchronized
    fun queue(songs: Collection<Song>): List<Song> {
        model.decayAllSeen()
        val eligible = songs.filterNot { model.isExcluded(it) }
        // If everything is banned, fall back to the full library so playback still starts.
        val remaining = (if (eligible.isNotEmpty()) eligible else songs.toList()).toMutableList()
        if (remaining.isEmpty()) {
            return emptyList()
        }

        // Score once up front — avoids O(n²) feature lookups while building the queue.
        val scores = HashMap<Song, Double>(remaining.size)
        for (song in remaining) {
            scores[song] = model.score(song)
        }

        val queue = ArrayList<Song>(remaining.size)
        while (remaining.isNotEmpty()) {
            val pool =
                if (remaining.size > CANDIDATE_POOL) {
                    remaining.shuffled(random).take(CANDIDATE_POOL)
                } else {
                    remaining
                }
            val next =
                when (random.nextInt(100)) {
                    in 0 until 42 -> pool.maxBy { scores[it] ?: Double.NEGATIVE_INFINITY }
                    in 42 until 82 -> weightedPick(pool, scores)
                    else -> pool.random(random)
                }
            remaining.remove(next)
            queue.add(next)
        }
        return queue
    }

    @Synchronized
    fun markSeen(song: Song) {
        model.markSeen(song)
        scheduleSave()
    }

    @Synchronized
    fun recordEarlySkip(song: Song, globalStreak: Int) {
        model.recordEarlySkip(song, globalStreak)
        scheduleSave()
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
        scheduleSave()
    }

    @Synchronized
    fun recordReplay(song: Song) {
        model.recordReplay(song)
        scheduleSave()
    }

    @Synchronized
    fun forgive(uid: String) {
        model.forgive(uid)
        scheduleSave()
    }

    @Synchronized
    fun removeSong(uid: String) {
        model.removeSong(uid)
        scheduleSave()
    }

    @Synchronized
    fun undesirableCount(): Int = model.undesirableEntries().size

    @Synchronized
    fun undesirableEntries(): List<Pair<String, SongStats>> = model.undesirableEntries()

    fun flush() {
        synchronized(scheduleLock) {
            pendingSave?.cancel(false)
            pendingSave = null
        }
        val snapshot = snapshotModel()
        try {
            store.save(snapshot)
        } catch (e: Exception) {
            L.w(e, "Unable to flush smart shuffle model")
        }
    }

    private fun scheduleSave() {
        synchronized(scheduleLock) {
            pendingSave?.cancel(false)
            pendingSave =
                saveExecutor.schedule(
                    {
                        try {
                            store.save(snapshotModel())
                        } catch (e: Exception) {
                            L.w(e, "Unable to save smart shuffle model")
                        }
                    },
                    SAVE_DEBOUNCE_MS,
                    TimeUnit.MILLISECONDS,
                )
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

    private fun weightedPick(songs: List<Song>, scores: Map<Song, Double>): Song {
        val weighted = songs.map { it to ((scores[it] ?: 0.0).coerceAtLeast(0.0) + 1.0) }
        val total = weighted.sumOf { it.second }
        var cursor = random.nextDouble(total)
        for ((song, weight) in weighted) {
            cursor -= weight
            if (cursor <= 0.0) {
                return song
            }
        }
        return songs.last()
    }

    private companion object {
        const val CANDIDATE_POOL = 3000
        const val SAVE_DEBOUNCE_MS = 750L
    }
}
