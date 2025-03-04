/*
 * Copyright (c) 2024 Auxio Project
 * CoverSilo.kt is part of Auxio.
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
 
package org.oxycblt.auxio.image.covers

import java.util.UUID
import org.oxycblt.musikr.covers.internal.CoverParams

data class CoverSilo(val revision: UUID, val params: CoverParams?) {
    override fun toString() =
        "${revision}.${params?.let { "${params.resolution}${params.quality}" }}"

    companion object {
        fun parse(silo: String): CoverSilo? {
            val parts = silo.split('.')
            if (parts.size != 3) return null
            val revision = parts[0].toUuidOrNull() ?: return null
            val resolution = parts[1].toIntOrNull() ?: return null
            val quality = parts[2].toIntOrNull() ?: return null
            return CoverSilo(revision, CoverParams.of(resolution, quality))
        }
    }
}

private fun String.toUuidOrNull(): UUID? =
    try {
        UUID.fromString(this)
    } catch (e: IllegalArgumentException) {
        null
    }
