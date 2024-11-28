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
 
package org.oxycblt.auxio.image.stack.cache

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface AppFiles {
    suspend fun read(file: String): InputStream?

    suspend fun write(file: String, inputStream: InputStream): Boolean
}

class AppFilesImpl @Inject constructor(@ApplicationContext private val context: Context) :
    AppFiles {
    override suspend fun read(file: String): InputStream? =
        withContext(context = Dispatchers.IO) {
            try {
                context.openFileInput(file)
            } catch (e: IOException) {
                null
            }
        }

    override suspend fun write(file: String, inputStream: InputStream) =
        withContext(context = Dispatchers.IO) {
            try {
                context.openFileOutput(file, Context.MODE_PRIVATE).use { fileOutputStream ->
                    inputStream.copyTo(fileOutputStream)
                }
                true
            } catch (e: IOException) {
                false
            } finally {
                inputStream.close()
            }
        }
}
