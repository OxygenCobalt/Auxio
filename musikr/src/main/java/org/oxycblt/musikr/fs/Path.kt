/*
 * Copyright (c) 2024 Auxio Project
 * Path.kt is part of Auxio.
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
 
package org.oxycblt.musikr.fs

import android.content.Context
import java.io.File

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

    internal companion object {
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
