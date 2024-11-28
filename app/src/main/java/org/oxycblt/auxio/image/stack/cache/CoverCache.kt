package org.oxycblt.auxio.image.stack.cache

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.oxycblt.auxio.music.Song
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.inject.Inject

interface CoverCache {
    suspend fun read(song: Song): InputStream?
    suspend fun write(song: Song, inputStream: InputStream): Boolean
}


class CoverCacheImpl @Inject constructor(
    private val storedCoversDao: StoredCoversDao,
    private val appFiles: AppFiles,
    private val perceptualHash: PerceptualHash
) : CoverCache {

    override suspend fun read(song: Song): InputStream? {
        val perceptualHash = storedCoversDao.getCoverFile(song.uid, song.lastModified)
            ?: return null

        return appFiles.read(fileName(perceptualHash))
    }

    override suspend fun write(song: Song, inputStream: InputStream): Boolean = withContext(Dispatchers.IO) {
        val bitmap = BitmapFactory.decodeStream(inputStream)
        val perceptualHash = perceptualHash.hash(bitmap)

        // Compress bitmap down to webp into another inputstream
        val compressedStream = ByteArrayOutputStream().use { outputStream ->
            bitmap.compress(COVER_CACHE_FORMAT, 80, outputStream)
            ByteArrayInputStream(outputStream.toByteArray())
        }

        val writeSuccess = appFiles.write(fileName(perceptualHash), compressedStream)

        if (writeSuccess) {
            storedCoversDao.setCoverFile(
                StoredCover(
                    uid = song.uid,
                    lastModified = song.lastModified,
                    perceptualHash = perceptualHash
                )
            )
        }

        writeSuccess
    }

    private fun fileName(perceptualHash: String) = "cover_$perceptualHash.png"

    private companion object {
        @Suppress("DEPRECATION")
        val COVER_CACHE_FORMAT = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            Bitmap.CompressFormat.WEBP
        }
    }
}