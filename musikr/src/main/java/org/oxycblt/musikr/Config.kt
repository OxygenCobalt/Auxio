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
import org.oxycblt.musikr.cover.MutableCovers
import org.oxycblt.musikr.playlist.db.StoredPlaylists
import org.oxycblt.musikr.tag.interpret.Naming
import org.oxycblt.musikr.tag.interpret.Separators

/** Side-effect laden [Storage] for use during music loading and [MutableLibrary] operation. */
data class Storage(
    /**
     * A factory producing a repository of cached metadata to read and write from over the course of
     * music loading. This will only be used during music loading.
     */
    val cache: Cache.Factory,

    /**
     * A repository of cover images to for re-use during music loading. Should be kept in lock-step
     * with the cache for best performance. This will be used during music loading and when
     * retrieving cover information from the library.
     */
    val storedCovers: MutableCovers,

    /**
     * A repository of user-created playlists that should also be loaded into the library. This will
     * be used during music loading and mutated when creating, renaming, or deleting playlists in
     * the library.
     */
    val storedPlaylists: StoredPlaylists
)

/** Configuration for how to interpret and extrapolate certain audio tags. */
data class Interpretation(
    /** How to construct names from audio tags. */
    val naming: Naming,

    /** What separators delimit multi-value audio tags. */
    val separators: Separators
)
