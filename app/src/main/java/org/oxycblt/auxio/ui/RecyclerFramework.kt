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
 * An adapter for one viewholder tied to one type of data. All functionality is derived from the
 * overridden values.
 * @author OxygenCobalt
 */
abstract class MonoAdapter<T, L, VH : BindingViewHolder<T, L>>(private val listener: L) :
    RecyclerView.Adapter<VH>() {
    /** The data that the adapter will source to bind viewholders. */
    abstract val data: BackingData<T>
    /** The creator instance that all viewholders will be derived from. */
    protected abstract val creator: BindingViewHolder.Creator<VH>

    /**
     * An optional override to further modify the given [viewHolder]. The normal operation is to
     * bind the viewholder, with nothing more.
     */
    open fun onBind(viewHolder: VH, item: T, listener: L, payload: List<Any>) {
        viewHolder.bind(item, listener)
    }

    override fun getItemCount(): Int = data.getItemCount()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        creator.create(parent.context)

    override fun onBindViewHolder(holder: VH, position: Int) = throw UnsupportedOperationException()

    override fun onBindViewHolder(viewHolder: VH, position: Int, payload: List<Any>) {
        onBind(viewHolder, data.getItem(position), listener, payload)
    }
}

private typealias AnyCreator = BindingViewHolder.Creator<out RecyclerView.ViewHolder>

/**
 * An adapter for many viewholders tied to many types of data. Deriving this is more complicated
 * than [MonoAdapter], as less overrides can be provided "for free".
 * @author OxygenCobalt
 */
abstract class MultiAdapter<L>(private val listener: L) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /** The data that the adapter will source to bind viewholders. */
    abstract val data: BackingData<Item>

    /**
     * Get any creator from the given item. This is used to derive the view type. If there is no
     * creator for the given item, return null.
     */
    protected abstract fun getCreatorFromItem(item: Item): AnyCreator?
    /**
     * Get any creator from the given view type. This is used to create the viewholder itself.
     * Ideally, one should compare the viewType to every creator's view type and return the one that
     * matches. In cases where the view type is unexpected, return null.
     */
    protected abstract fun getCreatorFromViewType(viewType: Int): AnyCreator?

    /**
     * Bind the given viewholder to an item. Casting must be done on the consumer's end due to
     * bounds on [BindingViewHolder].
     */
    protected abstract fun onBind(
        viewHolder: RecyclerView.ViewHolder,
        item: Item,
        listener: L,
        payload: List<Any>
    )

    override fun getItemCount(): Int = data.getItemCount()

    override fun getItemViewType(position: Int) =
        requireNotNull(getCreatorFromItem(data.getItem(position))) {
                "Unable to get view type for item ${data.getItem(position)}"
            }
            .viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        requireNotNull(getCreatorFromViewType(viewType)) {
                "Unable to create viewholder for view type $viewType"
            }
            .create(parent.context)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        throw UnsupportedOperationException()

    override fun onBindViewHolder(
        viewHolder: RecyclerView.ViewHolder,
        position: Int,
        payload: List<Any>
    ) {
        onBind(viewHolder, data.getItem(position), listener, payload)
    }
}

/**
 * A variation of [RecyclerView.ViewHolder] that enables ViewBinding. This is be used to provide a
 * universal surface for binding data to a ViewHolder, and can be used with [MonoAdapter] to get an
 * entire adapter implementation for free.
 * @author OxygenCobalt
 */
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
 * The base for all items in Auxio. Any datatype can derive this type and gain some behavior not
 * provided for free by the normal adapter implementations, such as certain types of diffing.
 */
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

/**
 * Represents data that backs a [MonoAdapter] or [MultiAdapter]. This can be implemented by any
 * datatype to customize the organization or editing of data in a way that works best for the
 * specific adapter.
 */
abstract class BackingData<T> {
    /** Get an item at [position]. */
    abstract fun getItem(position: Int): T
    /** Get the total length of the backing data. */
    abstract fun getItemCount(): Int
}

/**
 * A list-backed [BackingData] that is modified using adapter primitives. Useful in cases where
 * [AsyncBackingData] is not preferable due to bugs involving diffing.
 * @author OxygenCobalt
 */
class PrimitiveBackingData<T>(private val adapter: RecyclerView.Adapter<*>) : BackingData<T>() {
    private var _currentList = mutableListOf<T>()
    /** The current list backing this adapter. */
    val currentList: List<T>
        get() = _currentList

    override fun getItem(position: Int): T = _currentList[position]
    override fun getItemCount(): Int = _currentList.size

    /**
     * Update the list with a [newList]. This calls [RecyclerView.Adapter.notifyDataSetChanged]
     * internally, which is inefficient but also the most reliable update callback.
     */
    @Suppress("NotifyDatasetChanged")
    fun submitList(newList: List<T>) {
        _currentList = newList.toMutableList()
        adapter.notifyDataSetChanged()
    }
}

/**
 * A list-backed [BackingData] that is modified with [AsyncListDiffer]. This is useful in cases
 * where data updates are rapid-fire and unpredictable, and where the benefits of asynchronously
 * diffing the adapter outweigh the shortcomings.
 * @author OxygenCobalt
 */
class AsyncBackingData<T>(
    adapter: RecyclerView.Adapter<*>,
    diffCallback: DiffUtil.ItemCallback<T>
) : BackingData<T>() {
    private var differ = AsyncListDiffer(adapter, diffCallback)
    /** The current list backing this adapter. */
    val currentList: List<T>
        get() = differ.currentList

    override fun getItem(position: Int): T = differ.currentList[position]
    override fun getItemCount(): Int = differ.currentList.size

    /**
     * Submit a list for [AsyncListDiffer] to calculate. Any previous calls of [submitList] will be
     * dropped.
     */
    fun submitList(newList: List<T>, onDone: () -> Unit = {}) {
        differ.submitList(newList, onDone)
    }
}

/**
 * A base [DiffUtil.ItemCallback] that automatically provides an implementation of
 * [areContentsTheSame] any object that is derived from [Item].
 */
abstract class SimpleItemCallback<T : Item> : DiffUtil.ItemCallback<T>() {
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        if (oldItem.javaClass != newItem.javaClass) return false
        return oldItem.id == newItem.id
    }
}
