package org.oxycblt.auxio.ui.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.Music

/**
 * An adapter that implements selection indicators.
 * @author OxygenCobalt
 */
abstract class SelectionIndicatorAdapter<VH : RecyclerView.ViewHolder> : PlayingIndicatorAdapter<VH>() {
    private var selectedItems = setOf<Music>()

    override fun onBindViewHolder(
        holder: VH,
        position: Int,
        payloads: List<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)

        if (holder is ViewHolder) {
            holder.updateSelectionIndicator(selectedItems.contains(currentList[position]))
        }
    }

    fun updateSelection(items: Set<Music>) {
        val oldSelectedItems = selectedItems

        if (items == oldSelectedItems) {
            return
        }

        selectedItems = items
        for (i in currentList.indices) {
            // TODO: Perhaps add an optimization that allows me to avoid the O(n) iteration
            //  assuming all list items are unique?
            val item = currentList[i]
            if (item !is Music) {
                continue
            }

            if (oldSelectedItems.contains(item) || items.contains(item)) {
                notifyItemChanged(i, PAYLOAD_INDICATOR_CHANGED)
            }
        }
    }

    /** A ViewHolder that can respond to selection indicator updates. */
    abstract class ViewHolder(root: View) : RecyclerView.ViewHolder(root) {
        abstract fun updateSelectionIndicator(isSelected: Boolean)
    }
}

