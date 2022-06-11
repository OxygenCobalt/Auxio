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

import android.content.Context
import org.oxycblt.auxio.R

/**
 * Represents a path to a file from the android file-system. Intentionally designed to be
 * version-agnostic and follow modern storage recommendations.
 */
data class Path(val name: String, val parent: Dir)

/**
 * Represents a directory from the android file-system. Intentionally designed to be
 * version-agnostic and follow modern storage recommendations.
 */
sealed class Dir {
    /**
     * An absolute path.
     *
     * This is only used with [Song] instances on pre-Q android versions. This should be avoided in
     * most cases for [Relative].
     */
    data class Absolute(val path: String) : Dir()

    /**
     * A directory with a volume.
     *
     * This data structure is not version-specific:
     * - With excluded directories, it is the only path that is used. On versions that do not
     * support path, [Volume.Primary] is used.
     * - On songs, this is version-specific. It will only appear on versions that support it.
     */
    data class Relative(val volume: Volume, val relativePath: String) : Dir()

    sealed class Volume {
        object Primary : Volume()
        data class Secondary(val name: String) : Volume()
    }

    fun resolveName(context: Context) =
        when (this) {
            is Absolute -> path
            is Relative ->
                when (volume) {
                    is Volume.Primary -> context.getString(R.string.fmt_primary_path, relativePath)
                    is Volume.Secondary ->
                        context.getString(R.string.fmt_secondary_path, relativePath)
                }
        }
}
