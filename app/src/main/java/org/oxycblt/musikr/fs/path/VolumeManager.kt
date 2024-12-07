package org.oxycblt.musikr.fs.path

import android.content.Context
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import org.oxycblt.musikr.fs.Components
import org.oxycblt.musikr.fs.Volume
import org.oxycblt.musikr.fs.directoryCompat
import org.oxycblt.musikr.fs.getDescriptionCompat
import org.oxycblt.musikr.fs.isInternalCompat
import org.oxycblt.musikr.fs.mediaStoreVolumeNameCompat
import org.oxycblt.musikr.fs.storageVolumesCompat
import org.oxycblt.musikr.fs.uuidCompat
import javax.inject.Inject

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