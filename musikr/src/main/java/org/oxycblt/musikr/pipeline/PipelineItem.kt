/*
 * Copyright (c) 2025 Auxio Project
 * PipelineItem.kt is part of Auxio.
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
 
package org.oxycblt.musikr.pipeline

import org.oxycblt.musikr.covers.Cover
import org.oxycblt.musikr.fs.File
import org.oxycblt.musikr.metadata.Properties
import org.oxycblt.musikr.playlist.PlaylistFile
import org.oxycblt.musikr.tag.parse.ParsedTags

internal sealed interface PipelineItem

internal sealed interface Incomplete : PipelineItem

internal sealed interface Complete : PipelineItem

internal sealed interface Explored : PipelineItem {
    sealed interface New : Explored, Incomplete

    sealed interface Known : Explored, Complete
}

internal data class NewSong(val file: File, val addedMs: Long) : Explored.New

internal sealed interface Extracted : PipelineItem {
    sealed interface Valid : Complete, Extracted

    sealed interface Invalid : Extracted
}

internal data object InvalidSong : Extracted.Invalid

internal data class RawPlaylist(val file: PlaylistFile) : Explored.Known, Extracted.Valid

internal data class RawSong(
    val file: File,
    val properties: Properties,
    val tags: ParsedTags,
    val cover: Cover?,
    val addedMs: Long
) : Explored.Known, Extracted.Valid
