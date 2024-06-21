/*
 * Copyright (c) 2024 Auxio Project
 * DHash.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image.extractor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import java.math.BigInteger

@Suppress("UNUSED")
fun Bitmap.dHash(hashSize: Int = 16): String {
    // Step 1: Resize the bitmap to a fixed size
    val resizedBitmap = Bitmap.createScaledBitmap(this, hashSize + 1, hashSize, true)

    // Step 2: Convert the bitmap to grayscale
    val grayBitmap =
        Bitmap.createBitmap(resizedBitmap.width, resizedBitmap.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(grayBitmap)
    val paint = Paint()
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f)
    val filter = ColorMatrixColorFilter(colorMatrix)
    paint.colorFilter = filter
    canvas.drawBitmap(resizedBitmap, 0f, 0f, paint)

    // Step 3: Compute the difference between adjacent pixels
    var hash = BigInteger.valueOf(0)
    val one = BigInteger.valueOf(1)
    for (y in 0 until hashSize) {
        for (x in 0 until hashSize) {
            val pixel1 = grayBitmap.getPixel(x, y)
            val pixel2 = grayBitmap.getPixel(x + 1, y)
            val diff = Color.red(pixel1) - Color.red(pixel2)
            if (diff > 0) {
                hash = hash.or(one.shl(y * hashSize + x))
            }
        }
    }

    return hash.toString(16)
}
