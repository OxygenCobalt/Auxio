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
import javax.inject.Inject
import org.oxycblt.auxio.R

/**
 * An abstraction of an android file system path, including the volume and relative path.
 *
 * @param volume The volume that the path is on.
 * @param components The components of the path of the file, relative to the root of the volume.
 */
data class Path(
    val volume: Volume,
    val components: Components,
) {
    /** The name of the file/directory. */
    val name: String?
        get() = components.name

    /** The parent directory of the path, or itself if it's the root path. */
    val directory: Path
        get() = Path(volume, components.parent())

    /**
     * Transforms this [Path] into a "file" of the given name that's within the "directory"
     * represented by the current path. Ex. "/storage/emulated/0/Music" ->
     * "/storage/emulated/0/Music/file.mp3"
     *
     * @param fileName The name of the file to append to the path.
     * @return The new [Path] instance.
     */
    fun file(fileName: String) = Path(volume, components.child(fileName))

    /**
     * Resolves the [Path] in a human-readable format.
     *
     * @param context [Context] required to obtain human-readable strings.
     */
    fun resolve(context: Context) = "${volume.resolveName(context)}/$components"
}

sealed interface Volume {
    /** The name of the volume as it appears in MediaStore. */
    val mediaStoreName: String?

    /**
     * The components of the path to the volume, relative from the system root. Should not be used
     * except for compatibility purposes.
     */
    val components: Components?

    /** Resolves the name of the volume in a human-readable format. */
    fun resolveName(context: Context): String

    /** A volume representing the device's internal storage. */
    interface Internal : Volume

    /** A volume representing an external storage device, identified by a UUID. */
    interface External : Volume {
        /** The UUID of the volume. */
        val id: String?
    }
}

/**
 * The components of a path. This allows the path to be manipulated without having tp handle
 * separator parsing.
 *
 * @param components The components of the path.
 */
@JvmInline
value class Components private constructor(val components: List<String>) {
    /** The name of the file/directory. */
    val name: String?
        get() = components.lastOrNull()

    override fun toString() = unixString

    /** Formats these components using the unix file separator (/) */
    val unixString: String
        get() = components.joinToString(File.separator)

    /** Formats these components using the windows file separator (\). */
    val windowsString: String
        get() = components.joinToString("\\")

    /**
     * Returns a new [Components] instance with the last element of the path removed as a "parent"
     * element of the original instance.
     *
     * @return The new [Components] instance, or the original instance if it's the root path.
     */
    fun parent() = Components(components.dropLast(1))

    /**
     * Returns a new [Components] instance with the given name appended to the end of the path as a
     * "child" element of the original instance.
     *
     * @param name The name of the file/directory to append to the path.
     */
    fun child(name: String) =
        if (name.isNotEmpty()) {
            Components(components + name.trimSlashes())
        } else {
            this
        }

    /**
     * Removes the first [n] elements of the path, effectively resulting in a path that is n levels
     * deep.
     *
     * @param n The number of elements to remove.
     * @return The new [Components] instance.
     */
    fun depth(n: Int) = Components(components.drop(n))

    /**
     * Concatenates this [Components] instance with another.
     *
     * @param other The [Components] instance to concatenate with.
     * @return The new [Components] instance.
     */
    fun child(other: Components) = Components(components + other.components)

    /**
     * Returns the given [Components] has a prefix equal to this [Components] instance. Effectively,
     * as if the given [Components] instance was a child of this [Components] instance.
     */
    fun contains(other: Components): Boolean {
        if (other.components.size < components.size) {
            return false
        }

        return components == other.components.take(components.size)
    }

    fun containing(other: Components) = Components(other.components.drop(components.size))

    companion object {
        /**
         * Parses a path string into a [Components] instance by the unix path separator (/).
         *
         * @param path The path string to parse.
         * @return The [Components] instance.
         */
        fun parseUnix(path: String) =
            Components(path.trimSlashes().split(File.separatorChar).filter { it.isNotEmpty() })

        /**
         * Parses a path string into a [Components] instance by the windows path separator.
         *
         * @param path The path string to parse.
         * @return The [Components] instance.
         */
        fun parseWindows(path: String) =
            Components(path.trimSlashes().split('\\').filter { it.isNotEmpty() })

        private fun String.trimSlashes() = trimStart(File.separatorChar).trimEnd(File.separatorChar)
    }
}

/** A wrapper around [StorageManager] that provides instances of the [Volume] interface. */
interface VolumeManager {
    /**
     * The internal storage volume of the device.
     *
     * @see StorageManager.getPrimaryStorageVolume
     */
    fun getInternalVolume(): Volume.Internal

    /**
     * The list of [Volume]s currently recognized by [StorageManager].
     *
     * @see StorageManager.getStorageVolumes
     */
    fun getVolumes(): List<Volume>
}

class VolumeManagerImpl @Inject constructor(private val storageManager: StorageManager) :
    VolumeManager {
    override fun getInternalVolume(): Volume.Internal =
        InternalVolumeImpl(storageManager.primaryStorageVolume)

    override fun getVolumes() =
        storageManager.storageVolumesCompat.map {
            if (it.isInternalCompat) {
                InternalVolumeImpl(it)
            } else {
                ExternalVolumeImpl(it)
            }
        }

    private data class InternalVolumeImpl(val storageVolume: StorageVolume) : Volume.Internal {
        override val mediaStoreName
            get() = storageVolume.mediaStoreVolumeNameCompat

        override val components
            get() = storageVolume.directoryCompat?.let(Components::parseUnix)

        override fun resolveName(context: Context) = storageVolume.getDescriptionCompat(context)
    }

    private data class ExternalVolumeImpl(val storageVolume: StorageVolume) : Volume.External {
        override val id
            get() = storageVolume.uuidCompat

        override val mediaStoreName
            get() = storageVolume.mediaStoreVolumeNameCompat

        override val components
            get() = storageVolume.directoryCompat?.let(Components::parseUnix)

        override fun resolveName(context: Context) = storageVolume.getDescriptionCompat(context)
    }
}

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
