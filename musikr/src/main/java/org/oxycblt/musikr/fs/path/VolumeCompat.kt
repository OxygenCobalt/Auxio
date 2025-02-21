/*
 * Copyright (c) 2024 Auxio Project
 * VolumeCompat.kt is part of Auxio.
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
 
package org.oxycblt.musikr.fs.path

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.provider.MediaStore
import java.lang.reflect.Method
import org.oxycblt.musikr.util.lazyReflectedMethod

// Largely derived from Material Files: https://github.com/zhanghai/MaterialFiles

/**
 * Provides the analogous method to [StorageVolume.getDirectory] method that is usable from API 21
 * to API 23, in which the [StorageVolume] API was hidden and differed greatly.
 *
 * @see StorageVolume.getDirectory
 */
@Suppress("NewApi")
private val svApi21GetPathMethod: Method by lazyReflectedMethod(StorageVolume::class, "getPath")

/**
 * The list of [StorageVolume]s currently recognized by [StorageManager], in a version-compatible
 * manner.
 *
 * @see StorageManager.getStorageVolumes
 */
internal val StorageManager.storageVolumesCompat: List<StorageVolume>
    get() = storageVolumes.toList()

/**
 * The the absolute path to this [StorageVolume]'s directory within the file-system, in a
 * version-compatible manner. Will be null if the [StorageVolume] cannot be read.
 *
 * @see StorageVolume.getDirectory
 */
internal val StorageVolume.directoryCompat: String?
    @SuppressLint("NewApi")
    get() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            directory?.absolutePath
        } else {
            // Replicate API: Analogous method if mounted, null if not
            when (stateCompat) {
                Environment.MEDIA_MOUNTED,
                Environment.MEDIA_MOUNTED_READ_ONLY -> svApi21GetPathMethod.invoke(this) as String
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
internal fun StorageVolume.getDescriptionCompat(context: Context): String = getDescription(context)

/**
 * If this [StorageVolume] is considered the "Primary" volume where the Android System is kept. May
 * still be a removable volume.
 *
 * @see StorageVolume.isPrimary
 */
internal val StorageVolume.isPrimaryCompat: Boolean
    @SuppressLint("NewApi") get() = isPrimary

/**
 * If this storage is "emulated", i.e intrinsic to the device, obtained in a version compatible
 * manner.
 *
 * @see StorageVolume.isEmulated
 */
internal val StorageVolume.isEmulatedCompat: Boolean
    @SuppressLint("NewApi") get() = isEmulated

/**
 * If this [StorageVolume] represents the "Internal Shared Storage" volume, also known as "primary"
 * to [MediaStore] and Document [Uri]s, obtained in a version compatible manner.
 */
internal val StorageVolume.isInternalCompat: Boolean
    // Must contain the android system AND be an emulated drive, as non-emulated system
    // volumes use their UUID instead of primary in MediaStore/Document URIs.
    get() = isPrimaryCompat && isEmulatedCompat

/**
 * The unique identifier for this [StorageVolume], obtained in a version compatible manner. Can be
 * null.
 *
 * @see StorageVolume.getUuid
 */
internal val StorageVolume.uuidCompat: String?
    @SuppressLint("NewApi") get() = uuid

/**
 * The current state of this [StorageVolume], such as "mounted" or "read-only", obtained in a
 * version compatible manner.
 *
 * @see StorageVolume.getState
 */
internal val StorageVolume.stateCompat: String
    @SuppressLint("NewApi") get() = state

/**
 * Returns the name of this volume that can be used to interact with [MediaStore], in a version
 * compatible manner. Will be null if the volume is not scanned by [MediaStore].
 *
 * @see StorageVolume.getMediaStoreVolumeName
 */
internal val StorageVolume.mediaStoreVolumeNameCompat: String?
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
