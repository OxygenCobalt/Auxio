/*
 * Copyright (c) 2024 Auxio Project
 * AOSPCoverSource.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image.stack.extractor

import android.media.MediaMetadataRetriever
import android.net.Uri
import java.io.InputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AOSPCoverSource @Inject constructor() : CoverSource {
    override suspend fun extract(fileUri: Uri): InputStream? {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        val cover =
            withContext(Dispatchers.IO) {
                mediaMetadataRetriever.setDataSource(fileUri.toString())
                mediaMetadataRetriever.embeddedPicture
            } ?: return null
        return cover.inputStream()
    }
}
