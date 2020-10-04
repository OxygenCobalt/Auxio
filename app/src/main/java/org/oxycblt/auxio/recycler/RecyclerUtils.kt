package org.oxycblt.auxio.recycler

import androidx.recyclerview.widget.DiffUtil
import org.oxycblt.auxio.music.BaseModel

// RecyclerView click listener
class ClickListener<T : BaseModel>(val onClick: (T) -> Unit)

// Base Diff callback
class DiffCallback<T : BaseModel> : DiffUtil.ItemCallback<T>() {
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}
