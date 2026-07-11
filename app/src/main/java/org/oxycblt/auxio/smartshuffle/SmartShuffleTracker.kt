/*
 * Copyright (c) 2026 Auxio Project
 * SmartShuffleTracker.kt is part of Auxio.
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

import android.os.SystemClock
import javax.inject.Inject
import javax.inject.Singleton
import org.oxycblt.auxio.playback.state.PlaybackStateManager
import org.oxycblt.auxio.playback.state.Progression
import org.oxycblt.auxio.playback.state.QueueChange
import org.oxycblt.musikr.MusicParent
import org.oxycblt.musikr.Song

@Singleton
class SmartShuffleTracker
@Inject
constructor(
    private val playbackManager: PlaybackStateManager,
    private val smartShuffle: SmartShuffle,
) : PlaybackStateManager.Listener {
    private var current: TrackedSong? = null
    private var isPlaying = false
    private var started = false
    private var globalSkipStreak = 0
    private var suppressFinishUid: String? = null
    private val sessionListens = mutableSetOf<String>()

    fun start() {
        if (started) {
            return
        }
        started = true
        playbackManager.addListener(this)
    }

    fun stop() {
        if (!started) {
            return
        }
        started = false
        finishCurrent()
        current = null
        suppressFinishUid = null
        playbackManager.removeListener(this)
        smartShuffle.flush()
    }

    /**
     * Skip the next [finishCurrent] for [song] (e.g. after an explicit dislike that already
     * recorded preference and is about to call [PlaybackStateManager.next]).
     */
    fun suppressFinish(song: Song) {
        suppressFinishUid = song.uid.toString()
    }

    override fun onNewPlayback(
        parent: MusicParent?,
        queue: List<Song>,
        index: Int,
        isShuffled: Boolean,
    ) {
        finishCurrent()
        track(playbackManager.currentSong)
    }

    override fun onQueueChanged(queue: List<Song>, index: Int, change: QueueChange) {
        if (change.type == QueueChange.Type.SONG) {
            finishCurrent()
            track(playbackManager.currentSong)
        }
    }

    override fun onIndexMoved(index: Int) {
        finishCurrent()
        track(playbackManager.currentSong)
    }

    override fun onProgressionChanged(progression: Progression) {
        current?.updatePlaying(progression.isPlaying)
        isPlaying = progression.isPlaying
    }

    override fun onSessionEnded() {
        finishCurrent()
        current = null
        sessionListens.clear()
        globalSkipStreak = 0
        suppressFinishUid = null
        smartShuffle.flush()
    }

    private fun track(song: Song?) {
        val playing = isPlaying || playbackManager.progression.isPlaying
        current =
            song?.let {
                smartShuffle.markSeen(it)
                TrackedSong(it, if (playing) SystemClock.elapsedRealtime() else null)
            }
    }

    private fun finishCurrent() {
        val tracked = current ?: return
        current = null
        val song = tracked.song
        val uidKey = song.uid.toString()
        if (suppressFinishUid == uidKey) {
            suppressFinishUid = null
            return
        }

        val listenedMs = tracked.playedMs()
        val durationMs = song.durationMs.coerceAtLeast(1)

        val strongThreshold = minOf(90_000L, (durationMs * 0.85).toLong())
        val solidThreshold = minOf(45_000L, (durationMs * 0.50).toLong())
        val earlySkipThreshold = minOf(20_000L, (durationMs * 0.25).toLong())

        when {
            listenedMs >= strongThreshold -> {
                globalSkipStreak = 0
                if (uidKey in sessionListens) {
                    smartShuffle.recordReplay(song)
                } else {
                    smartShuffle.recordStrongLike(song)
                    sessionListens.add(uidKey)
                }
            }
            listenedMs >= solidThreshold -> {
                globalSkipStreak = 0
                if (uidKey in sessionListens) {
                    smartShuffle.recordReplay(song)
                } else {
                    smartShuffle.recordSolidListen(song)
                    sessionListens.add(uidKey)
                }
            }
            listenedMs <= earlySkipThreshold -> {
                // Per-song skipStreak (no solid listen between) marks undesirable at 4
                // consecutive early skips, or 6 lifetime early skips.
                // Global streak escalates the point penalty while the user is in skip mode.
                globalSkipStreak = (globalSkipStreak + 1).coerceAtMost(10)
                smartShuffle.recordEarlySkip(song, globalStreak = globalSkipStreak)
            }
            else -> {
                // Mid abandon: listened a bit, then moved on.
                globalSkipStreak = 0
                smartShuffle.recordMidAbandon(song)
            }
        }
    }

    private class TrackedSong(val song: Song, private var lastStartedMs: Long?) {
        private var accumulatedMs = 0L

        fun updatePlaying(playing: Boolean) {
            val now = SystemClock.elapsedRealtime()
            val startedMs = lastStartedMs
            if (playing && startedMs == null) {
                lastStartedMs = now
            } else if (!playing && startedMs != null) {
                accumulatedMs += now - startedMs
                lastStartedMs = null
            }
        }

        fun playedMs(): Long {
            val startedMs = lastStartedMs ?: return accumulatedMs
            return accumulatedMs + (SystemClock.elapsedRealtime() - startedMs)
        }
    }
}
