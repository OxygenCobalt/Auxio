/*
 * Copyright (c) 2021 Auxio Project
 * DiffCallback.kt is part of Auxio.
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

package org.oxycblt.auxio.ui

import androidx.recyclerview.widget.DiffUtil
import org.oxycblt.auxio.music.BaseModel

/**
 * A re-usable diff callback for all [BaseModel] implementations.
 * **Use this instead of creating a DiffCallback for each adapter.**
 * @author OxygenCobalt
 */
class DiffCallback<T : BaseModel> : DiffUtil.ItemCallback<T>() {
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.hashCode() == newItem.hashCode()
    }

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        // Prevent ID collisions from occurring between datatypes.
        if (oldItem.javaClass != newItem.javaClass) return false

        return oldItem.id == newItem.id
    }
}
