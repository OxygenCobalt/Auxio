/*
 * Copyright (c) 2024 Auxio Project
 * PipelineException.kt is part of Auxio.
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

import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.playlist.PlaylistFile
import org.oxycblt.musikr.playlist.interpret.PrePlaylist
import org.oxycblt.musikr.tag.interpret.PreSong

class PipelineException(val processing: WhileProcessing, val error: Exception) : Exception() {
    override val cause = error

    override val message = "Error while processing ${processing}: ${error.stackTraceToString()}"
}

sealed interface WhileProcessing {
    class AFile internal constructor(private val file: DeviceFile) : WhileProcessing {
        override fun toString() = "File @ ${file.path}"
    }

    class ARawSong internal constructor(private val rawSong: RawSong) : WhileProcessing {
        override fun toString() = "Raw Song @ ${rawSong.file.path}"
    }

    class APlaylistFile internal constructor(private val playlist: PlaylistFile) : WhileProcessing {
        override fun toString() = "Playlist File @ ${playlist.name}"
    }

    class APreSong internal constructor(private val preSong: PreSong) : WhileProcessing {
        override fun toString() = "Pre Song @ ${preSong.path}"
    }

    class APrePlaylist internal constructor(private val prePlaylist: PrePlaylist) :
        WhileProcessing {
        override fun toString() = "Pre Playlist @ ${prePlaylist.name}"
    }
}

internal suspend fun <R> wrap(file: DeviceFile, block: suspend (DeviceFile) -> R): R =
    try {
        block(file)
    } catch (e: Exception) {
        throw PipelineException(WhileProcessing.AFile(file), e)
    }

internal suspend fun <R> wrap(song: RawSong, block: suspend (RawSong) -> R): R =
    try {
        block(song)
    } catch (e: Exception) {
        throw PipelineException(WhileProcessing.ARawSong(song), e)
    }

internal suspend fun <R> wrap(file: PlaylistFile, block: suspend (PlaylistFile) -> R): R =
    try {
        block(file)
    } catch (e: Exception) {
        throw PipelineException(WhileProcessing.APlaylistFile(file), e)
    }

internal suspend fun <R> wrap(song: PreSong, block: suspend (PreSong) -> R): R =
    try {
        block(song)
    } catch (e: Exception) {
        throw PipelineException(WhileProcessing.APreSong(song), e)
    }

internal suspend fun <R> wrap(playlist: PrePlaylist, block: suspend (PrePlaylist) -> R): R =
    try {
        block(playlist)
    } catch (e: Exception) {
        throw PipelineException(WhileProcessing.APrePlaylist(playlist), e)
    }
