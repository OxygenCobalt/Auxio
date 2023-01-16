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
 
package org.oxycblt.auxio.list.recycler

import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.list.UpdateInstructions

/**
 * A [RecyclerView.Adapter] with [ListDiffer] integration.
 * @param differFactory The [ListDiffer.Factory] that defines the type of [ListDiffer] to use.
 */
abstract class DiffAdapter<T, VH : RecyclerView.ViewHolder>(differFactory: ListDiffer.Factory<T>) :
    RecyclerView.Adapter<VH>() {
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
     * Dynamically determine how to update the list based on the given [UpdateInstructions].
     * @param newList The new list of [T] items to show.
     * @param instructions The [UpdateInstructions] specifying how to update the list.
     */
    fun submitList(newList: List<T>, instructions: UpdateInstructions) {
        when (instructions) {
            UpdateInstructions.DIFF -> diffList(newList)
            UpdateInstructions.REPLACE -> replaceList(newList)
        }
    }

    /**
     * Update this list using [DiffUtil]. This can simplify the work of updating the list, but can
     * also cause erratic behavior.
     * @param newList The new list of [T] items to show.
     * @param onDone Callback that will be invoked when the update is completed, allowing means to
     * reset the state.
     */
    fun diffList(newList: List<T>, onDone: () -> Unit = {}) = differ.diffList(newList, onDone)

    /**
     * Visually replace the previous list with a new list. This is useful for large diffs that are
     * too erratic for [diffList].
     * @param newList The new list of [T] items to show.
     */
    fun replaceList(newList: List<T>) = differ.replaceList(newList)
}
