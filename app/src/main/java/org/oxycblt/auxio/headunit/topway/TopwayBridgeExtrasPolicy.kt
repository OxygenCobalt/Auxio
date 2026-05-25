/*
 * Copyright (c) 2024 Auxio Project
 * TopwayBridgeExtrasPolicy.kt is part of Auxio.
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

data class TopwayBridgeExtras(
    val cmd: String?,
    val widgetProgress: Int?,
)

/** Allowlist-only bridge payload sanitizer for exported Topway receiver intents. */
object TopwayBridgeExtrasPolicy {
    private val ALLOWED_COMMANDS: Set<String> =
        setOf(
            TopwayMusicContract.CMD_PREV,
            TopwayMusicContract.CMD_NEXT,
            TopwayMusicContract.CMD_PLAY_PAUSE,
            TopwayMusicContract.CMD_UPDATE,
        )

    fun sanitizeIncomingExtras(incoming: Map<String, Any?>): TopwayBridgeExtras {
        val cmd =
            (incoming[TopwayMusicContract.EXTRA_CMD] as? String)?.takeIf {
                it.length <= 16 && it in ALLOWED_COMMANDS
            }
        val widgetProgress =
            parseWidgetProgress(incoming[TopwayMusicContract.EXTRA_WIDGET_PROGRESS])
        return TopwayBridgeExtras(cmd = cmd, widgetProgress = widgetProgress)
    }

    private fun parseWidgetProgress(raw: Any?): Int? =
        when (raw) {
            is Int -> raw
            is Long -> raw.takeIf { it in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong() }?.toInt()
            is String -> raw.takeIf { it.length <= 10 }?.toIntOrNull()
            else -> null
        }
}
