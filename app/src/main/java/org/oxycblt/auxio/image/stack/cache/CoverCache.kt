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
import org.oxycblt.auxio.image.Cover

interface CoverCache {
    suspend fun read(cover: Cover.Single): InputStream?

    suspend fun write(cover: Cover.Single, data: ByteArray): InputStream?
}

class CoverCacheImpl
@Inject
constructor(
    private val coverIdentifier: CoverIdentifier,
    private val storedCoversDao: StoredCoversDao,
    private val appFiles: AppFiles
) : CoverCache {

    override suspend fun read(cover: Cover.Single): InputStream? {
        val perceptualHash =
            storedCoversDao.getStoredCover(cover.uid, cover.lastModified) ?: return null

        return appFiles.read(fileName(perceptualHash))
    }

    override suspend fun write(cover: Cover.Single, data: ByteArray): InputStream? {
        val id = coverIdentifier.identify(data)
        val file = fileName(id)
        if (!appFiles.exists(file)) {
            val transcoded = transcodeImage(data, FORMAT_WEBP)
            val writeSuccess = appFiles.write(fileName(id), transcoded)
            if (!writeSuccess) {
                return null
            }
        }

        storedCoversDao.setStoredCover(
            StoredCover(uid = cover.uid, lastModified = cover.lastModified, coverId = id))

        return appFiles.read(file)
    }

    private fun fileName(id: String) = "cover_$id"

    private fun transcodeImage(data: ByteArray, targetFormat: Bitmap.CompressFormat): InputStream {
        val options =
            BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                BitmapFactory.decodeByteArray(data, 0, data.size, this)
            }

        options.inSampleSize = calculateInSampleSize(options, 750, 750)

        val bitmap =
            BitmapFactory.decodeByteArray(
                data, 0, data.size, options.apply { inJustDecodeBounds = false })

        return ByteArrayOutputStream().use { outputStream ->
            bitmap?.compress(targetFormat, 80, outputStream)
            ByteArrayInputStream(outputStream.toByteArray())
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        var inSampleSize = 1
        val (height, width) = options.outHeight to options.outWidth

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / inSampleSize) >= reqHeight &&
                (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private companion object {
        val FORMAT_WEBP =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                Bitmap.CompressFormat.WEBP
            }
    }
}
