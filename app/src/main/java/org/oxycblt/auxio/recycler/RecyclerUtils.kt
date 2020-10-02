package org.oxycblt.auxio.recycler

import androidx.recyclerview.widget.DiffUtil
import org.oxycblt.auxio.music.BaseModel

// A RecyclerView click listener that can only be called once.
// Primarily used for navigation to prevent bugs when multiple items are selected.
class ClickListener<T>(private val clickAction: (T) -> Unit) {
    private var hasClicked = false

    fun onClick(item: T) {
        if (!hasClicked) {
            hasClicked = true

            clickAction(item)
        }
    }
}

// Base Diff callback
class DiffCallback<T : BaseModel> : DiffUtil.ItemCallback<T>() {
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}
