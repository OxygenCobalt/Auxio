/*
 * Copyright (c) 2022 Auxio Project
 * SimpleDiffCallback.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.adapter

import androidx.recyclerview.widget.DiffUtil
import org.oxycblt.auxio.list.Item

/**
 * A [DiffUtil.ItemCallback] that automatically implements the [areItemsTheSame] method. Use this
 * whenever creating [DiffUtil.ItemCallback] implementations with an [Item] subclass.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class SimpleDiffCallback<T : Item> : DiffUtil.ItemCallback<T>() {
    final override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}
