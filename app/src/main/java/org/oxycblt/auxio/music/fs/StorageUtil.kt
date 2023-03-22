/*
 * Copyright (c) 2022 Auxio Project
 * StorageUtil.kt is part of Auxio.
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

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.provider.MediaStore
import java.lang.reflect.Method
import org.oxycblt.auxio.util.lazyReflectedMethod

// --- MEDIASTORE UTILITIES ---

/**
 * Get a content resolver that will not mangle MediaStore queries on certain devices. See
 * https://github.com/OxygenCobalt/Auxio/issues/50 for more info.
 */
val Context.contentResolverSafe: ContentResolver
    get() = applicationContext.contentResolver

/**
 * A shortcut for querying the [ContentResolver] database.
 *
 * @param uri The [Uri] of content to retrieve.
 * @param projection A list of SQL columns to query from the database.
 * @param selector A SQL selection statement to filter results. Spaces where arguments should be
 *   filled in are represented with a "?".
 * @param args The arguments used for the selector.
 * @return A [Cursor] of the queried values, organized by the column projection.
 * @throws IllegalStateException If the [ContentResolver] did not return the queried [Cursor].
 * @see ContentResolver.query
 */
fun ContentResolver.safeQuery(
    uri: Uri,
    projection: Array<out String>,
    selector: String? = null,
    args: Array<String>? = null
) = requireNotNull(query(uri, projection, selector, args, null)) { "ContentResolver query failed" }

/**
 * A shortcut for [safeQuery] with [use] applied, automatically cleaning up the [Cursor]'s resources
 * when no longer used.
 *
 * @param uri The [Uri] of content to retrieve.
 * @param projection A list of SQL columns to query from the database.
 * @param selector A SQL selection statement to filter results. Spaces where arguments should be
 *   filled in are represented with a "?".
 * @param args The arguments used for the selector.
 * @param block The block of code to run with the queried [Cursor]. Will not be ran if the [Cursor]
 *   is empty.
 * @throws IllegalStateException If the [ContentResolver] did not return the queried [Cursor].
 * @see ContentResolver.query
 */
inline fun <reified R> ContentResolver.useQuery(
    uri: Uri,
    projection: Array<out String>,
    selector: String? = null,
    args: Array<String>? = null,
    block: (Cursor) -> R
) = safeQuery(uri, projection, selector, args).use(block)

/** Album art [MediaStore] database is not a built-in constant, have to define it ourselves. */
private val EXTERNAL_COVERS_URI = Uri.parse("content://media/external/audio/albumart")

/**
 * Convert a [MediaStore] Song ID into a [Uri] to it's audio file.
 *
 * @return An external storage audio file [Uri]. May not exist.
 * @see ContentUris.withAppendedId
 * @see MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
 */
fun Long.toAudioUri() =
    ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, this)

/**
 * Convert a [MediaStore] Album ID into a [Uri] to it's system-provided album cover. This cover will
 * be fast to load, but will be lower quality.
 *
 * @return An external storage image [Uri]. May not exist.
 * @see ContentUris.withAppendedId
 */
fun Long.toCoverUri() = ContentUris.withAppendedId(EXTERNAL_COVERS_URI, this)

// --- STORAGEMANAGER UTILITIES ---
// Largely derived from Material Files: https://github.com/zhanghai/MaterialFiles

/**
 * Provides the analogous method to [StorageManager.getStorageVolumes] method that is usable from
 * API 21 to API 23, in which the [StorageManager] API was hidden and differed greatly.
 *
 * @see StorageManager.getStorageVolumes
 */
@Suppress("NewApi")
private val SM_API21_GET_VOLUME_LIST_METHOD: Method by
    lazyReflectedMethod(StorageManager::class, "getVolumeList")

/**
 * Provides the analogous method to [StorageVolume.getDirectory] method that is usable from API 21
 * to API 23, in which the [StorageVolume] API was hidden and differed greatly.
 *
 * @see StorageVolume.getDirectory
 */
@Suppress("NewApi")
private val SV_API21_GET_PATH_METHOD: Method by lazyReflectedMethod(StorageVolume::class, "getPath")

