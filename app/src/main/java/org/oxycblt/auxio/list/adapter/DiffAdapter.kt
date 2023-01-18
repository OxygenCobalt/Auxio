/*
 * Copyright (c) 2023 Auxio Project
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

import androidx.recyclerview.widget.RecyclerView

/**
 * A [RecyclerView.Adapter] with [ListDiffer] integration.
 * @param differFactory The [ListDiffer.Factory] that defines the type of [ListDiffer] to use.
 */
abstract class DiffAdapter<T, I, VH : RecyclerView.ViewHolder>(
    differFactory: ListDiffer.Factory<T, I>
) : RecyclerView.Adapter<VH>() {
    private val differ = differFactory.new(@Suppress("LeakingThis") this)

    final override fun getItemCount() = differ.currentList.size

    /** The current list of [T] items. */
    val currentList: List<T>
        get() = differ.currentList

    /**
     * Get a [T] item at the given position.
     * @param at The position to get the item at.
     * @throws IndexOutOfBoundsException If the index is not in the list bounds/
     */
    fun getItem(at: Int) = differ.currentList[at]

    /**
     * Dynamically determine how to update the list based on the given instructions.
     * @param newList The new list of [T] items to show.
     * @param instructions The instructions specifying how to update the list.
     * @param onDone Called when the update process is completed. Defaults to a no-op.
     */
    fun submitList(newList: List<T>, instructions: I, onDone: () -> Unit = {}) {
        differ.submitList(newList, instructions, onDone)
    }
}
