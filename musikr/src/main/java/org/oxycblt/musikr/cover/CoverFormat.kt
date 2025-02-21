/*
 * Copyright (c) 2024 Auxio Project
 * CoverFormat.kt is part of Auxio.
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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.OutputStream

abstract class CoverFormat {
    internal abstract val extension: String

    internal abstract fun transcodeInto(data: ByteArray, output: OutputStream): Boolean

    companion object {
        fun jpeg(params: CoverParams): CoverFormat =
            CoverFormatImpl("jpg", params, Bitmap.CompressFormat.JPEG)
    }
}

private class CoverFormatImpl(
    override val extension: String,
    private val params: CoverParams,
    private val format: Bitmap.CompressFormat,
) : CoverFormat() {
    override fun transcodeInto(data: ByteArray, output: OutputStream) =
        BitmapFactory.Options().run {
            inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(data, 0, data.size, this)
            inSampleSize = calculateInSampleSize(params.resolution)
            inJustDecodeBounds = false
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size, this) ?: return@run false
            bitmap.compress(format, params.quality, output)
            true
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
