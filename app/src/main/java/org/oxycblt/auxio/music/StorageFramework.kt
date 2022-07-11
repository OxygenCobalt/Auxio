/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.music

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.google.android.exoplayer2.util.MimeTypes
import java.io.File
import java.lang.reflect.Method
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.lazyReflectedMethod
import org.oxycblt.auxio.util.logEOrThrow

/** A path to a file. [name] is the stripped file name, [parent] is the parent path. */
data class Path(val name: String, val parent: Directory)

/**
 * A path to a directory. [volume] is the volume the directory resides in, and [relativePath] is the
 * path from the volume's root to the directory itself.
 */
data class Directory(val volume: StorageVolume, val relativePath: String) {
    init {
        if (relativePath.startsWith(File.separatorChar) ||
            relativePath.endsWith(File.separatorChar)) {
            logEOrThrow("Path was formatted with trailing separators")
        }
    }

    fun resolveName(context: Context) =
        context.getString(R.string.fmt_path, volume.getDescriptionCompat(context), relativePath)

    /** Converts this dir into an opaque document URI in the form of VOLUME:PATH. */
    fun toDocumentUri() =
        // "primary" actually corresponds to the internal storage, not the primary volume.
        // Removable storage is represented with the UUID.
        if (volume.isInternalCompat) {
            "${DOCUMENT_URI_PRIMARY_NAME}:${relativePath}"
        } else {
            volume.uuidCompat?.let { uuid -> "${uuid}:${relativePath}" }
        }

    companion object {
        private const val DOCUMENT_URI_PRIMARY_NAME = "primary"

        /**
         * Converts an opaque document uri in the form of VOLUME:PATH into a [Directory]. This is a
         * flagrant violation of the API convention, but since we never really write to the URI I
         * really doubt it matters.
         */
        fun fromDocumentUri(storageManager: StorageManager, uri: String): Directory? {
            val split = uri.split(File.pathSeparator, limit = 2)

            val volume =
                when (split[0]) {
                    DOCUMENT_URI_PRIMARY_NAME -> storageManager.primaryStorageVolume
                    else -> storageManager.storageVolumesCompat.find { it.uuidCompat == split[0] }
                }

            val relativePath = split.getOrNull(1)

            return Directory(volume ?: return null, relativePath ?: return null)
        }
    }
}

private val SM_API21_GET_VOLUME_LIST_METHOD: Method by
    lazyReflectedMethod(StorageManager::class, "getVolumeList")
private val SV_API21_GET_PATH_METHOD: Method by lazyReflectedMethod(StorageVolume::class, "getPath")

/**
 * A list of recognized volumes, retrieved in a compatible manner. Note that these volumes may be
 * mounted or unmounted.
 */
val StorageManager.storageVolumesCompat: List<StorageVolume>
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            storageVolumes.toList()
        } else {
            @Suppress("UNCHECKED_CAST")
            (SM_API21_GET_VOLUME_LIST_METHOD.invoke(this) as Array<StorageVolume>).toList()
        }

/** Returns the absolute path to a particular volume in a compatible manner. */
val StorageVolume.directoryCompat: String?
    @SuppressLint("NewApi")
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            directory?.absolutePath
        } else {
            // Replicate API: getPath if mounted, null if not
            when (stateCompat) {
                Environment.MEDIA_MOUNTED,
                Environment.MEDIA_MOUNTED_READ_ONLY ->
                    SV_API21_GET_PATH_METHOD.invoke(this) as String
                else -> null
            }
        }

/** Get the readable description of the volume in a compatible manner. */
@SuppressLint("NewApi")
fun StorageVolume.getDescriptionCompat(context: Context): String = getDescription(context)

/** If this volume is the primary volume. May still be removable storage. */
val StorageVolume.isPrimaryCompat: Boolean
    @SuppressLint("NewApi") get() = isPrimary

/** If this volume is emulated. */
val StorageVolume.isEmulatedCompat: Boolean
    @SuppressLint("NewApi") get() = isEmulated

/**
 * If this volume corresponds to "Internal shared storage", represented in document URIs as
 * "primary". These volumes are primary volumes, but are also non-removable and emulated.
 */
val StorageVolume.isInternalCompat: Boolean
    get() = isPrimaryCompat && isEmulatedCompat

/** Returns the UUID of the volume in a compatible manner. */
val StorageVolume.uuidCompat: String?
    @SuppressLint("NewApi") get() = uuid

/*
 * Returns the state of the volume in a compatible manner.
 */
val StorageVolume.stateCompat: String
    @SuppressLint("NewApi") get() = state

/**
 * Returns the name of this volume as it is used in [MediaStore]. This will be
 * [MediaStore.VOLUME_EXTERNAL_PRIMARY] if it is the primary volume, and the lowercase UUID of the
 * volume otherwise.
 */
val StorageVolume.mediaStoreVolumeNameCompat: String?
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mediaStoreVolumeName
        } else {
            // Replicate API: primary_external if primary storage, lowercase uuid otherwise
            if (isPrimaryCompat) {
                MediaStore.VOLUME_EXTERNAL_PRIMARY
            } else {
                uuidCompat?.lowercase()
            }
        }

/**
 * Represents a mime type as it is loaded by Auxio. [fromExtension] is based on the file extension
 * should always exist, while [fromFormat] is based on the file itself and may not be available.
 * @author OxygenCobalt
 */
data class MimeType(val fromExtension: String, val fromFormat: String?) {
    fun resolveName(context: Context): String {
        // We try our best to produce a more readable name for the common audio formats.
        val formatName =
            when (fromFormat) {
                // We start with the extracted mime types, as they are more consistent. Note that
                // we do not include container formats at all with these names. It is only the
                // inner codec that we show.
                MimeTypes.AUDIO_MPEG,
                MimeTypes.AUDIO_MPEG_L1,
                MimeTypes.AUDIO_MPEG_L2 -> R.string.cdc_mp3
                MimeTypes.AUDIO_AAC -> R.string.cdc_aac
                MimeTypes.AUDIO_VORBIS -> R.string.cdc_vorbis
                MimeTypes.AUDIO_OPUS -> R.string.cdc_opus
                MimeTypes.AUDIO_FLAC -> R.string.cdc_flac
                MimeTypes.AUDIO_WAV -> R.string.cdc_wav

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
        // type for a particular format.
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
                ?: context.getString(R.string.def_codec)
        }
    }
}
