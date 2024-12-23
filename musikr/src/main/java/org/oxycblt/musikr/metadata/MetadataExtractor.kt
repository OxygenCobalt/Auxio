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

import android.os.ParcelFileDescriptor
import java.io.FileInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal interface MetadataExtractor {
    suspend fun extract(fd: ParcelFileDescriptor): Metadata?

    companion object {
        fun new(): MetadataExtractor = MetadataExtractorImpl
    }
}

private object MetadataExtractorImpl : MetadataExtractor {
    override suspend fun extract(fd: ParcelFileDescriptor) =
        withContext(Dispatchers.IO) {
            val fis = FileInputStream(fd.fileDescriptor)
            TagLibJNI.open(fis).also { fis.close() }
        }
}
