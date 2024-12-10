package org.oxycblt.musikr.cover

import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.extractor.metadata.flac.PictureFrame
import androidx.media3.extractor.metadata.id3.ApicFrame
import org.oxycblt.musikr.metadata.AudioMetadata
import javax.inject.Inject

interface CoverParser {
    suspend fun extract(metadata: AudioMetadata): ByteArray?
}

class CoverParserImpl @Inject constructor() : CoverParser {
    override suspend fun extract(metadata: AudioMetadata): ByteArray? {
        val exoPlayerMetadata = metadata.exoPlayerFormat?.metadata
        return exoPlayerMetadata?.let(::findCoverDataInMetadata)
            ?: metadata.mediaMetadataRetriever.embeddedPicture
    }

    private fun findCoverDataInMetadata(metadata: Metadata): ByteArray? {
        var fallbackPic: ByteArray? = null

        for (i in 0 until metadata.length()) {
            // We can only extract pictures from two tags with this method, ID3v2's APIC or
            // Vorbis picture comments.
            val pic: ByteArray?
            val type: Int

            when (val entry = metadata.get(i)) {
                is ApicFrame -> {
                    pic = entry.pictureData
                    type = entry.pictureType
                }

                is PictureFrame -> {
                    pic = entry.pictureData
                    type = entry.pictureType
                }

                else -> continue
            }

            if (type == MediaMetadata.PICTURE_TYPE_FRONT_COVER) {
                return pic
            } else if (fallbackPic == null) {
                fallbackPic = pic
            }
        }

        return fallbackPic
    }
}