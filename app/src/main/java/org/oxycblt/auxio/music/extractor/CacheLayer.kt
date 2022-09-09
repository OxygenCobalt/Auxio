package org.oxycblt.auxio.music.extractor

import org.oxycblt.auxio.music.Song

/**
 * TODO: Stub class, not implemented yet
 */
class CacheLayer {
    fun init() {
        // STUB: Add cache database
    }

    fun finalize(rawSongs: List<Song.Raw>) {
        // STUB: Add cache database
    }

    fun maybePopulateCachedRaw(raw: Song.Raw) = false
}