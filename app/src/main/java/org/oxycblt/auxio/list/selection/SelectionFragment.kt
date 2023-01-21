/*
 * Copyright (c) 2022 Auxio Project
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
 
package org.oxycblt.auxio.list.selection

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import org.oxycblt.auxio.R
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.showToast

/**
 * A subset of ListFragment that implements aspects of the selection UI.
 * @author Alexander Capehart (OxygenCobalt)
 */
abstract class SelectionFragment<VB : ViewBinding> :
    ViewBindingFragment<VB>(), Toolbar.OnMenuItemClickListener {
    protected val selectionModel: SelectionViewModel by activityViewModels()
    protected val playbackModel: PlaybackViewModel by androidActivityViewModels()

    /**
     * Get the [SelectionToolbarOverlay] of the concrete Fragment to be automatically managed by
     * [SelectionFragment].
     * @return The [SelectionToolbarOverlay] of the concrete [SelectionFragment]'s [VB], or null if
     * there is not one.
     */
    open fun getSelectionToolbar(binding: VB): SelectionToolbarOverlay? = null

    override fun onBindingCreated(binding: VB, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)
        getSelectionToolbar(binding)?.apply {
            // Add cancel and menu item listeners to manage what occurs with the selection.
            setOnSelectionCancelListener { selectionModel.consume() }
            setOnMenuItemClickListener(this@SelectionFragment)
        }
    }

    override fun onDestroyBinding(binding: VB) {
        super.onDestroyBinding(binding)
        getSelectionToolbar(binding)?.setOnMenuItemClickListener(null)
    }

    override fun onMenuItemClick(item: MenuItem) =
        when (item.itemId) {
            R.id.action_selection_play_next -> {
                playbackModel.playNext(selectionModel.consume())
                requireContext().showToast(R.string.lng_queue_added)
                true
            }
            R.id.action_selection_queue_add -> {
                playbackModel.addToQueue(selectionModel.consume())
                requireContext().showToast(R.string.lng_queue_added)
                true
            }
            R.id.action_selection_play -> {
                playbackModel.play(selectionModel.consume())
                true
            }
            R.id.action_selection_shuffle -> {
                playbackModel.shuffle(selectionModel.consume())
                true
            }
            else -> false
        }
}
