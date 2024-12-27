/*
 * Copyright (c) 2024 Auxio Project
 * CoverFiles.kt is part of Auxio.
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
 
package org.oxycblt.musikr.cover

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

interface CoverFiles {
    suspend fun find(name: String): CoverFile?

    suspend fun write(name: String, block: suspend (OutputStream) -> Unit): CoverFile

    suspend fun deleteWhere(block: (String) -> Boolean)

    companion object {
        suspend fun at(dir: File): CoverFiles {
            withContext(Dispatchers.IO) { check(dir.exists() && dir.isDirectory) }
            return CoverFilesImpl(dir)
        }
    }
}

interface CoverFile {
    suspend fun open(): InputStream?
}

private class CoverFilesImpl(private val dir: File) : CoverFiles {
    private val fileMutexes = mutableMapOf<String, Mutex>()
    private val mapMutex = Mutex()

    private suspend fun getMutexForFile(file: String): Mutex {
        return mapMutex.withLock { fileMutexes.getOrPut(file) { Mutex() } }
    }

    override suspend fun find(name: String): CoverFile? =
        withContext(Dispatchers.IO) {
            try {
                File(dir, name).takeIf { it.exists() }?.let { CoverFileImpl(it) }
            } catch (e: IOException) {
                null
            }
        }

    override suspend fun write(name: String, block: suspend (OutputStream) -> Unit): CoverFile {
        val fileMutex = getMutexForFile(name)
        return fileMutex.withLock {
            val targetFile = File(dir, name)
            if (!targetFile.exists()) {
                withContext(Dispatchers.IO) {
                    val tempFile = File(dir, "$name.tmp")

                    try {
                        tempFile.outputStream().use { block(it) }
                        tempFile.renameTo(targetFile)
                        CoverFileImpl(targetFile)
                    } catch (e: IOException) {
                        tempFile.delete()
                        throw e
                    }
                }
            } else {
                CoverFileImpl(targetFile)
            }
        }
    }

    override suspend fun deleteWhere(block: (String) -> Boolean) {
        withContext(Dispatchers.IO) {
            dir.listFiles { file -> block(file.name) }?.forEach { it.deleteRecursively() }
        }
    }
}

private class CoverFileImpl(private val file: File) : CoverFile {
    override suspend fun open() = withContext(Dispatchers.IO) { file.inputStream() }
}
