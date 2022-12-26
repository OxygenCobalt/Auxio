package org.oxycblt.auxio.list

import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

/**
 * A basic listener for list interactions.
 * TODO: Supply a ViewHolder on clicks (allows editable lists to be standardized into a listener.)
 * @author Alexander Capehart (OxygenCobalt)
 */
interface ClickableListListener {
    /**
     * Called when an [Item] in the list is clicked.
     * @param item The [Item] that was clicked.
     */
    fun onClick(item: Item)
}

/**
 * An extension of [ClickableListListener] that enables menu and selection functionality.
 * @author Alexander Capehart (OxygenCobalt)
 */
interface SelectableListListener : ClickableListListener {
    /**
     * Called when an [Item] in the list requests that a menu related to it should be opened.
     * @param item The [Item] to show a menu for.
     * @param anchor The [View] to anchor the menu to.
     */
    fun onOpenMenu(item: Item, anchor: View)

    /**
     * Called when an [Item] in the list requests that it be selected.
     * @param item The [Item] to select.
     */
    fun onSelect(item: Item)

    /**
     * Binds this instance to a list item.
     * @param viewHolder The [RecyclerView.ViewHolder] to bind.
     * @param item The [Item] that this list entry is bound to.
     * @param menuButton A [Button] that opens a menu.
     */
    fun bind(viewHolder: RecyclerView.ViewHolder, item: Item, menuButton: Button) {
        viewHolder.itemView.apply {
            // Map clicks to the click callback.
            setOnClickListener { onClick(item) }
            // Map long clicks to the selection callback.
            setOnLongClickListener {
                onSelect(item)
                true
            }
        }
        // Map the menu button to the menu opening callback.
        menuButton.setOnClickListener { onOpenMenu(item, it) }
    }
}
