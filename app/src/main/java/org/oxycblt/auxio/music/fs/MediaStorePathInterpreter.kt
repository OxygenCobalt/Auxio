/*
 * Copyright (c) 2024 Auxio Project
 * MediaStorePathInterpreter.kt is part of Auxio.
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

import android.database.Cursor
import android.os.Build
import android.provider.MediaStore
import org.oxycblt.auxio.util.logE

/**
 * Wrapper around a [Cursor] that interprets path information on a per-API/manufacturer basis.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed interface MediaStorePathInterpreter {
    /**
     * Extract a [Path] from the wrapped [Cursor]. This should be called after the cursor has been
     * moved to the row that should be interpreted.
     *
     * @return The [Path] instance, or null if the path could not be extracted.
     */
    fun extract(): Path?

    interface Factory {
        /** The columns that must be added to a query to support this interpreter. */
        val projection: Array<String>

        /**
         * Wrap a [Cursor] with this interpreter. This cursor should be the result of a query
         * containing the columns specified by [projection].
         *
         * @param cursor The [Cursor] to wrap.
         * @return A new [MediaStorePathInterpreter] that will work best on the device's API level.
         */
        fun wrap(cursor: Cursor): MediaStorePathInterpreter

        /**
         * Create a selector that will filter the given paths. By default this will filter *to* the
         * given paths, to exclude them, use a NOT.
         *
         * @param paths The paths to filter for.
         * @return A selector that will filter to the given paths, or null if a selector could not
         *   be created from the paths.
         */
        fun createSelector(paths: List<Path>): Selector?

        /**
         * A selector that will filter to the given paths.
         *
         * @param template The template to use for the selector.
         * @param args The arguments to use for the selector.
         * @see Factory.createSelector
         */
        data class Selector(val template: String, val args: List<String>)

        companion object {
            /**
             * Create a [MediaStorePathInterpreter.Factory] that will work best on the device's API
             * level.
             *
             * @param volumeManager The [VolumeManager] to use for volume information.
             */
            fun from(volumeManager: VolumeManager) =
                when {
                    // Huawei violates the API docs and prevents you from accessing the new path
                    // fields without first granting access to them through SAF. Fall back to DATA
                    // instead.
                    Build.MANUFACTURER.equals("huawei", ignoreCase = true) ||
                        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ->
                        DataMediaStorePathInterpreter.Factory(volumeManager)
                    else -> VolumeMediaStorePathInterpreter.Factory(volumeManager)
                }
        }
    }
}

/**
 * Wrapper around a [Cursor] that interprets the DATA column as a path. Create an instance with
 * [Factory].
 */
private class DataMediaStorePathInterpreter
private constructor(private val cursor: Cursor, volumeManager: VolumeManager) :
    MediaStorePathInterpreter {
    private val dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
    private val volumes = volumeManager.getVolumes()

    override fun extract(): Path? {
        val data = Components.parseUnix(cursor.getString(dataIndex))

        // Find the volume that transforms the DATA column into a relative path. This is
        // the Directory we will use.
        for (volume in volumes) {
            val volumePath = volume.components ?: continue
            if (volumePath.contains(data)) {
                return Path(volume, data.depth(volumePath.components.size))
            }
        }

        logE("Could not find volume for $data [tried: ${volumes.map { it.components }}]")

        return null
    }

    /**
     * Factory for [DataMediaStorePathInterpreter].
     *
     * @param volumeManager The [VolumeManager] to use for volume information.
     */
    class Factory(private val volumeManager: VolumeManager) : MediaStorePathInterpreter.Factory {
        override val projection: Array<String>
            get() =
                arrayOf(
                    MediaStore.Audio.AudioColumns.DISPLAY_NAME, MediaStore.Audio.AudioColumns.DATA)

        override fun createSelector(
            paths: List<Path>
        ): MediaStorePathInterpreter.Factory.Selector? {
            val args = mutableListOf<String>()
            var template = ""
            for (i in paths.indices) {
                val path = paths[i]
                val volume = path.volume.components ?: continue
                template +=
                    if (args.isEmpty()) {
                        "${MediaStore.Audio.AudioColumns.DATA} LIKE ?"
                    } else {
                        " OR ${MediaStore.Audio.AudioColumns.DATA} LIKE ?"
                    }
                args.add("/${volume}/${path.components}%")
            }

            if (template.isEmpty()) {
                return null
            }

            return MediaStorePathInterpreter.Factory.Selector(template, args)
        }

        override fun wrap(cursor: Cursor): MediaStorePathInterpreter =
            DataMediaStorePathInterpreter(cursor, volumeManager)
    }
}

