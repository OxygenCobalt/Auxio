package org.oxycblt.auxio.list

import android.view.View
import android.widget.Button

/**
 * A basic listener for list interactions.
 */
interface BasicListListener {
    /**
     * Called when an [Item] in the list is clicked.
     * @param item The [Item] that was clicked.
     */
    fun onClick(item: Item)
}

/**
 * An extension of [BasicListListener] that enables menu and selection functionality.
 */
interface ExtendedListListener : BasicListListener {
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
     * @param item The [Item] that this list entry is bound to.
     * @param root The root of the list [View].
     * @param menuButton A [Button] that opens a menu.
     */
    fun bind(item: Item, root: View, menuButton: Button) {
        root.apply {
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
