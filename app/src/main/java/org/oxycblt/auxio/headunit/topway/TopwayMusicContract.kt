/*
 * Copyright (c) 2024 Auxio Project
 * TopwayMusicContract.kt is part of Auxio.
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

/** Contract constants sourced from observed Topway com.tw.music decompile behavior. */
object TopwayMusicContract {
    const val ACTION_MUSIC_INFO = "com.tw.music.info"
    const val EXTRA_MUSIC_TITLE = "musicTitle"
    const val EXTRA_MUSIC_ARTIST = "musicaArtist"
    const val EXTRA_MUSIC_ALBUM = "musicAlbum"
    const val EXTRA_MUSIC_PATH = "musicPath"

    const val ACTION_PROGRESS_DURATION = "com.tw.launcher.music_progress_duration"
    const val EXTRA_PROGRESS = "msg_music_progress"
    const val EXTRA_DURATION = "msg_music_duration"

    const val ACTION_LAUNCHER_WIDGET_SEEK = "com.android.launcher.widget_music_progress"
    const val EXTRA_WIDGET_PROGRESS = "music_progress"

    const val ACTION_CMD = "com.tw.music.action.cmd"
    const val ACTION_PREV = "com.tw.music.action.prev"
    const val ACTION_NEXT = "com.tw.music.action.next"
    const val ACTION_PLAY_PAUSE = "com.tw.music.action.pp"

    const val EXTRA_CMD = "cmd"
    const val CMD_PREV = "prev"
    const val CMD_NEXT = "next"
    const val CMD_PLAY_PAUSE = "pp"
    const val CMD_UPDATE = "update"

    val INCOMING_ACTIONS: Set<String> =
        setOf(ACTION_CMD, ACTION_PREV, ACTION_NEXT, ACTION_PLAY_PAUSE, ACTION_LAUNCHER_WIDGET_SEEK)

    fun isIncomingAction(action: String?): Boolean = action != null && action in INCOMING_ACTIONS
}
