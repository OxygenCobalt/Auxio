/*
 * Copyright (c) 2024 Auxio Project
 * Metadata.kt is part of Auxio.
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
 
package org.oxycblt.musikr.metadata

data class Metadata(
    val id3v2: Map<String, List<String>>,
    val xiph: Map<String, List<String>>,
    val mp4: Map<String, List<String>>,
    val cover: ByteArray?,
    val properties: Properties
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Metadata

        if (id3v2 != other.id3v2) return false
        if (xiph != other.xiph) return false
        if (mp4 != other.mp4) return false
        if (cover != null) {
            if (other.cover == null) return false
            if (!cover.contentEquals(other.cover)) return false
        } else if (other.cover != null) return false
        if (properties != other.properties) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id3v2.hashCode()
        result = 31 * result + xiph.hashCode()
        result = 31 * result + mp4.hashCode()
        result = 31 * result + (cover?.contentHashCode() ?: 0)
        result = 31 * result + properties.hashCode()
        return result
    }
}

data class Properties(
    val mimeType: String,
    val durationMs: Long,
    val bitrateKbps: Int,
    val sampleRateHz: Int,
)
