/*
 * Copyright (c) 2023 Auxio Project
 * SquareCropTransformation.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image.coil

import android.graphics.Bitmap
import coil3.size.Size
import coil3.size.pxOrElse
import coil3.transform.Transformation
import kotlin.math.min

/**
 * A [Transformation] that performs a center crop-style transformation on an image. Allowing this
 * behavior to be intrinsic without any view configuration.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
class SquareCropTransformation : Transformation() {
    override val cacheKey: String
        get() = "SquareCropTransformation"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        // Find the smaller dimension and then take a center portion of the image that
        // has that size.
        val dstSize = min(input.width, input.height)
        val x = (input.width - dstSize) / 2
        val y = (input.height - dstSize) / 2
        val dst = Bitmap.createBitmap(input, x, y, dstSize, dstSize)

        val desiredWidth = size.width.pxOrElse { dstSize }
        val desiredHeight = size.height.pxOrElse { dstSize }
        if (dstSize != desiredWidth || dstSize != desiredHeight) {
            // Image is not the desired size, upscale it.
            return Bitmap.createScaledBitmap(dst, desiredWidth, desiredHeight, true)
        }
        return dst
    }

    companion object {
        /** A re-usable instance. */
        val INSTANCE = SquareCropTransformation()
    }
}
