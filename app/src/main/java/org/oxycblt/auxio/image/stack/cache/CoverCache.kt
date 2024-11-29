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
import kotlin.math.min
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.image.Cover

interface CoverCache {
    suspend fun read(cover: Cover.Single): InputStream?

    suspend fun write(cover: Cover.Single, inputStream: InputStream): InputStream?
}

class CoverCacheImpl
@Inject
constructor(private val storedCoversDao: StoredCoversDao, private val appFiles: AppFiles) :
    CoverCache {

    override suspend fun read(cover: Cover.Single): InputStream? {
        val perceptualHash =
            storedCoversDao.getCoverFile(cover.uid, cover.lastModified) ?: return null

        return appFiles.read(fileName(perceptualHash))
    }

    override suspend fun write(cover: Cover.Single, inputStream: InputStream): InputStream? {
        val id =
            withContext(Dispatchers.IO) {
                val available = inputStream.available()
                val skip = min(available / 2L, available - COVER_KEY_SAMPLE.toLong())
                inputStream.skip(skip)
                val bytes = ByteArray(COVER_KEY_SAMPLE)
                inputStream.read(bytes)
                inputStream.reset()
                @OptIn(ExperimentalStdlibApi::class) bytes.toHexString()
            }
        val file = fileName(id)
        if (!appFiles.exists(file)) {
            val transcoded = transcodeImage(inputStream, FORMAT_WEBP)
            val writeSuccess = appFiles.write(fileName(id), transcoded)
            if (!writeSuccess) {
                return null
            }
        }

        storedCoversDao.setCoverFile(
            StoredCover(uid = cover.uid, lastModified = cover.lastModified, perceptualHash = id))

        return appFiles.read(file)
    }

    private fun fileName(id: String) = "cover_$id"

    private fun transcodeImage(
        inputStream: InputStream,
        targetFormat: Bitmap.CompressFormat
    ): InputStream {
        val options =
            BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                BitmapFactory.decodeStream(inputStream, null, this)
            }

        options.inSampleSize = calculateInSampleSize(options, 750, 750)
        inputStream.reset()

        val bitmap =
            BitmapFactory.decodeStream(
                inputStream, null, options.apply { inJustDecodeBounds = false })

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
        const val COVER_KEY_SAMPLE = 32
        val FORMAT_WEBP =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Bitmap.CompressFormat.WEBP_LOSSY
            } else {
                Bitmap.CompressFormat.WEBP
            }
    }
}
