/*
 * Copyright (c) 2024 Auxio Project
 * FolderInterpreter.kt is part of Auxio.
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

import org.oxycblt.musikr.Interpretation
import org.oxycblt.musikr.folder.FolderFile
import org.oxycblt.musikr.folder.FolderHandle
import org.oxycblt.musikr.tag.interpret.Naming

internal interface FolderInterpreter {
    fun interpret(file: FolderFile): PreFolder

    fun interpret(name: String, handle: FolderHandle): PostFolder

    companion object {
        fun new(interpretation: Interpretation): FolderInterpreter =
            FolderInterpreterImpl(interpretation.naming)
    }
}

private class FolderInterpreterImpl(private val naming: Naming) : FolderInterpreter {
    override fun interpret(file: FolderFile) =
        PreFolder(
            name = naming.name(file.name, null),
            rawName = file.name,
            handle = file.handle,
            songPointers = file.songPointers,
        )

    override fun interpret(name: String, handle: FolderHandle): PostFolder =
        PostFolder(name = naming.name(name, null), rawName = name, handle = handle)
}
