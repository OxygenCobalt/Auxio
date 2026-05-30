/*
 * Copyright (c) 2024 Auxio Project
 * TopwayMusicSeekMapper.kt is part of Auxio.
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
