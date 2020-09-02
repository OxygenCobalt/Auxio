package org.oxycblt.auxio.recycler.viewholders

// Generic ClickListener
class ClickListener<T>(val action: (T) -> Unit) {
    fun onClick(action: T) = action
}
