package org.oxycblt.auxio.coil

import android.graphics.Bitmap
import coil.size.Size
import coil.size.pxOrElse
import coil.transform.Transformation
import kotlin.math.min

/**
 * A transformation that performs a center crop-style transformation on an image, however unlike
 * the actual ScaleType, this isn't affected by any hacks we do with ImageView itself.
 * @author OxygenCobalt
 */
class SquareFrameTransform : Transformation {
    override val cacheKey: String
        get() = "SquareFrameTransform"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val dstSize = min(input.width, input.height)
        val x = (input.width - dstSize) / 2
        val y = (input.height - dstSize) / 2

        val wantedWidth = size.width.pxOrElse { dstSize }
        val wantedHeight = size.height.pxOrElse { dstSize }

        val dst = Bitmap.createBitmap(input, x, y, dstSize, dstSize)

        if (dstSize != wantedWidth || dstSize != wantedHeight) {
            // Desired size differs from the cropped size, resize the bitmap.
            return Bitmap.createScaledBitmap(
                dst,
                wantedWidth,
                wantedHeight,
                true
            )
        }

        return dst
    }

    companion object {
        val INSTANCE = SquareFrameTransform()
    }
}
