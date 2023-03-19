/*
 * Copyright (c) 2023 Auxio Project
 * FlexibleListAdapter.kt is part of Auxio.
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

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.Executor

/**
 * A variant of ListDiffer with more flexible updates.
 *
 * @param diffCallback A [DiffUtil.ItemCallback] to compare list updates with.
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class FlexibleListAdapter<T, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>
) : RecyclerView.Adapter<VH>() {
    @Suppress("LeakingThis") private val differ = FlexibleListDiffer(this, diffCallback)
    final override fun getItemCount() = differ.currentList.size
    /** The current list stored by the adapter's differ instance. */
    val currentList: List<T>
        get() = differ.currentList
    /** @see currentList */
    fun getItem(at: Int) = differ.currentList[at]

    /**
     * Update the adapter with new data.
     *
     * @param newData The new list of data to update with.
     * @param instructions The [UpdateInstructions] to visually update the list with.
     * @param callback Called when the update is completed. May be done asynchronously.
     */
    fun update(
        newData: List<T>,
        instructions: UpdateInstructions?,
        callback: (() -> Unit)? = null
    ) = differ.update(newData, instructions, callback)
}

/**
 * Arbitrary instructions that can be given to a [FlexibleListAdapter] to direct how it updates
 * data.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
sealed class UpdateInstructions {
    /** Use an asynchronous diff. Useful for unpredictable updates, but looks chaotic and janky. */
    object Diff : UpdateInstructions()

    /**
     * Visually replace all items from a given point. More visually coherent than [Diff].
     *
     * @param from The index at which to start replacing items (inclusive)
     */
    data class Replace(val from: Int) : UpdateInstructions()

    /**
     * Add a new set of items.
     *
     * @param at The position at which to add.
     * @param size The amount of items to add.
     */
    data class Add(val at: Int, val size: Int) : UpdateInstructions()

    /**
     * Move one item to another location.
     *
     * @param from The index of the item to move.
     * @param to The index to move the item to.
     */
    data class Move(val from: Int, val to: Int) : UpdateInstructions()

    /**
     * Remove an item.
     *
     * @param at The location that the item should be removed from.
     */
    data class Remove(val at: Int) : UpdateInstructions()
}

/**
 * Vendor of AsyncListDiffer with more flexible update functionality.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
private class FlexibleListDiffer<T>(
    adapter: RecyclerView.Adapter<*>,
    diffCallback: DiffUtil.ItemCallback<T>
) {
    private val updateCallback = AdapterListUpdateCallback(adapter)
    private val config = AsyncDifferConfig.Builder(diffCallback).build()
    private val mainThreadExecutor = sMainThreadExecutor

    private class MainThreadExecutor : Executor {
        val mHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mHandler.post(command)
        }
    }

    var currentList = emptyList<T>()
        private set

    private var maxScheduledGeneration = 0

    fun update(newList: List<T>, instructions: UpdateInstructions?, callback: (() -> Unit)?) {
        // incrementing generation means any currently-running diffs are discarded when they finish
        val runGeneration = ++maxScheduledGeneration
        when (instructions) {
            is UpdateInstructions.Replace -> {
                updateCallback.onRemoved(instructions.from, currentList.size - instructions.from)
                currentList = newList
                if (newList.lastIndex >= instructions.from) {
                    // Need to re-insert the new data.
                    updateCallback.onInserted(instructions.from, newList.size - instructions.from)
                }
                callback?.invoke()
            }
            is UpdateInstructions.Add -> {
                currentList = newList
                updateCallback.onInserted(instructions.at, instructions.size)
                callback?.invoke()
            }
            is UpdateInstructions.Move -> {
                currentList = newList
                updateCallback.onMoved(instructions.from, instructions.to)
                callback?.invoke()
            }
            is UpdateInstructions.Remove -> {
                currentList = newList
                updateCallback.onRemoved(instructions.at, 1)
                callback?.invoke()
            }
            is UpdateInstructions.Diff,
            null -> diffList(currentList, newList, runGeneration, callback)
        }
    }

    private fun diffList(
        oldList: List<T>,
        newList: List<T>,
        runGeneration: Int,
        callback: (() -> Unit)?
    ) {
        // fast simple remove all
        if (newList.isEmpty()) {
            val countRemoved = oldList.size
            currentList = emptyList()
            // notify last, after list is updated
            updateCallback.onRemoved(0, countRemoved)
            callback?.invoke()
            return
        }

        // fast simple first insert
        if (oldList.isEmpty()) {
            currentList = newList
            // notify last, after list is updated
            updateCallback.onInserted(0, newList.size)
            callback?.invoke()
            return
        }

        config.backgroundThreadExecutor.execute {
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
                                config.diffCallback.areItemsTheSame(oldItem, newItem)
                            } else oldItem == null && newItem == null
                            // If both items are null we consider them the same.
                        }

                        override fun areContentsTheSame(
                            oldItemPosition: Int,
                            newItemPosition: Int
                        ): Boolean {
                            val oldItem: T? = oldList[oldItemPosition]
                            val newItem: T? = newList[newItemPosition]
                            if (oldItem != null && newItem != null) {
                                return config.diffCallback.areContentsTheSame(oldItem, newItem)
                            }
                            if (oldItem == null && newItem == null) {
                                return true
                            }
                            throw AssertionError()
                        }

                        override fun getChangePayload(
                            oldItemPosition: Int,
                            newItemPosition: Int
                        ): Any? {
                            val oldItem: T? = oldList[oldItemPosition]
                            val newItem: T? = newList[newItemPosition]
                            if (oldItem != null && newItem != null) {
                                return config.diffCallback.getChangePayload(oldItem, newItem)
                            }
                            throw AssertionError()
                        }
                    })
            mainThreadExecutor.execute {
                if (maxScheduledGeneration == runGeneration) {
                    currentList = newList
                    result.dispatchUpdatesTo(updateCallback)
                    callback?.invoke()
                }
            }
        }
    }

    companion object {
        private val sMainThreadExecutor: Executor = MainThreadExecutor()
    }
}
