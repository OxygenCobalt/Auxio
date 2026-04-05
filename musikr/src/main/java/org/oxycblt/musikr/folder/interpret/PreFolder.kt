/*
 * Copyright (c) 2024 Auxio Project
 * PreFolder.kt is part of Auxio.
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
 
package org.oxycblt.musikr.folder.interpret

import org.oxycblt.musikr.folder.FolderHandle
import org.oxycblt.musikr.folder.SongPointer
import org.oxycblt.musikr.tag.Name

internal interface PreFolderInfo {
    val name: Name.Known
    val rawName: String?
    val handle: FolderHandle
}

internal data class PreFolder(
    override val name: Name.Known,
    override val rawName: String?,
    override val handle: FolderHandle,
    val songPointers: List<SongPointer>,
) : PreFolderInfo

internal data class PostFolder(
    override val name: Name.Known,
    override val rawName: String?,
    override val handle: FolderHandle,
) : PreFolderInfo
