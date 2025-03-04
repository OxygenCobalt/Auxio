package org.oxycblt.musikr.covers.internal

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.OutputStream

abstract class CoverFormat {
    internal abstract val extension: String

    internal abstract fun transcodeInto(data: ByteArray, output: OutputStream): Boolean

    companion object {
        fun jpeg(params: CoverParams): CoverFormat =
            CompressingCoverFormat("jpg", params, Bitmap.CompressFormat.JPEG)

        fun asIs(): CoverFormat = AsIsCoverFormat()
    }
}

private class CompressingCoverFormat(
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

private class AsIsCoverFormat : CoverFormat() {
    override val extension: String = "bin"

    override fun transcodeInto(data: ByteArray, output: OutputStream): Boolean {
        return try {
            output.write(data)
            true
        } catch (e: Exception) {
            false
        }
    }
}