/**
 * The [StorageVolume] considered the "primary" volume by the system, obtained in a
 * version-compatible manner.
 *
 * @see StorageManager.getPrimaryStorageVolume
 * @see StorageVolume.isPrimary
 */
val StorageManager.primaryStorageVolumeCompat: StorageVolume
    @Suppress("NewApi") get() = primaryStorageVolume

/**
 * The list of [StorageVolume]s currently recognized by [StorageManager], in a version-compatible
 * manner.
 *
 * @see StorageManager.getStorageVolumes
 */
val StorageManager.storageVolumesCompat: List<StorageVolume>
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            storageVolumes.toList()
        } else {
            @Suppress("UNCHECKED_CAST")
            (SM_API21_GET_VOLUME_LIST_METHOD.invoke(this) as Array<StorageVolume>).toList()
        }

/**
 * The the absolute path to this [StorageVolume]'s directory within the file-system, in a
 * version-compatible manner. Will be null if the [StorageVolume] cannot be read.
 *
 * @see StorageVolume.getDirectory
 */
val StorageVolume.directoryCompat: String?
    @SuppressLint("NewApi")
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            directory?.absolutePath
        } else {
            // Replicate API: Analogous method if mounted, null if not
            when (stateCompat) {
                Environment.MEDIA_MOUNTED,
                Environment.MEDIA_MOUNTED_READ_ONLY ->
                    SV_API21_GET_PATH_METHOD.invoke(this) as String
                else -> null
            }
        }

/**
 * Get the human-readable description of this volume, such as "Internal Shared Storage".
 *
 * @param context [Context] required to obtain human-readable string resources.
 * @return A human-readable name for this volume.
 */
@SuppressLint("NewApi")
fun StorageVolume.getDescriptionCompat(context: Context): String = getDescription(context)

/**
 * If this [StorageVolume] is considered the "Primary" volume where the Android System is kept. May
 * still be a removable volume.
 *
 * @see StorageVolume.isPrimary
 */
val StorageVolume.isPrimaryCompat: Boolean
    @SuppressLint("NewApi") get() = isPrimary

/**
 * If this storage is "emulated", i.e intrinsic to the device, obtained in a version compatible
 * manner.
 *
 * @see StorageVolume.isEmulated
 */
val StorageVolume.isEmulatedCompat: Boolean
    @SuppressLint("NewApi") get() = isEmulated

/**
 * If this [StorageVolume] represents the "Internal Shared Storage" volume, also known as "primary"
 * to [MediaStore] and Document [Uri]s, obtained in a version compatible manner.
 */
val StorageVolume.isInternalCompat: Boolean
    // Must contain the android system AND be an emulated drive, as non-emulated system
    // volumes use their UUID instead of primary in MediaStore/Document URIs.
    get() = isPrimaryCompat && isEmulatedCompat

/**
 * The unique identifier for this [StorageVolume], obtained in a version compatible manner. Can be
 * null.
 *
 * @see StorageVolume.getUuid
 */
val StorageVolume.uuidCompat: String?
    @SuppressLint("NewApi") get() = uuid

/**
 * The current state of this [StorageVolume], such as "mounted" or "read-only", obtained in a
 * version compatible manner.
 *
 * @see StorageVolume.getState
 */
val StorageVolume.stateCompat: String
    @SuppressLint("NewApi") get() = state

/**
 * Returns the name of this volume that can be used to interact with [MediaStore], in a version
 * compatible manner. Will be null if the volume is not scanned by [MediaStore].
 *
 * @see StorageVolume.getMediaStoreVolumeName
 */
val StorageVolume.mediaStoreVolumeNameCompat: String?
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mediaStoreVolumeName
        } else {
            // Replicate API: primary_external if primary storage, lowercase uuid otherwise
            if (isPrimaryCompat) {
                // "primary_external" is used in all versions that Auxio supports, is safe to use.
                @Suppress("NewApi") MediaStore.VOLUME_EXTERNAL_PRIMARY
            } else {
                uuidCompat?.lowercase()
            }
        }
