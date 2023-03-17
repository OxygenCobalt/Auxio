/*
 * Copyright (c) 2023 Auxio Project
 * Disc.kt is part of Auxio.
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
 
package org.oxycblt.auxio.music.metadata

import org.oxycblt.auxio.list.Item

/**
 * A disc identifier for a song.
 *
 * @param number The disc number.
 * @param name The name of the disc group, if any. Null if not present.
 */
class Disc(val number: Int, val name: String?) : Item, Comparable<Disc> {
    override fun hashCode() = number.hashCode()
    override fun equals(other: Any?) = other is Disc && number == other.number
    override fun compareTo(other: Disc) = number.compareTo(other.number)
}
