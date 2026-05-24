package org.oxycblt.auxio.widgets

object WidgetTimeline {
    fun clampProgressSeconds(positionMs: Long, durationMs: Long): Pair<Int, Int> {
        val safeDuration = durationMs.coerceAtLeast(0L)
        val safePosition = positionMs.coerceIn(0L, safeDuration)
        return (safeDuration / 1000L).toInt() to (safePosition / 1000L).toInt()
    }

    fun formatClock(ms: Long): String {
        val total = (ms.coerceAtLeast(0L) / 1000L).toInt()
        val s = total % 60
        val m = (total / 60) % 60
        val h = total / 3600
        return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
    }
}
