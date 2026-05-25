/*
 * Copyright (c) 2024 Auxio Project
 * TopwayMusicBroadcastBridge.kt is part of Auxio.
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

package org.oxycblt.auxio.headunit.topway

import android.content.Context
import android.os.SystemClock
import org.oxycblt.auxio.headunit.compat.HeadUnitMetadataSnapshot
import org.oxycblt.auxio.ui.UISettings

class TopwayMusicBroadcastBridge(private val context: Context, private val uiSettings: UISettings) {
    private var lastMetadata: HeadUnitMetadataSnapshot? = null
    private var lastProgress: TopwayProgressSnapshot? = null
    private var lastProgressAtMs = 0L

    fun publishMetadata(snapshot: HeadUnitMetadataSnapshot?) {
        if (!uiSettings.headUnitLandscapeMode || snapshot == null) return
        if (snapshot == lastMetadata) return
        context.sendBroadcast(TopwayMusicIntentFactory.metadataIntent(snapshot))
        lastMetadata = snapshot
    }

    fun publishProgress(
        progressMs: Long,
        durationMs: Long,
        nowMs: Long = SystemClock.elapsedRealtime(),
    ) {
        if (!uiSettings.headUnitLandscapeMode) return
        val snapshot = TopwayProgressStatePolicy.active(progressMs, durationMs) ?: return
        if (
            !TopwayProgressStatePolicy.shouldPublish(
                snapshot,
                lastProgress,
                nowMs,
                lastProgressAtMs,
                MIN_PROGRESS_INTERVAL_MS,
            )
        ) {
            return
        }
        context.sendBroadcast(
            TopwayMusicIntentFactory.progressIntent(snapshot.progressMs, snapshot.durationMs)
        )
        lastProgress = snapshot
        lastProgressAtMs = nowMs
    }

    fun clear() {
        lastMetadata = null
        if (lastProgress != TopwayProgressStatePolicy.CLEAR) {
            context.sendBroadcast(TopwayMusicIntentFactory.progressIntent(0L, 0L))
            lastProgress = TopwayProgressStatePolicy.CLEAR
            lastProgressAtMs = SystemClock.elapsedRealtime()
        }
    }

    companion object {
        const val MIN_PROGRESS_INTERVAL_MS = 1000L
    }
}
