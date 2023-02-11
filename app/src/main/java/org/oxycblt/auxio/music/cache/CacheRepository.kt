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

import android.content.Context
import org.oxycblt.auxio.music.cache.CacheDatabase
import org.oxycblt.auxio.music.cache.CachedSong
import org.oxycblt.auxio.music.cache.CachedSongsDao
import org.oxycblt.auxio.music.model.RawSong
import org.oxycblt.auxio.util.*

/**
 * A cache of music metadata obtained in prior music loading operations. Obtain an instance with
 * [CacheRepository].
 * @author Alexander Capehart (OxygenCobalt)
 */
interface Cache {
    /** Whether this cache has encountered a [RawSong] that did not have a cache entry. */
    val invalidated: Boolean

    /**
     * Populate a [RawSong] from a cache entry, if it exists.
     * @param rawSong The [RawSong] to populate.
     * @return true if a cache entry could be applied to [rawSong], false otherwise.
     */
    fun populate(rawSong: RawSong): Boolean
}

private class RealCache(cachedSongs: List<CachedSong>) : Cache {
    private val cacheMap = buildMap {
        for (cachedSong in cachedSongs) {
            put(cachedSong.mediaStoreId, cachedSong)
        }
    }

    override var invalidated = false
    override fun populate(rawSong: RawSong): Boolean {

        // For a cached raw song to be used, it must exist within the cache and have matching
        // addition and modification timestamps. Technically the addition timestamp doesn't
        // exist, but to safeguard against possible OEM-specific timestamp incoherence, we
        // check for it anyway.
        val cachedSong = cacheMap[rawSong.mediaStoreId]
        if (cachedSong != null &&
            cachedSong.dateAdded == rawSong.dateAdded &&
            cachedSong.dateModified == rawSong.dateModified) {
            cachedSong.copyToRaw(rawSong)
            return true
        }

        // We could not populate this song. This means our cache is stale and should be
        // re-written with newly-loaded music.
        invalidated = true
        return false
    }
}

/**
 * A repository allowing access to cached metadata obtained in prior music loading operations.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface CacheRepository {
    /**
     * Read the current [Cache], if it exists.
     * @return The stored [Cache], or null if it could not be obtained.
     */
    suspend fun readCache(): Cache?

    /**
     * Write the list of newly-loaded [RawSong]s to the cache, replacing the prior data.
     * @param rawSongs The [rawSongs] to write to the cache.
     */
    suspend fun writeCache(rawSongs: List<RawSong>)

    companion object {
        /**
         * Create a framework-backed instance.
         * @param context [Context] required.
         * @return A new instance.
         */
        fun from(context: Context): CacheRepository = RealCacheRepository(context)
    }
}

private class RealCacheRepository(private val context: Context) : CacheRepository {
    private val cachedSongsDao: CachedSongsDao by lazy {
        CacheDatabase.getInstance(context).cachedSongsDao()
    }

    override suspend fun readCache() =
        try {
            // Faster to load the whole database into memory than do a query on each
            // populate call.
            RealCache(cachedSongsDao.readSongs())
        } catch (e: Exception) {
            logE("Unable to load cache database.")
            logE(e.stackTraceToString())
            null
        }

    override suspend fun writeCache(rawSongs: List<RawSong>) {
        try {
            // Still write out whatever data was extracted.
            cachedSongsDao.nukeSongs()
            cachedSongsDao.insertSongs(rawSongs.map(CachedSong::fromRaw))
        } catch (e: Exception) {
            logE("Unable to save cache database.")
            logE(e.stackTraceToString())
        }
    }
}
