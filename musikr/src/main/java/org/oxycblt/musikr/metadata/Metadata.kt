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

internal data class Metadata(
    val id3v2: Map<String, List<String>>,
    val xiph: Map<String, List<String>>,
    val mp4: Map<String, List<String>>,
    val cover: ByteArray?,
    val properties: Properties
)

internal data class Properties(
    val mimeType: String,
    val durationMs: Long,
    val bitrateKbps: Int,
    val sampleRateHz: Int,
)
