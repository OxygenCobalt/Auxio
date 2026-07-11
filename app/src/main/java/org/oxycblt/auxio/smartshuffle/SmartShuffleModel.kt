/*
 * Copyright (c) 2026 Auxio Project
 * SmartShuffleModel.kt is part of Auxio.
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

import org.oxycblt.musikr.Song

/**
 * Offline preference model inspired by Xikipedia's category weights, tuned for music libraries.
 *
 * Songs are scored from learned feature weights (album/artist/genre/decade/duration). Strong
 * negative skip patterns mark tracks as undesirable for review and exclusion from smart queues.
 */
class SmartShuffleModel(
    val featureScores: MutableMap<String, Int> = mutableMapOf(),
    val songStats: MutableMap<String, SongStats> = mutableMapOf(),
    var songsSinceLastStrongLike: Int = 0,
) {
    fun score(song: Song, nowMs: Long = System.currentTimeMillis()): Double {
        val stats = songStats[song.uid.toString()]
        if (stats != null && stats.isExcluded) {
            return UNDESIRABLE_SCORE
        }

        val featureScore =
            song.smartFeatures().sumOf { feature ->
                val raw = (featureScores[feature] ?: 0).toDouble()
                if (feature.startsWith("song:")) raw * SONG_FEATURE_WEIGHT else raw
            }
        val fatigue = -80.0 * effectiveSeen(stats, nowMs).coerceAtMost(MAX_FATIGUE_SEEN)
        val base = if (song.cover != null) 5.0 else 0.0
        return base + featureScore + fatigue
    }

    fun engage(song: Song, points: Int) {
        val uid = song.uid.toString()
        val stats = songStats.getOrPut(uid) { SongStats() }
        stats.engagement += points
        for (feature in song.smartFeatures()) {
            featureScores[feature] = (featureScores[feature] ?: 0) + points
        }
    }

    fun markSeen(song: Song, nowMs: Long = System.currentTimeMillis()) {
        val stats = songStats.getOrPut(song.uid.toString()) { SongStats() }
        decaySeen(stats, nowMs)
        stats.seen += 1
        stats.lastSeenMs = nowMs
    }

    /** Apply time-based seen decay across the library before building a new queue. */
    fun decayAllSeen(nowMs: Long = System.currentTimeMillis()) {
        for (stats in songStats.values) {
            decaySeen(stats, nowMs)
        }
    }

    fun recordEarlySkip(song: Song, globalStreak: Int, nowMs: Long = System.currentTimeMillis()) {
        val points = -5 * globalStreak.coerceIn(1, MAX_SKIP_STREAK_MULTIPLIER)
        engage(song, points)
        val stats = songStats.getOrPut(song.uid.toString()) { SongStats() }
        stats.skips += 1
        stats.skipStreak += 1
        stats.lastSkipMs = nowMs
        songsSinceLastStrongLike += 1
        if (stats.skipStreak >= BACK_TO_BACK_SKIP_THRESHOLD ||
            stats.skips >= TOTAL_SKIP_THRESHOLD) {
            stats.undesirable = true
            stats.forgiven = false
        }
    }

    fun recordMidAbandon(song: Song) {
        engage(song, -2)
        songsSinceLastStrongLike += 1
    }

    fun recordSolidListen(song: Song, nowMs: Long = System.currentTimeMillis()) {
        engage(song, 20)
        onListen(song, nowMs)
        songsSinceLastStrongLike += 1
    }

    fun recordStrongLike(song: Song, nowMs: Long = System.currentTimeMillis()) {
        val bonus = 50 + 4 * songsSinceLastStrongLike
        engage(song, bonus)
        onListen(song, nowMs)
        songsSinceLastStrongLike = 0
    }

    fun recordReplay(song: Song, nowMs: Long = System.currentTimeMillis()) {
        engage(song, 75)
        onListen(song, nowMs)
        songsSinceLastStrongLike = 0
    }

    fun forgive(uid: String) {
        val stats = songStats[uid] ?: return
        stats.undesirable = false
        stats.forgiven = true
        stats.skipStreak = 0
    }

    fun removeSong(uid: String) {
        songStats.remove(uid)
    }

    fun undesirableEntries(): List<Pair<String, SongStats>> =
        songStats
            .filter { it.value.isExcluded }
            .entries
            .map { it.key to it.value }
            .sortedByDescending { it.second.skips }

    fun isExcluded(song: Song): Boolean = songStats[song.uid.toString()]?.isExcluded == true

    private fun onListen(song: Song, nowMs: Long) {
        val stats = songStats.getOrPut(song.uid.toString()) { SongStats() }
        stats.listens += 1
        stats.skipStreak = 0
        stats.lastListenMs = nowMs
    }

    private fun effectiveSeen(stats: SongStats?, nowMs: Long): Int {
        if (stats == null) return 0
        decaySeen(stats, nowMs)
        return stats.seen
    }

    private fun decaySeen(stats: SongStats, nowMs: Long) {
        if (stats.lastSeenMs <= 0L || stats.seen <= 0) return
        val elapsed = (nowMs - stats.lastSeenMs).coerceAtLeast(0L)
        val decaySteps = (elapsed / SEEN_DECAY_MS).toInt()
        if (decaySteps <= 0) return
        stats.seen = (stats.seen - decaySteps).coerceAtLeast(0)
        stats.lastSeenMs += decaySteps * SEEN_DECAY_MS
    }

    private companion object {
        const val SONG_FEATURE_WEIGHT = 0.25
        const val MAX_FATIGUE_SEEN = 5
        const val UNDESIRABLE_SCORE = -1_000_000_000.0
        const val BACK_TO_BACK_SKIP_THRESHOLD = 2
        const val TOTAL_SKIP_THRESHOLD = 3
        const val MAX_SKIP_STREAK_MULTIPLIER = 10
        const val SEEN_DECAY_MS = 12L * 60L * 60L * 1000L
    }
}

data class SongStats(
    var seen: Int = 0,
    var engagement: Int = 0,
    var skips: Int = 0,
    var listens: Int = 0,
    var skipStreak: Int = 0,
    var lastSkipMs: Long = 0L,
    var lastListenMs: Long = 0L,
    var lastSeenMs: Long = 0L,
    var undesirable: Boolean = false,
    var forgiven: Boolean = false,
) {
    val isExcluded: Boolean
        get() = undesirable && !forgiven
}

fun Song.smartFeatures(): List<String> =
    buildList {
        add("song:${uid}")
        add("album:${album.uid}")
        artists.forEach { add("artist:${it.uid}") }
        genres.forEach { add("genre:${it.uid}") }
        date?.year?.let { add("decade:${it / 10 * 10}") }
        add("duration:${durationBucket(durationMs)}")
    }

private fun durationBucket(durationMs: Long): String =
    when {
        durationMs < 120_000 -> "short"
        durationMs < 300_000 -> "normal"
        durationMs < 480_000 -> "long"
        else -> "very_long"
    }
