/*
 * Copyright (c) 2021 Auxio Project
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

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * An adapter enabling both asynchronous list updates and synchronous list updates.
 *
 * DiffUtil is a joke. The animations are chaotic and gaudy, it does not preserve the scroll
 * position of the RecyclerView, it refuses to play along with item movements, and the speed gains
 * are minimal. We would rather want to use the slower yet more reliable notifyX in nearly all
 * cases, however DiffUtil does have some use in places such as search, so we still want the ability
 * to use a differ while also having access to the basic adapter primitives as well. This class
 * achieves it through some terrible reflection magic, and is more or less the base for all adapters
 * in the app.
 *
 * TODO: Delegate data management to the internal adapters so that we can isolate the horrible hacks
 * to the specific adapters that use need them.
 */
abstract class HybridAdapter<T, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>
) : RecyclerView.Adapter<VH>() {
    protected var mCurrentList = mutableListOf<T>()
    val currentList: List<T>
        get() = mCurrentList

    // Probably okay to leak this here since it's just a callback.
    @Suppress("LeakingThis") private val differ = AsyncListDiffer(this, diffCallback)

    protected fun getItem(position: Int): T = mCurrentList[position]

    override fun getItemCount(): Int = mCurrentList.size

    fun submitList(newData: List<T>, onDone: () -> Unit = {}) {
        if (newData != mCurrentList) {
            mCurrentList = newData.toMutableList()
            differ.submitList(newData, onDone)
        }
    }

    @Suppress("NotifyDatasetChanged")
    fun submitListHard(newList: List<T>) {
        if (newList != mCurrentList) {
            mCurrentList = newList.toMutableList()
            differ.rewriteListUnsafe(mCurrentList)
            notifyDataSetChanged()
        }
    }

    fun moveItems(from: Int, to: Int) {
        mCurrentList.add(to, mCurrentList.removeAt(from))
        differ.rewriteListUnsafe(mCurrentList)
        notifyItemMoved(from, to)
    }

    fun removeItem(at: Int) {
        mCurrentList.removeAt(at)
        differ.rewriteListUnsafe(mCurrentList)
        notifyItemRemoved(at)
    }

    /**
     * Rewrites the AsyncListDiffer's internal list, cancelling any diffs that are currently in
     * progress. I cannot describe in words how dangerous this is, but it's also the only thing I
     * can do to marry the adapter primitives with DiffUtil.
     */
    private fun <T> AsyncListDiffer<T>.rewriteListUnsafe(newList: List<T>) {
        differMaxGenerationsField.set(this, (differMaxGenerationsField.get(this) as Int).inc())
        differListField.set(this, newList.toMutableList())
        differImmutableListField.set(this, newList)
    }

    companion object {
        private val differListField =
            AsyncListDiffer::class.java.getDeclaredField("mList").apply { isAccessible = true }

        private val differImmutableListField =
            AsyncListDiffer::class.java.getDeclaredField("mReadOnlyList").apply {
                isAccessible = true
            }

        private val differMaxGenerationsField =
            AsyncListDiffer::class.java.getDeclaredField("mMaxScheduledGeneration").apply {
                isAccessible = true
            }
    }
}

abstract class MonoAdapter<T, L, VH : BindingViewHolder<T, L>>(
    private val listener: L,
    diffCallback: DiffUtil.ItemCallback<T>
) : HybridAdapter<T, VH>(diffCallback) {
    protected abstract val creator: BindingViewHolder.Creator<VH>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        creator.create(parent.context)

    override fun onBindViewHolder(viewHolder: VH, position: Int) {
        viewHolder.bind(getItem(position), listener)
    }
}

abstract class MultiAdapter<L>(private val listener: L, diffCallback: DiffUtil.ItemCallback<Item>) :
    HybridAdapter<Item, RecyclerView.ViewHolder>(diffCallback) {
    abstract fun getCreatorFromItem(
        item: Item
    ): BindingViewHolder.Creator<out RecyclerView.ViewHolder>?
    abstract fun getCreatorFromViewType(
        viewType: Int
    ): BindingViewHolder.Creator<out RecyclerView.ViewHolder>?
    abstract fun onBind(viewHolder: RecyclerView.ViewHolder, item: Item, listener: L)

    override fun getItemViewType(position: Int) =
        requireNotNull(getCreatorFromItem(getItem(position))) {
                "Unable to get view type for item ${getItem(position)}"
            }
            .viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        requireNotNull(getCreatorFromViewType(viewType)) {
                "Unable to create viewholder for view type $viewType"
            }
            .create(parent.context)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBind(holder, getItem(position), listener)
    }
}

/** The base for all items in Auxio. */
abstract class Item {
    /** A unique ID for this item. ***THIS IS NOT A MEDIASTORE ID!** */
    abstract val id: Long
}

/** A data object used solely for the "Header" UI element. */
data class Header(
    override val id: Long,
    /** The string resource used for the header. */
    @StringRes val string: Int
) : Item()

abstract class ItemDiffCallback<T : Item> : DiffUtil.ItemCallback<T>() {
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        if (oldItem.javaClass != newItem.javaClass) return false
        return oldItem.id == newItem.id
    }
}

interface ItemClickListener {
    fun onItemClick(item: Item)
}

interface MenuItemListener : ItemClickListener {
    fun onOpenMenu(item: Item, anchor: View)
}

abstract class BindingViewHolder<T, L>(root: View) : RecyclerView.ViewHolder(root) {
    abstract fun bind(item: T, listener: L)

    init {
        // Force the layout to *actually* be the screen width
        root.layoutParams =
            RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    interface Creator<VH : RecyclerView.ViewHolder> {
        val viewType: Int
        fun create(context: Context): VH
    }
}
