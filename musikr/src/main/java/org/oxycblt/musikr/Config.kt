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

import org.oxycblt.musikr.cache.MutableCache
import org.oxycblt.musikr.covers.Cover
import org.oxycblt.musikr.covers.MutableCovers
import org.oxycblt.musikr.fs.FS
import org.oxycblt.musikr.playlist.db.StoredPlaylists
import org.oxycblt.musikr.tag.interpret.Naming
import org.oxycblt.musikr.tag.interpret.Separators

data class Config(val fs: FS, val storage: Storage, val interpretation: Interpretation)

/** Side-effect laden [Config] for use during music loading and [MutableLibrary] operation. */
data class Storage(
    /**
     * A repository of cached metadata to read and write from over the course of music loading. This
     * will only be used during music loading.
     */
    val cache: MutableCache,

    /**
     * A repository of cover images to for re-use during music loading. Should be kept in lock-step
     * with the cache for best performance. This will be used during music loading and when
     * retrieving cover information from the library.
     */
    val covers: MutableCovers<out Cover>,

    /**
     * A repository of user-created playlists that should also be loaded into the library. This will
     * be used during music loading and mutated when creating, renaming, or deleting playlists in
     * the library.
     */
    val storedPlaylists: StoredPlaylists,
)

data class Interpretation(
    /** How to construct names from audio tags. */
    val naming: Naming,

    /** What separators delimit multi-value audio tags. */
    val separators: Separators,
)
