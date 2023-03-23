/*
 * Copyright (c) 2023 Auxio Project
 * PlaylistImpl.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.user

import android.content.Context
import java.util.*
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.device.DeviceLibrary

class PlaylistImpl
private constructor(
    override val uid: Music.UID,
    override val rawName: String,
    override val songs: List<Song>,
    musicSettings: MusicSettings
) : Playlist {
    constructor(
        name: String,
        songs: List<Song>,
        musicSettings: MusicSettings
    ) : this(Music.UID.auxio(MusicMode.PLAYLISTS, UUID.randomUUID()), name, songs, musicSettings)

    constructor(
        rawPlaylist: RawPlaylist,
        deviceLibrary: DeviceLibrary,
        musicSettings: MusicSettings
    ) : this(
        rawPlaylist.playlistInfo.playlistUid,
        rawPlaylist.playlistInfo.name,
        rawPlaylist.songs.mapNotNull { deviceLibrary.findSong(it.songUid) },
        musicSettings)

    override fun resolveName(context: Context) = rawName
    override val rawSortName = null
    override val sortName = SortName(rawName, musicSettings)
    override val durationMs = songs.sumOf { it.durationMs }
    override val albums =
        songs.groupBy { it.album }.entries.sortedByDescending { it.value.size }.map { it.key }
}
