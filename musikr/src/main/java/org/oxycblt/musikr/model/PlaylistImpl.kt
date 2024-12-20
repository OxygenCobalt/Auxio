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
 
package org.oxycblt.musikr.model

import org.oxycblt.musikr.Playlist
import org.oxycblt.musikr.Song
import org.oxycblt.musikr.cover.Cover
import org.oxycblt.musikr.playlist.interpret.PrePlaylistInfo
import org.oxycblt.musikr.tag.Name

internal interface PlaylistCore {
    val prePlaylist: PrePlaylistInfo
    val songs: List<Song>
}

internal class PlaylistImpl(val core: PlaylistCore) : Playlist {
    override val uid = core.prePlaylist.handle.uid
    override val name: Name.Known = core.prePlaylist.name
    override val durationMs = core.songs.sumOf { it.durationMs }
    override val cover = Cover.multi(core.songs)
    override val songs = core.songs

    private var hashCode =
        31 * (31 * uid.hashCode() + core.prePlaylist.hashCode()) + songs.hashCode()

    override fun equals(other: Any?) =
        other is PlaylistImpl && core.prePlaylist == other.core.prePlaylist && songs == other.songs

    override fun hashCode() = hashCode

    override fun toString() = "Playlist(uid=$uid, name=$name)"
}
