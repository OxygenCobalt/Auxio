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
    fun score(song: Song, nowMs: Long = System.currentTimeMillis()): Double =
        score(song.uid.toString(), song.smartFeatures(), song.cover != null, nowMs)

    fun score(
        uid: String,
        features: List<String>,
        hasCover: Boolean,
        nowMs: Long = System.currentTimeMillis(),
    ): Double {
        val stats = songStats[uid]
        if (stats != null && stats.isExcluded) {
            return UNDESIRABLE_SCORE
        }

        val featureScore =
            features.sumOf { feature ->
                val raw = (featureScores[feature] ?: 0).toDouble()
                if (feature.startsWith("song:")) raw * SONG_FEATURE_WEIGHT else raw
            }
        val fatigue = -80.0 * effectiveSeen(stats, nowMs).coerceAtMost(MAX_FATIGUE_SEEN)
        val base = if (hasCover) 5.0 else 0.0
        return base + featureScore + fatigue
    }

    fun engage(song: Song, points: Int) =
        engage(song.uid.toString(), song.smartFeatures(), points)

    fun engage(uid: String, features: List<String>, points: Int) {
        val stats = songStats.getOrPut(uid) { SongStats() }
        stats.engagement += points
        for (feature in features) {
            val next = ((featureScores[feature] ?: 0) + points).coerceIn(-FEATURE_CAP, FEATURE_CAP)
            if (next == 0) {
                featureScores.remove(feature)
            } else {
                featureScores[feature] = next
            }
        }
    }

    fun markSeen(song: Song, nowMs: Long = System.currentTimeMillis()) =
        markSeen(song.uid.toString(), nowMs)

    fun markSeen(uid: String, nowMs: Long = System.currentTimeMillis()) {
        val stats = songStats.getOrPut(uid) { SongStats() }
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

    /** Remove stale song data and rebuild feature weights from the current library. */
    fun reconcile(songs: Collection<Song>, nowMs: Long = System.currentTimeMillis()) {
        val activeUids = songs.mapTo(HashSet(songs.size)) { it.uid.toString() }
        decayAllSeen(nowMs)
        songStats.keys.retainAll(activeUids)

        val rebuilt = mutableMapOf<String, Long>()
        for (song in songs) {
            val engagement = songStats[song.uid.toString()]?.engagement?.toLong() ?: continue
            if (engagement == 0L) {
                continue
            }
            for (feature in song.smartFeatures()) {
                rebuilt[feature] = (rebuilt[feature] ?: 0L) + engagement
            }
        }
        featureScores.clear()
        for ((feature, score) in rebuilt) {
            val bounded = score.coerceIn(-FEATURE_CAP.toLong(), FEATURE_CAP.toLong()).toInt()
            if (bounded != 0) {
                featureScores[feature] = bounded
            }
        }
    }

    fun recordEarlySkip(
        song: Song,
        globalStreak: Int,
        nowMs: Long = System.currentTimeMillis(),
    ) = recordEarlySkip(song.uid.toString(), song.smartFeatures(), globalStreak, nowMs)

    fun recordEarlySkip(
        uid: String,
        features: List<String>,
        globalStreak: Int,
        nowMs: Long = System.currentTimeMillis(),
    ) {
        val points = -5 * globalStreak.coerceIn(1, MAX_SKIP_STREAK_MULTIPLIER)
        engage(uid, features, points)
        val stats = songStats.getOrPut(uid) { SongStats() }
        stats.skips += 1
        stats.skipStreak += 1
        stats.lastSkipMs = nowMs
        songsSinceLastStrongLike += 1
        // Require sustained dislike: several consecutive early skips of the *same* song,
        // or a higher lifetime skip count. Rapid library browsing alone should not ban tracks.
        if (stats.skipStreak >= BACK_TO_BACK_SKIP_THRESHOLD ||
            stats.skips >= TOTAL_SKIP_THRESHOLD) {
            stats.undesirable = true
            stats.forgiven = false
        }
    }

    fun recordMidAbandon(song: Song) =
        recordMidAbandon(song.uid.toString(), song.smartFeatures())

    fun recordMidAbandon(uid: String, features: List<String>) {
        engage(uid, features, -2)
        songsSinceLastStrongLike += 1
    }

    fun recordSolidListen(song: Song, nowMs: Long = System.currentTimeMillis()) =
        recordSolidListen(song.uid.toString(), song.smartFeatures(), nowMs)

    fun recordSolidListen(
        uid: String,
        features: List<String>,
        nowMs: Long = System.currentTimeMillis(),
    ) {
        engage(uid, features, 20)
        onListen(uid, nowMs)
        songsSinceLastStrongLike += 1
    }

    /**
     * Implicit strong listen (e.g. played ≥85%). Boosts recommendations but does not fill the
     * heart — only [recordExplicitLike] does that.
     */
    fun recordStrongLike(song: Song, nowMs: Long = System.currentTimeMillis()) =
        recordStrongLike(song.uid.toString(), song.smartFeatures(), nowMs)

    fun recordStrongLike(
        uid: String,
        features: List<String>,
        nowMs: Long = System.currentTimeMillis(),
    ) {
        applyStrongLikeBoost(uid, features, nowMs)
    }

    /** Explicit heart tap: same boost as a strong listen, plus marks the song as liked. */
    fun recordExplicitLike(song: Song, nowMs: Long = System.currentTimeMillis()) =
        recordExplicitLike(song.uid.toString(), song.smartFeatures(), nowMs)

    fun recordExplicitLike(
        uid: String,
        features: List<String>,
        nowMs: Long = System.currentTimeMillis(),
    ) {
        applyStrongLikeBoost(uid, features, nowMs)
        songStats.getOrPut(uid) { SongStats() }.liked = true
    }

    fun isLiked(uid: String): Boolean = songStats[uid]?.liked == true

    fun isLiked(song: Song): Boolean = isLiked(song.uid.toString())

    /**
     * Explicit dislike: strong negative engagement, clear any like, and mark the song undesirable
     * so it is excluded from smart queues and listed under Songs to review. Skip counters are left
     * to the tracker for implicit early skips only.
     */
    fun recordExplicitDislike(song: Song) =
        recordExplicitDislike(song.uid.toString(), song.smartFeatures())

    fun recordExplicitDislike(uid: String, features: List<String>) {
        engage(uid, features, EXPLICIT_DISLIKE_POINTS)
        val stats = songStats.getOrPut(uid) { SongStats() }
        stats.liked = false
        stats.undesirable = true
        stats.forgiven = false
        songsSinceLastStrongLike += 1
    }

    private fun applyStrongLikeBoost(uid: String, features: List<String>, nowMs: Long) {
        val bonus = 50 + 4 * songsSinceLastStrongLike
        engage(uid, features, bonus)
        onListen(uid, nowMs)
        songsSinceLastStrongLike = 0
        val stats = songStats.getOrPut(uid) { SongStats() }
        stats.undesirable = false
        stats.forgiven = true
        stats.skipStreak = 0
    }

    fun isDisliked(uid: String): Boolean = songStats[uid]?.isExcluded == true

    fun isDisliked(song: Song): Boolean = isDisliked(song.uid.toString())

    fun recordReplay(song: Song, nowMs: Long = System.currentTimeMillis()) =
        recordReplay(song.uid.toString(), song.smartFeatures(), nowMs)

    fun recordReplay(
        uid: String,
        features: List<String>,
        nowMs: Long = System.currentTimeMillis(),
    ) {
        engage(uid, features, 75)
        onListen(uid, nowMs)
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

    fun isExcluded(song: Song): Boolean = isExcluded(song.uid.toString())

    fun isExcluded(uid: String): Boolean = songStats[uid]?.isExcluded == true

    private fun onListen(uid: String, nowMs: Long) {
        val stats = songStats.getOrPut(uid) { SongStats() }
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

    internal companion object {
        const val SONG_FEATURE_WEIGHT = 0.25
        const val MAX_FATIGUE_SEEN = 5
        const val UNDESIRABLE_SCORE = -1_000_000_000.0
        /** Consecutive early skips of the same song without a solid listen. */
        const val BACK_TO_BACK_SKIP_THRESHOLD = 4
        /** Lifetime early skips before auto-exclude. */
        const val TOTAL_SKIP_THRESHOLD = 6
        const val MAX_SKIP_STREAK_MULTIPLIER = 10
        const val SEEN_DECAY_MS = 12L * 60L * 60L * 1000L
        const val FEATURE_CAP = 5_000
        /** Strong negative engagement applied on an explicit dislike. */
        const val EXPLICIT_DISLIKE_POINTS = -60
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
    var liked: Boolean = false,
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
