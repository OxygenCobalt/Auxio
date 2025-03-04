/*
 * Copyright (c) 2024 Auxio Project
 * AppFiles.kt is part of Auxio.
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
 
package org.oxycblt.musikr.fs.app

import android.os.ParcelFileDescriptor
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

interface AppFS {
    suspend fun find(name: String): AppFile?

    suspend fun write(name: String, block: suspend (OutputStream) -> Unit): AppFile

    suspend fun deleteWhere(block: (String) -> Boolean)

    companion object {
        suspend fun at(dir: File): AppFS {
            withContext(Dispatchers.IO) { check(dir.exists() && dir.isDirectory) }
            return AppFSImpl(dir)
        }
    }
}

interface AppFile {
    suspend fun fd(): ParcelFileDescriptor?

    suspend fun open(): InputStream?
}

private class AppFSImpl(private val dir: File) : AppFS {
    private val fileMutexes = mutableMapOf<String, Mutex>()
    private val mapMutex = Mutex()

    private suspend fun getMutexForFile(file: String): Mutex {
        return mapMutex.withLock { fileMutexes.getOrPut(file) { Mutex() } }
    }

    override suspend fun find(name: String): AppFile? =
        withContext(Dispatchers.IO) {
            try {
                File(dir, name).takeIf { it.exists() }?.let { AppFileImpl(it) }
            } catch (e: IOException) {
                null
            }
        }

    override suspend fun write(name: String, block: suspend (OutputStream) -> Unit): AppFile {
        val fileMutex = getMutexForFile(name)
        return fileMutex.withLock {
            val targetFile = File(dir, name)
            if (!targetFile.exists()) {
                withContext(Dispatchers.IO) {
                    val tempFile = File(dir, "$name.tmp")

                    try {
                        tempFile.outputStream().use { block(it) }
                        tempFile.renameTo(targetFile)
                        AppFileImpl(targetFile)
                    } catch (e: IOException) {
                        tempFile.delete()
                        throw e
                    }
                }
            } else {
                AppFileImpl(targetFile)
            }
        }
    }

    override suspend fun deleteWhere(block: (String) -> Boolean) {
        withContext(Dispatchers.IO) {
            dir.listFiles { file -> block(file.name) }?.forEach { it.deleteRecursively() }
        }
    }
}

private data class AppFileImpl(private val file: File) : AppFile {
    override suspend fun fd() =
        withContext(Dispatchers.IO) {
            try {
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            } catch (e: IOException) {
                null
            }
        }

    override suspend fun open() = withContext(Dispatchers.IO) { file.inputStream() }
}
