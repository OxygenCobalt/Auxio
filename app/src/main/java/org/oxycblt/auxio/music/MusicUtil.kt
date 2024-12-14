/*
 * Copyright (c) 2024 Auxio Project
 * MusicUtil.kt is part of Auxio.
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
import kotlin.math.max
import org.oxycblt.auxio.R
import org.oxycblt.auxio.util.concatLocalized
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.tag.Name
import org.oxycblt.musikr.tag.Placeholder

fun Name.resolve(context: Context) =
    when (this) {
        is Name.Known -> raw
        is Name.Unknown ->
            when (placeholder) {
                Placeholder.ALBUM -> context.getString(R.string.def_album)
                Placeholder.ARTIST -> context.getString(R.string.def_artist)
                Placeholder.GENRE -> context.getString(R.string.def_genre)
            }
    }

/**
 * Run [Name.resolve] on each instance in the given list and concatenate them into a [String] in a
 * localized manner.
 *
 * @param context [Context] required
 * @return A concatenated string.
 */
fun <T : Music> List<T>.resolveNames(context: Context) =
    concatLocalized(context) { it.name.resolve(context) }

/**
 * Returns if [Music.name] matches for each item in a list. Useful for scenarios where the display
 * information of an item must be compared without a context.
 *
 * @param other The list of items to compare to.
 * @return True if they are the same (by [Music.name]), false otherwise.
 */
fun <T : Music> List<T>.areNamesTheSame(other: List<T>): Boolean {
    for (i in 0 until max(size, other.size)) {
        val a = getOrNull(i) ?: return false
        val b = other.getOrNull(i) ?: return false
        if (a.name != b.name) {
            return false
        }
    }

    return true
}
