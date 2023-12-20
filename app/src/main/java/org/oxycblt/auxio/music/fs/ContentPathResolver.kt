/*
 * Copyright (c) 2023 Auxio Project
 * ContentPathResolver.kt is part of Auxio.
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

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.database.getStringOrNull
import org.oxycblt.auxio.util.logE

/**
 * Resolves a content URI into a [Path] instance.
 *
 * @author Alexander Capehart (OxygenCobalt)
 *
 * TODO: Integrate this with [MediaStoreExtractor].
 */
interface ContentPathResolver {
    /**
     * Resolve a content [Uri] into it's corresponding [Path].
     *
     * @param uri The content [Uri] to resolve.
     * @return The corresponding [Path], or null if the [Uri] is invalid.
     */
    fun resolve(uri: Uri): Path?

    companion object {
        fun from(context: Context, volumeManager: VolumeManager) =
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->
                    Api29ContentPathResolverImpl(context.contentResolverSafe, volumeManager)
                else -> Api21ContentPathResolverImpl(context.contentResolverSafe, volumeManager)
            }
    }
}

private class Api21ContentPathResolverImpl(
    private val contentResolver: ContentResolver,
    private val volumeManager: VolumeManager
) : ContentPathResolver {
    override fun resolve(uri: Uri): Path? {
        val rawPath =
            contentResolver.useQuery(uri, arrayOf(MediaStore.MediaColumns.DATA)) { cursor ->
                cursor.moveToFirst()
                cursor.getStringOrNull(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
            }

        if (rawPath == null) {
            logE("No data available for uri $uri")
            return null
        }

        val volumes = volumeManager.getVolumes()
        for (volume in volumes) {
            val volumePath = (volume.components ?: continue).toString()
            val strippedPath = rawPath.removePrefix(volumePath)
            if (strippedPath != rawPath) {
                return Path(volume, Components.parse(strippedPath))
            }
        }

        logE("No volume found for uri $uri")
        return null
    }
}

private class Api29ContentPathResolverImpl(
    private val contentResolver: ContentResolver,
    private val volumeManager: VolumeManager
) : ContentPathResolver {
    private data class RawPath(val volumeName: String?, val relativePath: String?)

    override fun resolve(uri: Uri): Path? {
        val rawPath =
            contentResolver.useQuery(
                uri,
                arrayOf(
                    MediaStore.MediaColumns.VOLUME_NAME, MediaStore.MediaColumns.RELATIVE_PATH)) {
                    cursor ->
                    cursor.moveToFirst()
                    RawPath(
                        cursor.getStringOrNull(
                            cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.VOLUME_NAME)),
                        cursor.getStringOrNull(
                            cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.RELATIVE_PATH)))
                }

        if (rawPath.volumeName == null || rawPath.relativePath == null) {
            logE("No data available for uri $uri (raw path obtained: $rawPath)")
            return null
        }

        // Find the StorageVolume whose MediaStore name corresponds to this song.
        // This is combined with the plain relative path column to create the directory.
        val volume = volumeManager.getVolumes().find { it.mediaStoreName == rawPath.volumeName }
        if (volume != null) {
            return Path(volume, Components.parse(rawPath.relativePath))
        }

        logE("No volume found for uri $uri")
        return null
    }
}
