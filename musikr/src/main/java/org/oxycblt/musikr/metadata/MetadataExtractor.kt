/*
 * Copyright (c) 2024 Auxio Project
 * MetadataExtractor.kt is part of Auxio.
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
 
package org.oxycblt.musikr.metadata

import android.content.ContentResolver
import android.content.Context
import java.io.FileInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.musikr.fs.File

internal interface MetadataExtractor {
    suspend fun extract(deviceFile: File): Metadata?

    companion object {
        fun from(context: Context): MetadataExtractor =
            MetadataExtractorImpl(context.contentResolver)
    }
}

private class MetadataExtractorImpl(private val contentResolver: ContentResolver) :
    MetadataExtractor {
    override suspend fun extract(deviceFile: File): Metadata? =
        withContext(Dispatchers.IO) {
            contentResolver.openFileDescriptor(deviceFile.uri, "r")?.use { fd ->
                val fis = FileInputStream(fd.fileDescriptor)
                TagLibJNI.open(deviceFile, fis).also { fis.close() }
            }
        }
}
