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
        context.sendBroadcast(TopwayMusicIntentFactory.progressIntent(snapshot.progressMs, snapshot.durationMs))
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

    companion object { const val MIN_PROGRESS_INTERVAL_MS = 1000L }
}
