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
 
package org.oxycblt.auxio.musikr.model

import org.oxycblt.auxio.musikr.cover.Cover
import org.oxycblt.auxio.music.Playlist
import org.oxycblt.auxio.musikr.interpret.link.LinkedPlaylist

class PlaylistImpl(linkedPlaylist: LinkedPlaylist) : Playlist {
    private val prePlaylist = linkedPlaylist.prePlaylist
    override val uid = prePlaylist.handle.uid
    override val name: Name.Known = prePlaylist.name
    override val songs = linkedPlaylist.songs.resolve(this)
    override val durationMs = songs.sumOf { it.durationMs }
    override val cover = Cover.multi(songs)
    private var hashCode = uid.hashCode()

    init {
        hashCode = 31 * hashCode + prePlaylist.hashCode()
        hashCode = 31 * hashCode + songs.hashCode()
    }

    override fun equals(other: Any?) =
        other is PlaylistImpl && prePlaylist == other.prePlaylist && songs == other.songs

    override fun hashCode() = hashCode

    override fun toString() = "Playlist(uid=$uid, name=$name)"
}
