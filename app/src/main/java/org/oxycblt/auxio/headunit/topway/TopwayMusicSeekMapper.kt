package org.oxycblt.auxio.headunit.topway

object TopwayMusicSeekMapper {
    fun mapSeekTargetMs(rawProgress: Any?, durationMs: Long?): Long? {
        if (durationMs == null || durationMs <= 0L) return null
        val progress =
            when (rawProgress) {
                is Int -> rawProgress.toLong()
                is Long -> rawProgress
                is Short -> rawProgress.toLong()
                is String -> rawProgress.toLongOrNull()
                else -> null
            } ?: return null
        return progress.coerceIn(0L, durationMs)
    }
}
