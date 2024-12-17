/*
 * Copyright (c) 2024 Auxio Project
 * PlaylistInterpreter.kt is part of Auxio.
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
 
package org.oxycblt.musikr.playlist.interpret

import org.oxycblt.musikr.Interpretation
import org.oxycblt.musikr.playlist.PlaylistFile
import org.oxycblt.musikr.playlist.PlaylistHandle

internal interface PlaylistInterpreter {
    fun interpret(file: PlaylistFile, interpretation: Interpretation): PrePlaylist

    fun interpret(
        name: String,
        handle: PlaylistHandle,
        interpretation: Interpretation
    ): PostPlaylist

    companion object {
        fun new(): PlaylistInterpreter = PlaylistInterpreterImpl
    }
}

private data object PlaylistInterpreterImpl : PlaylistInterpreter {
    override fun interpret(file: PlaylistFile, interpretation: Interpretation) =
        PrePlaylist(
            name = interpretation.naming.name(file.name, null),
            rawName = file.name,
            handle = file.handle,
            songPointers = file.songPointers)

    override fun interpret(
        name: String,
        handle: PlaylistHandle,
        interpretation: Interpretation
    ): PostPlaylist =
        PostPlaylist(name = interpretation.naming.name(name, null), rawName = name, handle = handle)
}
