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
 
package org.oxycblt.auxio.ui.recycler

import android.view.View
import androidx.annotation.StringRes
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/** A marker for something that is a RecyclerView item. Has no functionality on it's own. */
interface Item

/** A data object used solely for the "Header" UI element. */
data class Header(
    /** The string resource used for the header. */
    @StringRes val string: Int
) : Item

/** An interface for detecting if an item has been clicked once. */
interface ItemClickListener {
    /** Called when an item is clicked once. */
    fun onItemClick(item: Item)
}

/** An interface for detecting if an item has had it's menu opened. */
interface MenuItemListener : ItemClickListener {
    /** Called when an item desires to open a menu relating to it. */
    fun onOpenMenu(item: Item, anchor: View)
}

/**
 * Like AsyncListDiffer, but synchronous. This may seem like it would be inefficient, but in
 * practice Auxio's lists tend to be small enough to the point where this does not matter, and
 * situations that would be inefficient are ruled out with [replaceList].
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
                    }
                )

            field = newList
            result.dispatchUpdatesTo(updateCallback)
        }

    /** Submit a list normally, doing a diff synchronously. Only use this for trivial changes. */
    fun submitList(newList: List<T>) {
        currentList = newList
    }

    /**
     * Replace this list with a new list. This is useful for very large list diffs that would
     * generally be too chaotic and slow to provide a good UX.
     */
    fun replaceList(newList: List<T>) {
        if (newList == currentList) {
            return
        }

        currentList = emptyList()
        currentList = newList
    }
}

/**
 * A base [DiffUtil.ItemCallback] that automatically provides an implementation of
 * [areContentsTheSame] any object that is derived from [Item].
 */
abstract class SimpleItemCallback<T : Item> : DiffUtil.ItemCallback<T>() {
    final override fun areItemsTheSame(oldItem: T, newItem: T) = oldItem == newItem
}
