/*
 * Copyright (c) 2023 Auxio Project
 * MenuDialogFragment.kt is part of Auxio.
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
 
package org.oxycblt.auxio.list.menu

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.children
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.DialogMenuBinding
import org.oxycblt.auxio.list.ClickableListListener
import org.oxycblt.auxio.list.ListViewModel
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.ui.ViewBindingBottomSheetDialogFragment
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.logD

/**
 * A [ViewBindingBottomSheetDialogFragment] that displays basic music information and a series of
 * options.
 *
 * @author Alexander Capehart (OxygenCobalt)
 *
 * TODO: Extend the amount of music info shown in the dialog
 */
abstract class MenuDialogFragment<M : Menu> :
    ViewBindingBottomSheetDialogFragment<DialogMenuBinding>(), ClickableListListener<MenuItem> {
    protected abstract val menuModel: MenuViewModel
    protected abstract val listModel: ListViewModel
    private val menuAdapter = MenuItemAdapter(@Suppress("LeakingThis") this)

    abstract val parcel: Menu.Parcel

    /**
     * Get the options to disable in the context of the currently shown [M].
     *
     * @param menu The currently-shown menu [M].
     */
    abstract fun getDisabledItemIds(menu: M): Set<Int>

    /**
     * Update the displayed information about the currently shown [M].
     *
     * @param binding The [DialogMenuBinding] to bind information to.
     * @param menu The currently-shown menu [M].
     */
    abstract fun updateMenu(binding: DialogMenuBinding, menu: M)

    /**
     * Forward the clicked [MenuItem] to it's corresponding handler in another module.
     *
     * @param item The [MenuItem] that was clicked.
     * @param menu The currently-shown menu [M].
     */
    abstract fun onClick(item: MenuItem, menu: M)

    override fun onCreateBinding(inflater: LayoutInflater) = DialogMenuBinding.inflate(inflater)

    override fun onBindingCreated(binding: DialogMenuBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        // --- UI SETUP ---
        binding.menuName.isSelected = true
        binding.menuInfo.isSelected = true
        binding.menuOptionRecycler.apply {
            adapter = menuAdapter
            itemAnimator = null
        }

        // --- VIEWMODEL SETUP ---
        listModel.menu.consume()
        menuModel.setMenu(parcel)
        collectImmediately(menuModel.currentMenu, this::updateMenu)
    }

    override fun onDestroyBinding(binding: DialogMenuBinding) {
        super.onDestroyBinding(binding)
        binding.menuName.isSelected = false
        binding.menuInfo.isSelected = false
        binding.menuOptionRecycler.adapter = null
    }

    private fun updateMenu(menu: Menu?) {
        if (menu == null) {
            logD("No menu to show, navigating away")
            findNavController().navigateUp()
            return
        }

        @Suppress("UNCHECKED_CAST") val casted = menu as? M
        check(casted != null) { "Unexpected menu instance ${menu::class.simpleName}" }

        // We need to inflate the menu on every menu update since it might have changed
        // what options are available (ex. if an artist with no songs has had new songs added).
        // Since we don't have (and don't want) a dummy view to inflate this menu, just
        // depend on the AndroidX Toolbar internal API and hope for the best.
        @SuppressLint("RestrictedApi") val builder = MenuBuilder(requireContext())
        MenuInflater(requireContext()).inflate(casted.res, builder)

        // Disable any menu options as specified by the impl
        val disabledIds = getDisabledItemIds(casted)
        val visible =
            builder.children.mapTo(mutableListOf()) {
                it.isEnabled = !disabledIds.contains(it.itemId)
                it
            }
        menuAdapter.update(visible, UpdateInstructions.Diff)

        // Delegate to impl how to show music
        updateMenu(requireBinding(), casted)
    }

    final override fun onClick(item: MenuItem, viewHolder: RecyclerView.ViewHolder) {
        // All option selections close the dialog currently.
        // TODO: This should change if the app is 100% migrated to menu dialogs
        findNavController().navigateUp()
        // Delegate to impl on how to handle items
        @Suppress("UNCHECKED_CAST") onClick(item, menuModel.currentMenu.value as M)
    }
}
