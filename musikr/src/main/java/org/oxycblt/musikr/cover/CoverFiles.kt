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

import android.content.Context
import android.util.Log
import java.io.File
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

interface CoverFiles {
    suspend fun find(id: String): CoverFile?
    suspend fun write(id: String, data: ByteArray): CoverFile?

    companion object {
        fun at(context: Context, path: String): CoverFiles =
            CoverFilesImpl(File(context.filesDir, path).also { it.mkdirs() }, CoverFormat.webp())
    }
}

interface CoverFile {
    suspend fun open(): InputStream?
}

private class CoverFilesImpl(private val dir: File, private val coverFormat: CoverFormat) :
    CoverFiles {
    private val fileMutexes = mutableMapOf<String, Mutex>()
    private val mapMutex = Mutex()

    private suspend fun getMutexForFile(file: String): Mutex {
        return mapMutex.withLock { fileMutexes.getOrPut(file) { Mutex() } }
    }

    override suspend fun find(id: String): CoverFile? =
        withContext(Dispatchers.IO) {
            try {
                File(dir, getTargetFilePath(id)).takeIf { it.exists() }?.let { CoverFileImpl(it) }
            } catch (e: IOException) {
                null
            }
        }

    override suspend fun write(id: String, data: ByteArray): CoverFile? {
        val fileMutex = getMutexForFile(id)
        return fileMutex.withLock {
            val targetFile = File(dir, getTargetFilePath(id))
            if (!targetFile.exists()) {
                withContext(Dispatchers.IO) {
                    val tempFile = File(dir, getTempFilePath(id))

                    try {
                        tempFile.outputStream().use { coverFormat.transcodeInto(data, it) }
                        tempFile.renameTo(targetFile)
                        CoverFileImpl(targetFile)
                    } catch (e: IOException) {
                        tempFile.delete()
                        null
                    }
                }
            } else {
                CoverFileImpl(targetFile)
            }
        }
    }

    private fun getTargetFilePath(name: String) = "cover_${name}.${coverFormat.extension}"

    private fun getTempFilePath(name: String) = "${getTargetFilePath(name)}.tmp"
}

private class CoverFileImpl(private val file: File) : CoverFile {
    override suspend fun open() = withContext(Dispatchers.IO) { file.inputStream() }
}