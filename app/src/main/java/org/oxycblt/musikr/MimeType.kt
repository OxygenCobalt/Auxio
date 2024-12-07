package org.oxycblt.musikr

import android.content.Context
import android.media.MediaFormat
import android.webkit.MimeTypeMap
import org.oxycblt.auxio.R

/**
 * A mime type of a file. Only intended for display.
 *
 * @param fromExtension The mime type obtained by analyzing the file extension.
 * @param fromFormat The mime type obtained by analyzing the file format. Null if could not be
 *   obtained.
 * @author Alexander Capehart (OxygenCobalt)
 *
 * TODO: Get around to simplifying this
 */
data class MimeType(val fromExtension: String, val fromFormat: String?) {
    /**
     * Resolve the mime type into a human-readable format name, such as "Ogg Vorbis".
     *
     * @param context [Context] required to obtain human-readable strings.
     * @return A human-readable name for this mime type. Will first try [fromFormat], then falling
     *   back to [fromExtension], and then null if that fails.
     */
    fun resolveName(context: Context): String? {
        // We try our best to produce a more readable name for the common audio formats.
        val formatName =
            when (fromFormat) {
                // We start with the extracted mime types, as they are more consistent. Note that
                // we do not include container formats at all with these names. It is only the
                // inner codec that we bother with.
                MediaFormat.MIMETYPE_AUDIO_MPEG -> R.string.cdc_mp3
                MediaFormat.MIMETYPE_AUDIO_AAC -> R.string.cdc_aac
                MediaFormat.MIMETYPE_AUDIO_VORBIS -> R.string.cdc_vorbis
                MediaFormat.MIMETYPE_AUDIO_OPUS -> R.string.cdc_opus
                MediaFormat.MIMETYPE_AUDIO_FLAC -> R.string.cdc_flac
                // TODO: Add ALAC to this as soon as I can stop using MediaFormat for
                //  extracting metadata and just use ExoPlayer.
                // We don't give a name to more unpopular formats.
                else -> -1
            }

        if (formatName > -1) {
            return context.getString(formatName)
        }

        // Fall back to the file extension in the case that we have no mime type or
        // a useless "audio/raw" mime type. Here:
        // - We return names for container formats instead of the inner format, as we
        // cannot parse the file.
        // - We are at the mercy of the Android OS, hence we check for every possible mime
        // type for a particular format according to Wikipedia.
        val extensionName =
            when (fromExtension) {
                "audio/mpeg",
                "audio/mp3" -> R.string.cdc_mp3
                "audio/mp4",
                "audio/mp4a-latm",
                "audio/mpeg4-generic" -> R.string.cdc_mp4
                "audio/aac",
                "audio/aacp",
                "audio/3gpp",
                "audio/3gpp2" -> R.string.cdc_aac
                "audio/ogg",
                "application/ogg",
                "application/x-ogg" -> R.string.cdc_ogg
                "audio/flac" -> R.string.cdc_flac
                "audio/wav",
                "audio/x-wav",
                "audio/wave",
                "audio/vnd.wave" -> R.string.cdc_wav
                "audio/x-matroska" -> R.string.cdc_mka
                else -> -1
            }

        return if (extensionName > -1) {
            context.getString(extensionName)
        } else {
            // Fall back to the extension if we can't find a special name for this format.
            MimeTypeMap.getSingleton().getExtensionFromMimeType(fromExtension)?.uppercase()
        }
    }
}