/**
 * Wrapper around a [Cursor] that interprets the VOLUME_NAME, RELATIVE_PATH, and DISPLAY_NAME
 * columns as a path. Create an instance with [Factory].
 */
private class VolumeMediaStorePathInterpreter
private constructor(private val cursor: Cursor, volumeManager: VolumeManager) :
    MediaStorePathInterpreter {
    private val displayNameIndex =
        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
    private val volumeIndex =
        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.VOLUME_NAME)
    private val relativePathIndex =
        cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.RELATIVE_PATH)
    private val volumes = volumeManager.getVolumes()

    override fun extract(): Path? {
        // Find the StorageVolume whose MediaStore name corresponds to it.
        val volumeName = cursor.getString(volumeIndex)
        // Relative path does not include file name, must use DISPLAY_NAME and add it
        // in manually.
        val relativePath = cursor.getString(relativePathIndex)
        val displayName = cursor.getString(displayNameIndex)
        val volume = volumes.find { it.mediaStoreName == volumeName }
        if (volume == null) {
            logE(
                "Could not find volume for $volumeName:$relativePath/$displayName [tried: ${volumes.map { it.mediaStoreName }}]")
            return null
        }
        val components = Components.parseUnix(relativePath).child(displayName)
        return Path(volume, components)
    }

    /**
     * Factory for [VolumeMediaStorePathInterpreter].
     *
     * @param volumeManager The [VolumeManager] to use for volume information.
     */
    class Factory(private val volumeManager: VolumeManager) : MediaStorePathInterpreter.Factory {
        override val projection: Array<String>
            get() =
                arrayOf(
                    // After API 29, we now have access to the volume name and relative
                    // path, which hopefully are more standard and less likely to break
                    // compared to DATA.
                    MediaStore.Audio.AudioColumns.DISPLAY_NAME,
                    MediaStore.Audio.AudioColumns.VOLUME_NAME,
                    MediaStore.Audio.AudioColumns.RELATIVE_PATH)

        // The selector should be configured to compare both the volume name and relative path
        // of the given directories, albeit with some conversion to the analogous MediaStore
        // column values.

        override fun createSelector(
            paths: List<Path>
        ): MediaStorePathInterpreter.Factory.Selector? {
            val args = mutableListOf<String>()
            var template = ""
            for (i in paths.indices) {
                val path = paths[i]
                template +=
                    if (args.isEmpty()) {
                        "(${MediaStore.Audio.AudioColumns.VOLUME_NAME} LIKE ? " +
                            "AND ${MediaStore.Audio.AudioColumns.RELATIVE_PATH} LIKE ?)"
                    } else {
                        " OR (${MediaStore.Audio.AudioColumns.VOLUME_NAME} LIKE ? " +
                            "AND ${MediaStore.Audio.AudioColumns.RELATIVE_PATH} LIKE ?)"
                    }
                // MediaStore uses a different naming scheme for it's volume column. Convert this
                // directory's volume to it.
                args.add(path.volume.mediaStoreName ?: return null)
                // "%" signifies to accept any DATA value that begins with the Directory's path,
                // thus recursively filtering all files in the directory.
                args.add("${path.components}%")
            }

            if (template.isEmpty()) {
                return null
            }

            return MediaStorePathInterpreter.Factory.Selector(template, args)
        }

        override fun wrap(cursor: Cursor): MediaStorePathInterpreter =
            VolumeMediaStorePathInterpreter(cursor, volumeManager)
    }
}
