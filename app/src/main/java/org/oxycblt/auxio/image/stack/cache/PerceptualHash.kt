package org.oxycblt.auxio.image.stack.cache

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import java.math.BigInteger

interface PerceptualHash {
    fun hash(bitmap: Bitmap): String
}

class PerceptualHashImpl : PerceptualHash {
    override fun hash(bitmap: Bitmap): String {
        val hashSize = 16
        // Step 1: Resize the bitmap to a fixed size
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, hashSize + 1, hashSize, true)

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

}