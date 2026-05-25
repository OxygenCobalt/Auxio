/*
 * Copyright (c) 2024 Auxio Project
 * TopwayMusicCommandMapper.kt is part of Auxio.
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

enum class TopwayMappedCommand {
    PREV,
    NEXT,
    PLAY_PAUSE,
    UPDATE,
    UNKNOWN
}

object TopwayMusicCommandMapper {
    fun map(action: String?, cmd: String?): TopwayMappedCommand =
        when (action) {
            TopwayMusicContract.ACTION_PREV -> TopwayMappedCommand.PREV
            TopwayMusicContract.ACTION_NEXT -> TopwayMappedCommand.NEXT
            TopwayMusicContract.ACTION_PLAY_PAUSE -> TopwayMappedCommand.PLAY_PAUSE
            TopwayMusicContract.ACTION_CMD ->
                when (cmd) {
                    TopwayMusicContract.CMD_PREV -> TopwayMappedCommand.PREV
                    TopwayMusicContract.CMD_NEXT -> TopwayMappedCommand.NEXT
                    TopwayMusicContract.CMD_PLAY_PAUSE -> TopwayMappedCommand.PLAY_PAUSE
                    TopwayMusicContract.CMD_UPDATE -> TopwayMappedCommand.UPDATE
                    else -> TopwayMappedCommand.UNKNOWN
                }
            else -> TopwayMappedCommand.UNKNOWN
        }
}
