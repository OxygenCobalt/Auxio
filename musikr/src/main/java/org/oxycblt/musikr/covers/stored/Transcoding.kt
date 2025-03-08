/*
 * Copyright (c) 2025 Auxio Project
 * Transcoding.kt is part of Auxio.
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
 
package org.oxycblt.musikr.covers.stored

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.OutputStream

interface Transcoding {
    val tag: String

    fun transcodeInto(data: ByteArray, output: OutputStream)
}

object NoTranscoding : Transcoding {
    override val tag = ".img"

    override fun transcodeInto(data: ByteArray, output: OutputStream) {
        output.write(data)
    }
}

class Compress(
    private val format: Bitmap.CompressFormat,
    private val resolution: Int,
    private val quality: Int,
) : Transcoding {
    override val tag = "_${resolution}x${quality}.${format.name.lowercase()}"

    override fun transcodeInto(data: ByteArray, output: OutputStream) {
        BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(data, 0, data.size, this)
            inSampleSize = calculateInSampleSize(resolution)
            inJustDecodeBounds = false
            val bitmap =
                requireNotNull(BitmapFactory.decodeByteArray(data, 0, data.size, this)) {
                    "Failed to decode bitmap"
                }
            bitmap.compress(format, quality, output)
            true
        }
    }

    private fun BitmapFactory.Options.calculateInSampleSize(size: Int): Int {
        var inSampleSize = 1
        val (height, width) = outHeight to outWidth

        if (height > size || width > size) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / inSampleSize) >= size && (halfWidth / inSampleSize) >= size) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
