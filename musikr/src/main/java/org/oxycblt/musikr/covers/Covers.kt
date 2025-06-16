/*
 * Copyright (c) 2024 Auxio Project
 * Covers.kt is part of Auxio.
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
 
package org.oxycblt.musikr.covers

import android.os.ParcelFileDescriptor
import java.io.InputStream
import org.oxycblt.musikr.fs.DeviceFile
import org.oxycblt.musikr.metadata.Metadata

/**
 * An immutable repository for cover information.
 *
 * While not directly required by the music loader, this can still be used to work with covers
 * marshalled over some I/O boundary via their ID.
 */
interface Covers<T : Cover> {
    /**
     * Obtain a cover instance by it's ID.
     *
     * You cannot assume anything about the data source this will use.
     *
     * @param id The ID of the cover to obtain
     * @return a [CoverResult] indicating whether the cover was found or not
     */
    suspend fun obtain(id: String): CoverResult<T>
}

/**
 * An mutable repoistory for cover information.
 *
 * This is explicitly required by the music loader to figure out cover instances to use over some
 * I/O boundary.
 */
interface MutableCovers<T : Cover> : Covers<T> {
    /**
     * Create a cover instance for the given [file] and [metadata].
     *
     * This could result in side-effect-laden storage, or be a simple translation into a lazily
     * loaded [Cover] instance.
     *
     * @param file The [DeviceFile] to of the file to create a cover for.
     * @param metadata The [Metadata] to use to create the cover.
     * @return a [CoverResult] indicating whether the cover was created or not
     */
    suspend fun create(file: DeviceFile, metadata: Metadata): CoverResult<T>

    /**
     * Cleanup the cover repository by removing any covers that are not in the [excluding]
     * collection.
     *
     * This is useful with cached covers to prevent accumulation of useless data.
     *
     * @param excluding The collection of covers to exclude from cleanup.
     */
    suspend fun cleanup(excluding: Collection<Cover>)
}

/** A result of a cover lookup. */
sealed interface CoverResult<T : Cover> {
    /**
     * A cover was found for the given ID/file.
     *
     * @param cover The cover that was found.
     */
    data class Hit<T : Cover>(val cover: T) : CoverResult<T>

    /**
     * A cover was not found for the given ID.
     *
     * For [Covers.obtain], this implies that the cover repository is outdated for the particular
     * song's cover ID queries. Therefore, returning it in that context will trigger the song to be
     * re-extracted.
     *
     * For [MutableCovers.create], this implies that the song being queries does not have a cover.
     * In that case, the song will be represented as not having a cover at all.
     */
    class Miss<T : Cover> : CoverResult<T>
}

/**
 * Some song's cover art.
 *
 * A cover can be backed by any kind of data source and depends on the [Covers]/[MutableCovers] that
 * yields it.
 */
interface Cover {
    /**
     * The ID of the cover. This is used to identify the cover in the [Covers] repository, and is
     * useful if the cover data needs to be marshalled over an I/O boundary.
     */
    val id: String

    /**
     * Open the cover for reading. This might require blocking operations.
     *
     * @return an [InputStream] for the cover, or null if an error occurred. Assume nothing about
     *   the internal implementation of the stream or the validity of the image format.
     */
    suspend fun open(): InputStream?

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int
}

/**
 * A cover that can be opened as a [ParcelFileDescriptor].
 *
 * This more or less implies that the cover is explicitly stored on-device somewhere.
 */
interface FDCover : Cover {
    /**
     * Open the cover for reading as a [ParcelFileDescriptor]. Useful in some content provider
     * contexts. This might require blocking operations.
     *
     * @return a [ParcelFileDescriptor] for the cover, or null if an error occurred preventing it
     *   from being opened. Assume nothing about the validity of the image format.
     */
    suspend fun fd(): ParcelFileDescriptor?
}

/**
 * A cover exclusively hosted in-memory. These tend to not be exposed in practice and are often
 * cached into a [FDCover].
 */
interface MemoryCover : Cover {
    /** Get the raw data this cover holds. Might be a valid image. */
    fun data(): ByteArray
}

/**
 * A large collection of [Cover]s, organized by frequency.
 *
 * This is useful if you want to compose several [Cover]s into a single image.
 */
class CoverCollection private constructor(val covers: List<Cover>) {
    override fun hashCode() = covers.hashCode()

    override fun equals(other: Any?) = other is CoverCollection && covers == other.covers

    companion object {
        /**
         * Create a [CoverCollection] from a collection of [Cover]s.
         *
         * This will deduplicate and organize the covers by frequency. Since doing such is a
         * time-consuming operation that should be done asynchronously to avoid lockups in UI
         * contexts.
         *
         * @return a [CoverCollection] containing the most frequent covers in the given collection.
         */
        fun from(covers: Collection<Cover>) =
            CoverCollection(
                covers
                    .groupBy { it.id }
                    .entries
                    .sortedByDescending { it.key }
                    .sortedByDescending { it.value.size }
                    .map { it.value.first() })
    }
}
