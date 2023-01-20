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

import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView

// TODO: Re-add list instructions with a less dangerous framework.

/**
 * List differ wrapper that provides more flexibility regarding the way lists are updated.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface ListDiffer<T, I> {
    /** The current list of [T] items. */
    val currentList: List<T>

    /**
     * Dynamically determine how to update the list based on the given instructions.
     * @param newList The new list of [T] items to show.
     * @param instructions The [BasicListInstructions] specifying how to update the list.
     * @param onDone Called when the update process is completed.
     */
    fun submitList(newList: List<T>, instructions: I, onDone: () -> Unit)

    /**
     * Defines the creation of new [ListDiffer] instances. Allows such [ListDiffer]s to be passed as
     * arguments without reliance on a `this` [RecyclerView.Adapter].
     */
    abstract class Factory<T, I> {
        /**
         * Create a new [ListDiffer] bound to the given [RecyclerView.Adapter].
         * @param adapter The [RecyclerView.Adapter] to bind to.
         */
        abstract fun new(adapter: RecyclerView.Adapter<*>): ListDiffer<T, I>
    }

    /**
     * Update lists on another thread. This is useful when large diffs are likely to occur in this
     * list that would be exceedingly slow with [Blocking].
     * @param diffCallback A [DiffUtil.ItemCallback] to use for item comparison when diffing the
     * internal list.
     */
    class Async<T>(private val diffCallback: DiffUtil.ItemCallback<T>) :
        Factory<T, BasicListInstructions>() {
        override fun new(adapter: RecyclerView.Adapter<*>): ListDiffer<T, BasicListInstructions> =
            RealAsyncListDiffer(AdapterListUpdateCallback(adapter), diffCallback)
    }

    /**
     * Update lists on the main thread. This is useful when many small, discrete list diffs are
     * likely to occur that would cause [Async] to suffer from race conditions.
     * @param diffCallback A [DiffUtil.ItemCallback] to use for item comparison when diffing the
     * internal list.
     */
    class Blocking<T>(private val diffCallback: DiffUtil.ItemCallback<T>) :
        Factory<T, BasicListInstructions>() {
        override fun new(adapter: RecyclerView.Adapter<*>): ListDiffer<T, BasicListInstructions> =
            RealBlockingListDiffer(AdapterListUpdateCallback(adapter), diffCallback)
    }
}

/**
 * Represents the specific way to update a list of items.
 * @author Alexander Capehart (OxygenCobalt)
 */
enum class BasicListInstructions {
    /**
     * (A)synchronously diff the list. This should be used for small diffs with little item
     * movement.
     */
    DIFF,

    /**
     * Synchronously remove the current list and replace it with a new one. This should be used for
     * large diffs with that would cause erratic scroll behavior or in-efficiency.
     */
    REPLACE
}

private abstract class BasicListDiffer<T> : ListDiffer<T, BasicListInstructions> {
    override fun submitList(
        newList: List<T>,
        instructions: BasicListInstructions,
        onDone: () -> Unit
    ) {
        when (instructions) {
            BasicListInstructions.DIFF -> diffList(newList, onDone)
            BasicListInstructions.REPLACE -> replaceList(newList, onDone)
        }
    }

    protected abstract fun diffList(newList: List<T>, onDone: () -> Unit)
    protected abstract fun replaceList(newList: List<T>, onDone: () -> Unit)
}

private class RealAsyncListDiffer<T>(
    updateCallback: ListUpdateCallback,
    diffCallback: DiffUtil.ItemCallback<T>
) : BasicListDiffer<T>() {
    private val inner =
        AsyncListDiffer(updateCallback, AsyncDifferConfig.Builder(diffCallback).build())

    override val currentList: List<T>
        get() = inner.currentList

    override fun diffList(newList: List<T>, onDone: () -> Unit) {
        inner.submitList(newList, onDone)
    }

    override fun replaceList(newList: List<T>, onDone: () -> Unit) {
        inner.submitList(null) { inner.submitList(newList, onDone) }
    }
}

private class RealBlockingListDiffer<T>(
    private val updateCallback: ListUpdateCallback,
    private val diffCallback: DiffUtil.ItemCallback<T>
) : BasicListDiffer<T>() {
    override var currentList = listOf<T>()

    override fun diffList(newList: List<T>, onDone: () -> Unit) {
        if (newList === currentList || newList.isEmpty() && currentList.isEmpty()) {
            onDone()
            return
        }

        if (newList.isEmpty()) {
            val oldListSize = currentList.size
            currentList = listOf()
            updateCallback.onRemoved(0, oldListSize)
            onDone()
            return
        }

        if (currentList.isEmpty()) {
            currentList = newList
            updateCallback.onInserted(0, newList.size)
            onDone()
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

        currentList = newList
        result.dispatchUpdatesTo(updateCallback)
        onDone()
    }

    override fun replaceList(newList: List<T>, onDone: () -> Unit) {
        if (currentList != newList) {
            diffList(listOf()) { diffList(newList, onDone) }
        }
    }
}
