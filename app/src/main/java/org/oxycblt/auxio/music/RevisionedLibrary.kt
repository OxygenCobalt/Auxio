/*
 * Copyright (c) 2024 Auxio Project
 * RevisionedLibrary.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music

import org.oxycblt.musikr.Album
import org.oxycblt.musikr.Artist
import java.util.UUID
import org.oxycblt.musikr.Library
import org.oxycblt.musikr.MutableLibrary
import org.oxycblt.musikr.Playlist
import org.oxycblt.musikr.Song

interface RevisionedLibrary : Library {
    val revision: UUID
}

class MutableRevisionedLibrary(override val revision: UUID, private val inner: MutableLibrary) :
    RevisionedLibrary, Library by inner, MutableLibrary {
    override suspend fun createPlaylist(name: String, songs: List<Song>) =
        MutableRevisionedLibrary(revision, inner.createPlaylist(name, songs))

    override suspend fun renamePlaylist(playlist: Playlist, name: String) =
        MutableRevisionedLibrary(revision, inner.renamePlaylist(playlist, name))

    override suspend fun addToPlaylist(playlist: Playlist, songs: List<Song>) =
        MutableRevisionedLibrary(revision, inner.addToPlaylist(playlist, songs))

    override suspend fun rewritePlaylist(playlist: Playlist, songs: List<Song>) =
        MutableRevisionedLibrary(revision, inner.rewritePlaylist(playlist, songs))

    override suspend fun deletePlaylist(playlist: Playlist) =
        MutableRevisionedLibrary(revision, inner.deletePlaylist(playlist))
}
