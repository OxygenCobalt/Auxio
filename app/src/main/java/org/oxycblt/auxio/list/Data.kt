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
 
package org.oxycblt.auxio.list

import androidx.annotation.StringRes

/** A marker for something that is a RecyclerView item. Has no functionality on it's own. */
interface Item

/**
 * A "header" used for delimiting groups of data.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface Header : Item {
    /** The string resource used for the header's title. */
    val titleRes: Int
}

/**
 * A basic header with no additional actions.
 * @param titleRes The string resource used for the header's title.
 * @author Alexander Capehart (OxygenCobalt)
 */
data class BasicHeader(@StringRes override val titleRes: Int) : Header
