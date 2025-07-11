/*
 * Copyright (c) 2024 Auxio Project
 * Cache.kt is part of Auxio.
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
 
package org.oxycblt.musikr.cache

import org.oxycblt.musikr.fs.File
import org.oxycblt.musikr.metadata.Properties
import org.oxycblt.musikr.tag.parse.ParsedTags

/**
 * An immutable repository for cached song metadata.
 *
 * Since file opening and metadata extraction sends to be quite slow on Android, a cache allows
 * up-to-date metadata to be read from a local database, which tends to be far faster.
 *
 * This is a read-only interface for reading cached metadata and isn't expected by Musikr's public
 * API, however there might be some use in external cache diagnostics by the client. For writing,
 * see [MutableCache].
 */
interface Cache {
    /**
     * Read a [CachedSong] corresponding to the given [file] from the cache. This can result in
     * several outcomes represented by [CacheResult].
     *
     * @param file the [File] to read from the cache
     * @return a [CacheResult] representing the result of the operation.
     */
    suspend fun read(file: File): CacheResult
}

/**
 * A mutable repository for cached song metadata.
 *
 * Since file opening and metadata extraction sends to be quite slow on Android, a cache allows
 * up-to-date metadata to be saved to a local database, which tends to be far faster.
 *
 * This is required by Musikr's public API for proper function.
 */
interface MutableCache : Cache {
    /**
     * Write a [CachedSong] to the cache.
     *
     * This should commit the metadata to the repository in such a way that it can be retrieved
     * later by [read] using only the [File].
     *
     * @param cachedSong the [CachedSong] to write to the cache
     */
    suspend fun write(cachedSong: CachedSong)

    /**
     * Cleanup the cache by removing all [CachedSong]s that are not in the provided [excluding]
     * list.
     *
     * This is paramount for any long-term persistence to maintain correct Date added metadata and
     * to avoid having space taken up by useless data.
     *
     * @param excluding a list of [CachedSong]s to exclude from cleanup, analogous to the library
     *   created by the loader this cache is used with.
     */
    suspend fun cleanup(excluding: List<CachedSong>)
}

/** A cached song entry containing the data needed by the rest of the loader. */
data class CachedSong(
    /** The file this song corresponds to. */
    val file: File,
    /** The properties of the song. */
    val properties: Properties,
    /** The parsed tags of the song. */
    val tags: ParsedTags,
    /**
     * The cover ID of the song. Should be understandable by the [org.oxycblt.musikr.covers.Covers]
     * implementation used.
     */
    val coverId: String?,
    /**
     * The time the song was added to the cache. Used for date added values. Should not be used for
     * cleanup since it is unlikely to be monotonic.
     */
    val addedMs: Long
)

/** A result of a cache lookup. */
sealed interface CacheResult {
    /**
     * A cache entry was found.
     *
     * @param song the [CachedSong] that was found.
     */
    data class Hit(val song: CachedSong) : CacheResult

    /**
     * A cache entry was not found.
     *
     * @param file the [File] that could not be found in the cache.
     */
    data class Miss(val file: File) : CacheResult

    /**
     * A cache entry was found, but it's out of date compared to the [file] given.
     *
     * @param file the [File] that was found in the cache.
     * @param addedMs the time the song was added to the cache.
     */
    data class Stale(val file: File, val addedMs: Long) : CacheResult
}
