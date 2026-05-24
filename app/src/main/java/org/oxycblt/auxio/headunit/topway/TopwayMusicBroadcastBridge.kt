package org.oxycblt.auxio.headunit.topway

import android.content.Context
import org.oxycblt.auxio.headunit.compat.HeadUnitMetadataSnapshot
import org.oxycblt.auxio.ui.UISettings

class TopwayMusicBroadcastBridge(private val context: Context, private val uiSettings: UISettings) {
    private var lastMetadata: HeadUnitMetadataSnapshot? = null
    private var lastProgressAtMs = 0L

    fun publishMetadata(snapshot: HeadUnitMetadataSnapshot?) {
        if (!uiSettings.headUnitLandscapeMode || snapshot == null) return
        if (snapshot == lastMetadata) return
        context.sendBroadcast(TopwayMusicIntentFactory.metadataIntent(snapshot))
        lastMetadata = snapshot
    }

    fun publishProgress(progressMs: Long, durationMs: Long, nowMs: Long = System.currentTimeMillis()) {
        if (!uiSettings.headUnitLandscapeMode) return
        if (nowMs - lastProgressAtMs < MIN_PROGRESS_INTERVAL_MS) return
        context.sendBroadcast(TopwayMusicIntentFactory.progressIntent(progressMs, durationMs))
        lastProgressAtMs = nowMs
    }

    fun clear() {
        lastMetadata = null
        context.sendBroadcast(TopwayMusicIntentFactory.progressIntent(0L, 0L))
    }

    companion object { const val MIN_PROGRESS_INTERVAL_MS = 1000L }
}
