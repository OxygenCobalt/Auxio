/*
 * Copyright (c) 2023 Auxio Project
 * FolderImpl.kt is part of Auxio.
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

import org.oxycblt.musikr.Folder
import org.oxycblt.musikr.Song
import org.oxycblt.musikr.covers.CoverCollection
import org.oxycblt.musikr.folder.interpret.PreFolderInfo
import org.oxycblt.musikr.tag.Name

internal interface FolderCore {
    val preFolder: PreFolderInfo
    val songs: List<Song>
}

internal class FolderImpl(private val core: FolderCore) : Folder {
    override val uid = core.preFolder.handle.uid
    override val name: Name.Known = core.preFolder.name
    override val durationMs = core.songs.sumOf { it.durationMs }
    override val covers = CoverCollection.from(core.songs.mapNotNull { it.cover })
    override val songs = core.songs

    private var hashCode = 31 * (31 * uid.hashCode() + core.preFolder.hashCode()) + songs.hashCode()

    override fun equals(other: Any?) =
        other is FolderImpl && core.preFolder == other.core.preFolder && songs == other.songs

    override fun hashCode() = hashCode

    override fun toString() = "Folder(uid=$uid, name=$name)"
}
