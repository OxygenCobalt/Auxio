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

package org.oxycblt.musikr.cover

import android.content.Context
import android.util.Log
import java.io.File
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.oxycblt.musikr.fs.Components
import org.oxycblt.musikr.util.update
import java.io.InputStream
import java.io.OutputStream
import java.security.MessageDigest
import java.util.UUID

internal interface AppFiles {
    suspend fun read(path: Components): AppFile?

    suspend fun write(path: Components, block: suspend (OutputStream) -> Unit): AppFile?

    companion object {
        fun from(context: Context): AppFiles =
            AppFilesImpl(context.filesDir)
    }
}

interface AppFile {
    val path: Components
    suspend fun open(): InputStream?
}

private class AppFilesImpl(private val rootDir: File) :
    AppFiles {
    private val tempDir = File(rootDir, "tmp-${UUID.randomUUID()}")
    private val fileMutexes = mutableMapOf<String, Mutex>()
    private val mapMutex = Mutex()

    private suspend fun getMutexForFile(path: String): Mutex {
        return mapMutex.withLock { fileMutexes.getOrPut(path) { Mutex() } }
    }

    override suspend fun read(path: Components): AppFile? =
        withContext(Dispatchers.IO) {
            val file = rootDir.resolve(path.unixString)
            if (file.exists()) {
                AppFileImpl(path, file)
            } else {
                null
            }
        }

    override suspend fun write(path: Components, block: suspend (OutputStream) -> Unit): AppFile? =
        withContext(Dispatchers.IO) {
            if (!tempDir.exists()) {
                tempDir.mkdirs()
            }
            val parentDir = rootDir.resolve(path.parent().toString())
            if (parentDir.isFile) {
                parentDir.delete()
            }
            if (!parentDir.exists()) {
                parentDir.mkdirs()
            }
            val pathString = path.unixString
            val fileMutex = getMutexForFile(pathString)

            fileMutex.withLock {
                val targetFile = rootDir.resolve(pathString)
                if (targetFile.exists()) {
                    return@withLock AppFileImpl(path, targetFile)
                }
                val tempFile = tempDir.resolve(pathString.sha256())

                try {
                    block(tempFile.outputStream())
                    tempFile.renameTo(targetFile)
                    AppFileImpl(path, targetFile)
                } catch (e: IOException) {
                    tempFile.delete()
                    null
                }
            }
        }
}

class AppFileImpl(
    override val path: Components,
    private val file: File
) : AppFile {
    override suspend fun open() = withContext(Dispatchers.IO) { file.inputStream() }
}

@OptIn(ExperimentalStdlibApi::class)
private fun String.sha256() = MessageDigest.getInstance("SHA-256").let {
    it.update(this)
    it.digest().toHexString()
}

