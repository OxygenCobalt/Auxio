/*
 * Copyright (c) 2024 Auxio Project
 * WidgetTimeline.kt is part of Auxio.
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

package org.oxycblt.auxio.widgets

data class WidgetTimelineState(
    val currentText: String,
    val durationText: String,
    val maxSeconds: Int,
    val progressSeconds: Int,
)

object WidgetTimeline {
    val NO_SESSION = WidgetTimelineState("0:00", "0:00", 1, 0)

    fun state(positionMs: Long, durationMs: Long): WidgetTimelineState {
        val (durationSeconds, positionSeconds) = clampProgressSeconds(positionMs, durationMs)
        return WidgetTimelineState(
            currentText = formatClock(positionSeconds * 1000L),
            durationText = formatClock(durationMs),
            maxSeconds = durationSeconds.coerceAtLeast(1),
            progressSeconds = positionSeconds,
        )
    }

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
