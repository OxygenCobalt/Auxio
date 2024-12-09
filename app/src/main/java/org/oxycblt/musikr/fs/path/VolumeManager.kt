/*
 * Copyright (c) 2024 Auxio Project
 * VolumeManager.kt is part of Auxio.
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

import android.content.Context
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import javax.inject.Inject
import org.oxycblt.musikr.fs.Components
import org.oxycblt.musikr.fs.Volume

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
            get() = storageVolume.directoryCompat?.let(Components.Companion::parseUnix)

        override fun resolveName(context: Context) = storageVolume.getDescriptionCompat(context)
    }

    private data class ExternalVolumeImpl(val storageVolume: StorageVolume) : Volume.External {
        override val id
            get() = storageVolume.uuidCompat

        override val mediaStoreName
            get() = storageVolume.mediaStoreVolumeNameCompat

        override val components
            get() = storageVolume.directoryCompat?.let(Components.Companion::parseUnix)

        override fun resolveName(context: Context) = storageVolume.getDescriptionCompat(context)
    }
}
