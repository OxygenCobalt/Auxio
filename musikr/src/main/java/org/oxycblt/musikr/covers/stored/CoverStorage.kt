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

interface CoverStorage {
    suspend fun find(name: String): FDCover?

    suspend fun write(name: String, block: suspend (OutputStream) -> Unit): FDCover

    suspend fun ls(exclude: Set<String>): List<String>

    suspend fun rm(file: String)

    companion object {
        suspend fun at(dir: File): CoverStorage {
            withContext(Dispatchers.IO) {
                if (dir.exists()) check(dir.isDirectory) { "Not a directory" } else check(dir.mkdirs()) { "Cannot create directory" }
            }
            return CoverStorageImpl(dir)
        }
    }
}

private class CoverStorageImpl(private val dir: File) : CoverStorage {
    private val fileMutexes = mutableMapOf<String, Mutex>()
    private val mapMutex = Mutex()

    private suspend fun getMutexForFile(file: String): Mutex {
        return mapMutex.withLock { fileMutexes.getOrPut(file) { Mutex() } }
    }

    override suspend fun find(name: String): FDCover? =
        withContext(Dispatchers.IO) {
            try {
                File(dir, name).takeIf { it.exists() }?.let { StoredCover(it) }
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
                        StoredCover(targetFile)
                    } catch (e: IOException) {
                        tempFile.delete()
                        throw e
                    }
                }
            } else {
                StoredCover(targetFile)
            }
        }
    }

    override suspend fun ls(exclude: Set<String>): List<String> =
        withContext(Dispatchers.IO) {
            dir.listFiles()?.map { it.name }?.filter { exclude.contains(it) } ?: emptyList()
        }

    override suspend fun rm(file: String) {
        withContext(Dispatchers.IO) { File(dir, file).delete() }
    }
}

private data class StoredCover(private val file: File) : FDCover {
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

    override fun equals(other: Any?) = other is StoredCover && file == other.file

    override fun hashCode() = file.hashCode()
}
