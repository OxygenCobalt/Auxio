/*
 * Copyright (c) 2024 Auxio Project
 * PlaylistLinker.kt is part of Auxio.
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
 
package org.oxycblt.auxio.musikr.interpret.link

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.oxycblt.auxio.musikr.playlist.PlaylistFile
import org.oxycblt.auxio.musikr.model.PlaylistImpl

class PlaylistLinker {
    fun register(
        playlists: Flow<PlaylistFile>,
        linkedSongs: Flow<AlbumLinker.LinkedSong>
    ): Flow<LinkedPlaylist> = emptyFlow()

    fun resolve(): Collection<PlaylistImpl> = setOf()
}
