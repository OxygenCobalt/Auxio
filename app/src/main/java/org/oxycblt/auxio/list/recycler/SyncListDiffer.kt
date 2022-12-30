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
 
package org.oxycblt.auxio.list.recycler

import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * A list differ that operates synchronously. This can help resolve some shortcomings with
 * AsyncListDiffer, at the cost of performance. Derived from Material Files:
 * https://github.com/zhanghai/MaterialFiles
 * @author Hai Zhang, Alexander Capehart (OxygenCobalt)
 */
class SyncListDiffer<T>(
    adapter: RecyclerView.Adapter<*>,
    private val diffCallback: DiffUtil.ItemCallback<T>
) {
    private val updateCallback = AdapterListUpdateCallback(adapter)

    var currentList: List<T> = emptyList()
        private set(newList) {
            if (newList === currentList || newList.isEmpty() && currentList.isEmpty()) {
                return
            }

            if (newList.isEmpty()) {
                val oldListSize = currentList.size
                field = emptyList()
                updateCallback.onRemoved(0, oldListSize)
                return
            }

            if (currentList.isEmpty()) {
                field = newList
                updateCallback.onInserted(0, newList.size)
                return
            }

            val oldList = currentList
            val result =
                DiffUtil.calculateDiff(
                    object : DiffUtil.Callback() {
                        override fun getOldListSize(): Int {
                            return oldList.size
                        }

                        override fun getNewListSize(): Int {
                            return newList.size
                        }

                        override fun areItemsTheSame(
                            oldItemPosition: Int,
                            newItemPosition: Int
                        ): Boolean {
                            val oldItem: T? = oldList[oldItemPosition]
                            val newItem: T? = newList[newItemPosition]
                            return if (oldItem != null && newItem != null) {
                                diffCallback.areItemsTheSame(oldItem, newItem)
                            } else {
                                oldItem == null && newItem == null
                            }
                        }

                        override fun areContentsTheSame(
                            oldItemPosition: Int,
                            newItemPosition: Int
                        ): Boolean {
                            val oldItem: T? = oldList[oldItemPosition]
                            val newItem: T? = newList[newItemPosition]
                            return if (oldItem != null && newItem != null) {
                                diffCallback.areContentsTheSame(oldItem, newItem)
                            } else if (oldItem == null && newItem == null) {
                                true
                            } else {
                                throw AssertionError()
                            }
                        }

                        override fun getChangePayload(
                            oldItemPosition: Int,
                            newItemPosition: Int
                        ): Any? {
                            val oldItem: T? = oldList[oldItemPosition]
                            val newItem: T? = newList[newItemPosition]
                            return if (oldItem != null && newItem != null) {
                                diffCallback.getChangePayload(oldItem, newItem)
                            } else {
                                throw AssertionError()
                            }
                        }
                    })

            field = newList
            result.dispatchUpdatesTo(updateCallback)
        }

    /**
     * Submit a list like AsyncListDiffer. This is exceedingly slow for large diffs, so only use it
     * if the changes are trivial.
     * @param newList The list to update to.
     */
    fun submitList(newList: List<T>) {
        if (newList == currentList) {
            // Nothing to do.
            return
        }

        currentList = newList
    }

    /**
     * Replace this list with a new list. This is good for large diffs that are too slow to update
     * synchronously, but too chaotic to update asynchronously.
     * @param newList The list to update to.
     */
    fun replaceList(newList: List<T>) {
        if (newList == currentList) {
            // Nothing to do.
            return
        }

        currentList = emptyList()
        currentList = newList
    }
}
