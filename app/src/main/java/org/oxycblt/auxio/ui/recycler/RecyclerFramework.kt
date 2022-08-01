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
 
package org.oxycblt.auxio.ui.recycler

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.AdapterListUpdateCallback
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

// TODO: Differentiate between replace and diffing w/item updates
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
     * bind the viewholder.
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
    /** The string resource used for the header. */
    @StringRes val string: Int
) : Item() {
    override val id: Long
        get() = string.toLong()
}

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
 * A list-backed [BackingData] that is modified synchronously. This is generally the recommended
 * option for most adapters.
 * @author OxygenCobalt
 */
class SyncBackingData<T>(adapter: RecyclerView.Adapter<*>, diffCallback: DiffUtil.ItemCallback<T>) :
    BackingData<T>() {
    private var differ = SyncListDiffer(adapter, diffCallback)
    /** The current list backing this adapter. */
    val currentList: List<T>
        get() = differ.currentList

    override fun getItem(position: Int): T = differ.currentList[position]
    override fun getItemCount(): Int = differ.currentList.size

    /** Submit a list normally, doing a diff synchronously. */
    fun submitList(newList: List<T>) {
        differ.currentList = newList
    }

    /**
     * Replace this list with a new list. This is useful for very large list diffs that would
     * generally be too chaotic and slow to provide a good UX.
     */
    fun replaceList(newList: List<T>) {
        if (newList == differ.currentList) {
            return
        }

        differ.currentList = emptyList()
        differ.currentList = newList
    }
}

/**
 * Like [AsyncListDiffer], but synchronous. This may seem like it would be inefficient, but in
 * practice Auxio's lists tend to be small enough to the point where this does not matter, and
 * situations that would be inefficient are ruled out with [SyncBackingData.replaceList].
 */
private class SyncListDiffer<T>(
    adapter: RecyclerView.Adapter<*>,
    private val diffCallback: DiffUtil.ItemCallback<T>
) {
    private val updateCallback = AdapterListUpdateCallback(adapter)

    private var _currentList: List<T> = emptyList()
    var currentList: List<T>
        get() = _currentList
        set(newList) {
            if (newList === _currentList || newList.isEmpty() && _currentList.isEmpty()) {
                return
            }

            if (newList.isEmpty()) {
                val oldListSize = _currentList.size
                _currentList = emptyList()
                updateCallback.onRemoved(0, oldListSize)
                return
            }

            if (_currentList.isEmpty()) {
                _currentList = newList
                updateCallback.onInserted(0, newList.size)
                return
            }

            val oldList = _currentList
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

            _currentList = newList
            result.dispatchUpdatesTo(updateCallback)
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
