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
 
package org.oxycblt.auxio.music.playlist

import android.content.Context
import org.oxycblt.auxio.music.*
import org.oxycblt.auxio.music.library.Library

class PlaylistImpl(rawPlaylist: RawPlaylist, library: Library, musicSettings: MusicSettings) :
    Playlist {
    override val uid = rawPlaylist.playlistInfo.playlistUid
    override val rawName = rawPlaylist.playlistInfo.name
    override fun resolveName(context: Context) = rawName
    override val rawSortName = null
    override val sortName = SortName(rawName, musicSettings)
    override val songs = rawPlaylist.songs.mapNotNull { library.find<Song>(it.songUid) }
}
