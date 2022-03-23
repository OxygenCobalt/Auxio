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
 
package org.oxycblt.auxio.home.tabs

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogTabsBinding
import org.oxycblt.auxio.settings.SettingsManager
import org.oxycblt.auxio.ui.ViewBindingDialogFragment
import org.oxycblt.auxio.util.logD

/**
 * The dialog for customizing library tabs. This dialog does not rely on any specific ViewModel and
 * serializes it's state instead of
 * @author OxygenCobalt
 */
class TabCustomizeDialog : ViewBindingDialogFragment<DialogTabsBinding>() {
    private val settingsManager = SettingsManager.getInstance()
    private var pendingTabs = settingsManager.libTabs

    override fun onCreateBinding(inflater: LayoutInflater) = DialogTabsBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder.setTitle(R.string.set_lib_tabs)

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            logD("Committing tab changes")
            settingsManager.libTabs = pendingTabs
        }

        // Negative button just dismisses, no need for a listener.
        builder.setNegativeButton(android.R.string.cancel, null)
    }

    override fun onBindingCreated(binding: DialogTabsBinding, savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            // Restore any pending tab configurations
            val tabs = Tab.fromSequence(savedInstanceState.getInt(KEY_TABS))
            if (tabs != null) {
                pendingTabs = tabs
            }
        }

        // Set up adapter & drag callback
        val callback = TabDragCallback { pendingTabs }
        val helper = ItemTouchHelper(callback)
        val tabAdapter = TabAdapter(helper, getTabs = { pendingTabs }, onTabSwitch = ::moveTabs)

        callback.addTabAdapter(tabAdapter)

        binding.tabRecycler.apply {
            adapter = tabAdapter
            helper.attachToRecyclerView(this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_TABS, Tab.toSequence(pendingTabs))
    }

    private fun moveTabs(tab: Tab) {
        // Don't find the specific tab [Which might be outdated due to the nature
        // of how ViewHolders are bound], but instead simply look for the mode in
        // the list of pending tabs and update that instead.
        val index = pendingTabs.indexOfFirst { it.mode == tab.mode }
        if (index != -1) {
            val curTab = pendingTabs[index]
            logD("Updating tab $curTab to $tab")
            pendingTabs[index] =
                when (curTab) {
                    is Tab.Visible -> Tab.Invisible(curTab.mode)
                    is Tab.Invisible -> Tab.Visible(curTab.mode)
                }
        }

        (requireDialog() as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE).isEnabled =
            pendingTabs.filterIsInstance<Tab.Visible>().isNotEmpty()
    }

    companion object {
        const val TAG = BuildConfig.APPLICATION_ID + ".tag.TAB_CUSTOMIZE"
        const val KEY_TABS = BuildConfig.APPLICATION_ID + ".key.PENDING_TABS"
    }
}
