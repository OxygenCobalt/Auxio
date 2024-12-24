/*
 * Copyright (c) 2024 Auxio Project
 * Config.kt is part of Auxio.
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
 
package org.oxycblt.musikr

import org.oxycblt.musikr.cache.Cache
import org.oxycblt.musikr.cover.MutableStoredCovers
import org.oxycblt.musikr.playlist.db.StoredPlaylists
import org.oxycblt.musikr.tag.interpret.Naming
import org.oxycblt.musikr.tag.interpret.Separators
import org.oxycblt.musikr.track.Tracker

data class Storage(
    val cache: Cache,
    val tracker: Tracker,
    val storedCovers: MutableStoredCovers,
    val storedPlaylists: StoredPlaylists
)

data class Interpretation(val naming: Naming, val separators: Separators)
