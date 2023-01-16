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

import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Field
import org.oxycblt.auxio.util.lazyReflectedField
import org.oxycblt.auxio.util.requireIs

/**
 * List differ wrapper that provides more flexibility regarding the way lists are updated.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface ListDiffer<T> {
    /** The current list of [T] items. */
    val currentList: List<T>

    /**
     * Update this list using [DiffUtil]. This can simplify the work of updating the list, but can
     * also cause erratic behavior.
     * @param newList The new list of [T] items to show.
     * @param onDone Callback that will be invoked when the update is completed, allowing means to
     * reset the state.
     */
    fun diffList(newList: List<T>, onDone: () -> Unit = {})

    /**
     * Visually replace the previous list with a new list. This is useful for large diffs that are
     * too erratic for [diffList].
     * @param newList The new list of [T] items to show.
     */
    fun replaceList(newList: List<T>)

    /**
     * Defines the creation of new [ListDiffer] instances. Allows such [ListDiffer]s to be passed as
     * arguments without reliance on a `this` [RecyclerView.Adapter].
     */
    abstract class Factory<T> {
        /**
         * Create a new [ListDiffer] bound to the given [RecyclerView.Adapter].
         * @param adapter The [RecyclerView.Adapter] to bind to.
         */
        abstract fun new(adapter: RecyclerView.Adapter<*>): ListDiffer<T>
    }

    /**
     * Update lists on another thread. This is useful when large diffs are likely to occur in this
     * list that would be exceedingly slow with [Blocking].
     * @param diffCallback A [DiffUtil.ItemCallback] to use for item comparison when diffing the
     * internal list.
     */
    class Async<T>(private val diffCallback: DiffUtil.ItemCallback<T>) : Factory<T>() {
        override fun new(adapter: RecyclerView.Adapter<*>): ListDiffer<T> =
            RealAsyncListDiffer(AdapterListUpdateCallback(adapter), diffCallback)
    }

    /**
     * Update lists on the main thread. This is useful when many small, discrete list diffs are
     * likely to occur that would cause [Async] to get race conditions.
     * @param diffCallback A [DiffUtil.ItemCallback] to use for item comparison when diffing the
     * internal list.
     */
    class Blocking<T>(private val diffCallback: DiffUtil.ItemCallback<T>) : Factory<T>() {
        override fun new(adapter: RecyclerView.Adapter<*>): ListDiffer<T> =
            RealBlockingListDiffer(AdapterListUpdateCallback(adapter), diffCallback)
    }
}

private class RealAsyncListDiffer<T>(
    private val updateCallback: ListUpdateCallback,
    diffCallback: DiffUtil.ItemCallback<T>
) : ListDiffer<T> {
    private val inner =
        AsyncListDiffer(updateCallback, AsyncDifferConfig.Builder(diffCallback).build())

    override val currentList: List<T>
        get() = inner.currentList

    override fun diffList(newList: List<T>, onDone: () -> Unit) {
        inner.submitList(newList, onDone)
    }

    override fun replaceList(newList: List<T>) {
        if (inner.currentList == newList) {
            // Nothing to do.
            return
        }
        // Do possibly the most idiotic thing possible and mutate the internal differ state
        // so we don't have to deal with any disjoint list garbage. This should cancel any prior
        // updates and correctly set up the list values while still allowing for the same
        // visual animation as the blocking replaceList.
        val oldListSize = inner.currentList.size
        ASD_MAX_GENERATION_FIELD.set(inner, requireIs<Int>(ASD_MAX_GENERATION_FIELD.get(inner)) + 1)
        ASD_MUTABLE_LIST_FIELD.set(inner, newList.ifEmpty { null })
        ASD_READ_ONLY_LIST_FIELD.set(inner, newList)
        updateCallback.onRemoved(0, oldListSize)
        updateCallback.onInserted(0, newList.size)
    }

    private companion object {
        val ASD_MAX_GENERATION_FIELD: Field by
            lazyReflectedField(AsyncListDiffer::class, "mMaxScheduledGeneration")
        val ASD_MUTABLE_LIST_FIELD: Field by lazyReflectedField(AsyncListDiffer::class, "mList")
        val ASD_READ_ONLY_LIST_FIELD: Field by
            lazyReflectedField(AsyncListDiffer::class, "mReadOnlyList")
    }
}

private class RealBlockingListDiffer<T>(
    private val updateCallback: ListUpdateCallback,
    private val diffCallback: DiffUtil.ItemCallback<T>
) : ListDiffer<T> {
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

    override fun replaceList(newList: List<T>) {
        if (currentList == newList) {
            // Nothing to do.
            return
        }

        diffList(listOf())
        diffList(newList)
    }
}
