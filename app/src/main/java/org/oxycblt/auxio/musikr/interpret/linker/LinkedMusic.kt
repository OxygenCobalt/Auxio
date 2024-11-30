/*
 * Copyright (c) 2024 Auxio Project
 * LinkedMusic.kt is part of Auxio.
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
 
package org.oxycblt.auxio.musikr.interpret.linker

import org.oxycblt.auxio.musikr.interpret.model.AlbumImpl
import org.oxycblt.auxio.musikr.interpret.model.ArtistImpl
import org.oxycblt.auxio.musikr.interpret.model.GenreImpl
import org.oxycblt.auxio.musikr.interpret.model.PlaylistImpl
import org.oxycblt.auxio.musikr.interpret.model.SongImpl
import org.oxycblt.auxio.musikr.interpret.prepare.PreAlbum
import org.oxycblt.auxio.musikr.interpret.prepare.PrePlaylist
import org.oxycblt.auxio.musikr.interpret.prepare.PreSong

interface LinkedSong {
    val preSong: PreSong
    val album: Linked<AlbumImpl, SongImpl>
    val artists: Linked<List<ArtistImpl>, SongImpl>
    val genres: Linked<List<GenreImpl>, SongImpl>
}

interface LinkedAlbum {
    val preAlbum: PreAlbum
    val artists: Linked<List<ArtistImpl>, AlbumImpl>
}

interface LinkedPlaylist {
    val prePlaylist: PrePlaylist
    val songs: Linked<List<SongImpl>, PlaylistImpl>
}

interface Linked<P, C> {
    fun resolve(child: C): P
}
