package org.oxycblt.auxio.music.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory

// Class containing all relevant values for a song.
data class Song(
    val name: String?,
    val artist: String?,
    val album: String?,
    val genre: String?,
    val year: Int,
    val track: Int,
    val duration: Long,

    private val coverData: ByteArray?,
    val id: Long
) {
    var cover: Bitmap? = null

    init {
        coverData?.let { data ->
            // Decode the Album Cover ByteArray if not null.
            val options = BitmapFactory.Options()
            options.inMutable = true

            cover = BitmapFactory.decodeByteArray(
                data, 0, data.size, options
            )
        }
    }
}
