/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.music.extractor

import org.oxycblt.auxio.music.Song

/** TODO: Stub class, not implemented yet */
class CacheLayer {
    fun init() {
        // STUB: Add cache database
    }

    fun finalize(rawSongs: List<Song.Raw>) {
        // STUB: Add cache database
    }

    fun maybePopulateCachedRaw(raw: Song.Raw) = false
}

// TODO: Make raw naming consistent (always rawSong(s), not raw)
