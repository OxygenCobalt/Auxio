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
 
package org.oxycblt.auxio.detail

import androidx.annotation.StringRes
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.filesystem.MimeType

/**
 * A header variation that displays a button to open a sort menu.
 * @param titleRes The string resource to use as the header title
 */
data class SortHeader(@StringRes val titleRes: Int) : Item

/**
 * A header variation that delimits between disc groups.
 * @param disc The disc number to be displayed on the header.
 */
data class DiscHeader(val disc: Int) : Item

/**
 * The properties of a [Song]'s file.
 * @param bitrateKbps The bit rate, in kilobytes-per-second. Null if it could not be parsed.
 * @param sampleRateHz The sample rate, in hertz.
 * @param resolvedMimeType The known mime type of the [Song] after it's file format was determined.
 */
data class SongProperties(
    val bitrateKbps: Int?,
    val sampleRateHz: Int?,
    val resolvedMimeType: MimeType
)
