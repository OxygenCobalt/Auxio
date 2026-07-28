/*
 * Copyright (c) 2026 Auxio Project
 * PendingImportHandle.kt is part of Auxio.
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

internal class PendingImportHandle(override val uid: Music.UID, override val updatedAt: Long) :
    PlaylistHandle {
    override suspend fun rename(name: String) {}

    override suspend fun add(songs: List<Song>) {}

    override suspend fun rewrite(songs: List<Song>) {}

    override suspend fun delete() {}
}
