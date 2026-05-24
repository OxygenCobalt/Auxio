package org.oxycblt.auxio.headunit.topway

object TopwayMusicSeekMapper {
    fun mapSeekTargetMs(rawProgress: Int?, durationMs: Long?): Long? {
        if (rawProgress == null || durationMs == null || durationMs <= 0L) return null
        return rawProgress.toLong().coerceIn(0L, durationMs)
    }
}
