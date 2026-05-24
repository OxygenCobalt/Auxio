package org.oxycblt.auxio.headunit.topway

data class TopwayProgressSnapshot(val progressMs: Long, val durationMs: Long)

object TopwayProgressStatePolicy {
    val CLEAR = TopwayProgressSnapshot(0L, 0L)

    fun active(progressMs: Long, durationMs: Long): TopwayProgressSnapshot? {
        if (durationMs <= 0L) return null
        return TopwayProgressSnapshot(progressMs.coerceIn(0L, durationMs), durationMs)
    }

    fun shouldPublish(
        next: TopwayProgressSnapshot,
        last: TopwayProgressSnapshot?,
        nowMs: Long,
        lastAtMs: Long,
        minIntervalMs: Long,
    ): Boolean = next != last || nowMs - lastAtMs >= minIntervalMs
}
