/*
 * Copyright (c) 2022 Auxio Project
 * Fs.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.fs

import android.content.Context
import android.media.MediaFormat
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.webkit.MimeTypeMap
import java.io.File
import org.oxycblt.auxio.R

/**
 * A full absolute path to a file. Only intended for display purposes. For accessing files, URIs are
 * preferred in all cases due to scoped storage limitations.
 *
 * @param name The name of the file.
 * @param parent The parent [Directory] of the file.
 * @author Alexander Capehart (OxygenCobalt)
 */
data class Path(val name: String, val parent: Directory)

/**
 * A volume-aware relative path to a directory.
 *
 * @param volume The [StorageVolume] that the [Directory] is contained in.
 * @param relativePath The relative path from within the [StorageVolume] to the [Directory].
 * @author Alexander Capehart (OxygenCobalt)
 */
class Directory private constructor(val volume: StorageVolume, val relativePath: String) {
    /**
     * Resolve the [Directory] instance into a human-readable path name.
     *
     * @param context [Context] required to obtain volume descriptions.
     * @return A human-readable path.
     * @see StorageVolume.getDescription
     */
    fun resolveName(context: Context) =
        context.getString(R.string.fmt_path, volume.getDescriptionCompat(context), relativePath)

    /**
     * Converts this [Directory] instance into an opaque document tree path. This is a huge
     * violation of the document tree URI contract, but it's also the only one can sensibly work
     * with these uris in the UI, and it doesn't exactly matter since we never write or read to
     * directory.
     *
     * @return A URI [String] abiding by the document tree specification, or null if the [Directory]
     *   is not valid.
     */
    fun toDocumentTreeUri() =
        // Document tree URIs consist of a prefixed volume name followed by a relative path.
        if (volume.isInternalCompat) {
            // The primary storage has a volume prefix of "primary", regardless
            // of if it's internal or not.
            "$DOCUMENT_URI_PRIMARY_NAME:$relativePath"
        } else {
            // Removable storage has a volume prefix of it's UUID.
            volume.uuidCompat?.let { uuid -> "$uuid:$relativePath" }
        }

    override fun hashCode(): Int {
        var result = volume.hashCode()
        result = 31 * result + relativePath.hashCode()
        return result
    }

    override fun equals(other: Any?) =
        other is Directory && other.volume == volume && other.relativePath == relativePath

    companion object {
        /** The name given to the internal volume when in a document tree URI. */
        private const val DOCUMENT_URI_PRIMARY_NAME = "primary"

        /**
         * Create a new directory instance from the given components.
         *
         * @param volume The [StorageVolume] that the [Directory] is contained in.
         * @param relativePath The relative path from within the [StorageVolume] to the [Directory].
         *   Will be stripped of any trailing separators for a consistent internal representation.
         * @return A new [Directory] created from the components.
         */
        fun from(volume: StorageVolume, relativePath: String) =
            Directory(
                volume, relativePath.removePrefix(File.separator).removeSuffix(File.separator))

        /**
         * Create a new directory from a document tree URI. This is a huge violation of the document
         * tree URI contract, but it's also the only one can sensibly work with these uris in the
         * UI, and it doesn't exactly matter since we never write or read directory.
         *
         * @param storageManager [StorageManager] in order to obtain the [StorageVolume] specified
         *   in the given URI.
         * @param uri The URI string to parse into a [Directory].
         * @return A new [Directory] parsed from the URI, or null if the URI is not valid.
         */
        fun fromDocumentTreeUri(storageManager: StorageManager, uri: String): Directory? {
            // Document tree URIs consist of a prefixed volume name followed by a relative path,
            // delimited with a colon.
            val split = uri.split(File.pathSeparator, limit = 2)
            val volume =
                when (split[0]) {
                    // The primary storage has a volume prefix of "primary", regardless
                    // of if it's internal or not.
                    DOCUMENT_URI_PRIMARY_NAME -> storageManager.primaryStorageVolumeCompat
                    // Removable storage has a volume prefix of it's UUID, try to find it
                    // within StorageManager's volume list.
                    else -> storageManager.storageVolumesCompat.find { it.uuidCompat == split[0] }
                }
            val relativePath = split.getOrNull(1)
            return from(volume ?: return null, relativePath ?: return null)
        }
    }
}

/**
 * Represents the configuration for specific directories to filter to/from when loading music.
 *
 * @param dirs A list of [Directory] instances. How these are interpreted depends on [shouldInclude]
 * @param shouldInclude True if the library should only load from the [Directory] instances, false
 *   if the library should not load from the [Directory] instances.
 * @author Alexander Capehart (OxygenCobalt)
 */
data class MusicDirectories(val dirs: List<Directory>, val shouldInclude: Boolean)

/**
 * A mime type of a file. Only intended for display.
 *
 * @param fromExtension The mime type obtained by analyzing the file extension.
 * @param fromFormat The mime type obtained by analyzing the file format. Null if could not be
 *   obtained.
 * @author Alexander Capehart (OxygenCobalt)
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
