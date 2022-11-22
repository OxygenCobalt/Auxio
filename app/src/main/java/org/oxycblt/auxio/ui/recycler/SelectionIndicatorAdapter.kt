package org.oxycblt.auxio.ui.recycler

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.util.logD

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

    fun updateSelection(items: List<Music>) {
        val oldSelectedItems = selectedItems
        val newSelectedItems = items.toSet()
        if (newSelectedItems == oldSelectedItems) {
            return
        }

        selectedItems = newSelectedItems
        for (i in currentList.indices) {
            // TODO: Perhaps add an optimization that allows me to avoid the O(n) iteration
            //  assuming all list items are unique?
            val item = currentList[i]
            if (item !is Music) {
                continue
            }

            val added = !oldSelectedItems.contains(item) && newSelectedItems.contains(item)
            val removed = oldSelectedItems.contains(item) && !newSelectedItems.contains(item)
            if (added || removed) {
                notifyItemChanged(i, PAYLOAD_INDICATOR_CHANGED)
            }
        }
    }

    /** A ViewHolder that can respond to selection indicator updates. */
    abstract class ViewHolder(root: View) : PlayingIndicatorAdapter.ViewHolder(root) {
        abstract fun updateSelectionIndicator(isSelected: Boolean)
    }
}

