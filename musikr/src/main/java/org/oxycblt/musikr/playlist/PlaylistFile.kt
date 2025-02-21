/*
 * Copyright (c) 2024 Auxio Project
 * PlaylistFile.kt is part of Auxio.
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
 
package org.oxycblt.musikr.playlist

import org.oxycblt.musikr.Music
import org.oxycblt.musikr.Song

internal data class PlaylistFile(
    val name: String,
    val songPointers: List<SongPointer>,
    val handle: PlaylistHandle
)

internal sealed interface SongPointer {
    data class UID(val uid: Music.UID) : SongPointer
    //    data class Path(val options: List<Path>) : SongPointer
}

internal interface PlaylistHandle {
    val uid: Music.UID

    suspend fun rename(name: String)

    suspend fun add(songs: List<Song>)

    suspend fun rewrite(songs: List<Song>)

    suspend fun delete()
}
