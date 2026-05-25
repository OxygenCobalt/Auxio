/*
 * Copyright (c) 2024 Auxio Project
 * TopwayStartRoutingPolicy.kt is part of Auxio.
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

enum class TopwayServiceAction {
    PREVIOUS,
    NEXT,
    PLAY_PAUSE,
    WIDGET_UPDATE,
    SEEK,
    IGNORE
}

data class TopwayServiceDecision(
    val action: TopwayServiceAction,
    val seekTargetMs: Long? = null,
)

/** Pure routing policy used by both the cold manifest bridge and active playback receiver. */
object TopwayStartRoutingPolicy {
    fun decide(
        action: String?,
        cmd: String?,
        rawSeek: Any?,
        durationMs: Long?,
        hasCurrentSong: Boolean,
    ): TopwayServiceDecision {
        if (action == TopwayMusicContract.ACTION_LAUNCHER_WIDGET_SEEK) {
            val target = TopwayMusicSeekMapper.mapSeekTargetMs(rawSeek, durationMs)
            return if (hasCurrentSong && target != null) {
                TopwayServiceDecision(TopwayServiceAction.SEEK, target)
            } else {
                TopwayServiceDecision(TopwayServiceAction.IGNORE)
            }
        }

        return when (TopwayMusicCommandMapper.map(action, cmd)) {
            TopwayMappedCommand.PREV ->
                if (hasCurrentSong) TopwayServiceDecision(TopwayServiceAction.PREVIOUS)
                else TopwayServiceDecision(TopwayServiceAction.IGNORE)
            TopwayMappedCommand.NEXT ->
                if (hasCurrentSong) TopwayServiceDecision(TopwayServiceAction.NEXT)
                else TopwayServiceDecision(TopwayServiceAction.IGNORE)
            TopwayMappedCommand.PLAY_PAUSE ->
                if (hasCurrentSong) TopwayServiceDecision(TopwayServiceAction.PLAY_PAUSE)
                else TopwayServiceDecision(TopwayServiceAction.IGNORE)
            TopwayMappedCommand.UPDATE -> TopwayServiceDecision(TopwayServiceAction.WIDGET_UPDATE)
            TopwayMappedCommand.UNKNOWN -> TopwayServiceDecision(TopwayServiceAction.IGNORE)
        }
    }
}
