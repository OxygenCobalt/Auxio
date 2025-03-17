/*
 * Copyright (c) 2024 Auxio Project
 * CoverStorage.kt is part of Auxio.
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
 
package org.oxycblt.musikr.covers.stored

import android.os.ParcelFileDescriptor
import java.io.File
import java.io.IOException
import java.io.OutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.oxycblt.musikr.covers.FDCover

/**
 * A cover storage interface backing [StoredCovers].
 *
 * Covers written here should be reasonably persisted long-term, and can be queries roughly as a
 * folder of cover files.
 */
interface CoverStorage {
    /**
     * Find a cover by a file-name.
     *
     * @return A [FDCover] if found, or null if not.
     */
    suspend fun find(name: String): FDCover?

    /**
     * Write a cover to the storage, yielding a new Cover instance.
     *
     * [block] is a critical section that may require some time to execute, so the specific cover
     * entry should be locked while it executes.
     *
     * @param name The name to write the cover to.
     * @param block The critical section where the cover data is written to the output stream.
     */
    suspend fun write(name: String, block: suspend (OutputStream) -> Unit): FDCover

    /**
     * List all cover files in the storage.
     *
     * @param exclude A set of file names to exclude from the result. This can make queries more
     *   efficient if used with native APIs.
     * @return A list of file names in the storage, excluding the specified ones.
     */
    suspend fun ls(exclude: Set<String>): List<String>

    /**
     * Remove a cover file from the storage.
     *
     * @param name The name of the file to remove. Will do nothing if this file does not exist.
     */
    suspend fun rm(name: String)

    companion object {
        /**
         * Create a [CoverStorage] implementation at some directory. Covers will be written in that
         * location.
         *
         * Note that in the context of Android's scoped storage, the given [File] will need to be in
         * the app's internal storage
         *
         * @param dir The directory to store the covers in.
         * @return A [CoverStorage] instance.
         */
        suspend fun at(dir: File): CoverStorage {
            withContext(Dispatchers.IO) {
                if (dir.exists()) check(dir.isDirectory) { "Not a directory" }
                else check(dir.mkdirs()) { "Cannot create directory" }
            }
            return FSCoverStorage(dir)
        }
    }
}

private class FSCoverStorage(private val dir: File) : CoverStorage {
    private val fileMutexes = mutableMapOf<String, Mutex>()
    private val mapMutex = Mutex()

    private suspend fun getMutexForFile(file: String): Mutex {
        return mapMutex.withLock { fileMutexes.getOrPut(file) { Mutex() } }
    }

    override suspend fun find(name: String): FDCover? =
        withContext(Dispatchers.IO) {
            try {
                File(dir, name).takeIf { it.exists() }?.let { FSStoredCover(it) }
            } catch (e: IOException) {
                null
            }
        }

    override suspend fun write(name: String, block: suspend (OutputStream) -> Unit): FDCover {
        val fileMutex = getMutexForFile(name)
        return fileMutex.withLock {
            val targetFile = File(dir, name)
            if (!targetFile.exists()) {
                withContext(Dispatchers.IO) {
                    val tempFile = File(dir, "$name.tmp")

                    try {
                        tempFile.outputStream().use { block(it) }
                        tempFile.renameTo(targetFile)
                        FSStoredCover(targetFile)
                    } catch (e: IOException) {
                        tempFile.delete()
                        throw e
                    }
                }
            } else {
                FSStoredCover(targetFile)
            }
        }
    }

    override suspend fun ls(exclude: Set<String>): List<String> =
        withContext(Dispatchers.IO) {
            dir.listFiles { _, name -> !exclude.contains(name) }?.map { it.name } ?: emptyList()
        }

    override suspend fun rm(name: String) {
        withContext(Dispatchers.IO) { File(dir, name).deleteRecursively() }
    }
}

private data class FSStoredCover(private val file: File) : FDCover {
    override val id: String = file.name

    override suspend fun fd() =
        withContext(Dispatchers.IO) {
            try {
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            } catch (e: IOException) {
                null
            }
        }

    override suspend fun open() = withContext(Dispatchers.IO) { file.inputStream() }

    override fun equals(other: Any?) = other is FSStoredCover && file == other.file

    override fun hashCode() = file.hashCode()
}
