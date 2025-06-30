/*
 * Copyright (c) 2025 Auxio Project
 * LocationConfig.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.locations

import android.content.Context
import androidx.core.net.toUri
import org.oxycblt.auxio.util.splitEscaped
import org.oxycblt.musikr.fs.Location
import org.oxycblt.musikr.fs.mediastore.MediaStoreFS
import org.oxycblt.musikr.fs.saf.SAF

sealed interface LocationConfig {
    data class Filesystem(private val query: SAF.Query) : LocationConfig

    data class SystemDatabase(private val query: MediaStoreFS.Query) : LocationConfig

    companion object {
        fun from(context: Context, string: String): LocationConfig? {
            val typeSplit = string.splitEscaped('!')
            if (typeSplit.size != 2) return null
            val type = typeSplit[0]
            val args = typeSplit[1].splitEscaped('|')
            return when (type) {
                "saf" -> {
                    if (args.size < 3) return null
                    val source =
                        args[0]
                            .splitEscaped(';')
                            .map { it.toUri() }
                            .mapNotNull { Location.Unopened.from(context, it)?.open(context) }
                    val exclude =
                        args[1]
                            .splitEscaped(';')
                            .map { it.toUri() }
                            .mapNotNull { Location.Unopened.from(context, it) }
                    val withHidden = args[2].toBoolean()
                    val query =
                        SAF.Query(source = source, exclude = exclude, withHidden = withHidden)
                    Filesystem(query = query)
                }
                "media" -> {
                    if (args.size < 3) return null
                    val include =
                        args[0]
                            .splitEscaped(';')
                            .map { it.toUri() }
                            .mapNotNull { Location.Unopened.from(context, it) }
                    val exclude =
                        args[1]
                            .splitEscaped(';')
                            .map { it.toUri() }
                            .mapNotNull { Location.Unopened.from(context, it) }
                    val excludeNonMusic = args[1].toBoolean()
                    SystemDatabase(
                        MediaStoreFS.Query(
                            include = include,
                            exclude = exclude,
                            excludeNonMusic = excludeNonMusic))
                }
                else -> null
            }
        }
    }
}
