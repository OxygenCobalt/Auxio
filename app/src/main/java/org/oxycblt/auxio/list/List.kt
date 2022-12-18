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

import android.view.View
import androidx.annotation.StringRes

/** A marker for something that is a RecyclerView item. Has no functionality on it's own. */
interface Item

/** A data object used solely for the "Header" UI element. */
data class Header(
    /** The string resource used for the header. */
    @StringRes val string: Int
) : Item

open class ItemClickCallback(val onClick: (Item) -> Unit)

open class ItemMenuCallback(onClick: (Item) -> Unit, val onOpenMenu: (Item, View) -> Unit) :
    ItemClickCallback(onClick)

open class ItemSelectCallback(
    onClick: (Item) -> Unit,
    onOpenMenu: (Item, View) -> Unit,
    val onSelect: (Item) -> Unit
) : ItemMenuCallback(onClick, onOpenMenu)
