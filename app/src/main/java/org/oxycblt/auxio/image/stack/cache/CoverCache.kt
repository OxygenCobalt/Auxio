/*
 * Copyright (c) 2024 Auxio Project
 * CoverCache.kt is part of Auxio.
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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.music.Song

interface CoverCache {
    suspend fun read(song: Song): InputStream?

    suspend fun write(song: Song, inputStream: InputStream): Boolean
}

class CoverCacheImpl
@Inject
constructor(
    private val storedCoversDao: StoredCoversDao,
    private val appFiles: AppFiles,
    private val perceptualHash: PerceptualHash
) : CoverCache {

    override suspend fun read(song: Song): InputStream? {
        val perceptualHash =
            storedCoversDao.getCoverFile(song.uid, song.lastModified) ?: return null

        return appFiles.read(fileName(perceptualHash))
    }

    override suspend fun write(song: Song, inputStream: InputStream): Boolean =
        withContext(Dispatchers.IO) {
            val bitmap = BitmapFactory.decodeStream(inputStream)
            val perceptualHash = perceptualHash.hash(bitmap)

            // Compress bitmap down to webp into another inputstream
            val compressedStream =
                ByteArrayOutputStream().use { outputStream ->
                    bitmap.compress(COVER_CACHE_FORMAT, 80, outputStream)
                    ByteArrayInputStream(outputStream.toByteArray())
                }

            val writeSuccess = appFiles.write(fileName(perceptualHash), compressedStream)

            if (writeSuccess) {
                storedCoversDao.setCoverFile(
                    StoredCover(
                        uid = song.uid,
                        lastModified = song.lastModified,
                        perceptualHash = perceptualHash))
            }

            writeSuccess
        }

    private fun fileName(perceptualHash: String) = "cover_$perceptualHash.png"

    private companion object {
        @Suppress("DEPRECATION")
        val COVER_CACHE_FORMAT =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                Bitmap.CompressFormat.WEBP
            }
    }
}
