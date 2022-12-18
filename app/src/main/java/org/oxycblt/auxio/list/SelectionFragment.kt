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
 
package org.oxycblt.auxio.list

import android.view.MenuItem
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.util.showToast

abstract class SelectionFragment<VB : ViewBinding> : MenuFragment<VB>() {
    protected val selectionModel: SelectionViewModel by activityViewModels()

    open fun onClick(music: Music) {
        throw NotImplementedError()
    }

    protected fun setupOverlay(overlay: SelectionToolbarOverlay) {
        overlay.apply {
            setOnSelectionCancelListener { selectionModel.consume() }
            setOnMenuItemClickListener {
                handleSelectionMenuItem(it)
                true
            }
        }
    }

    private fun handleSelectionMenuItem(item: MenuItem) {
        when (item.itemId) {
            R.id.action_play_next -> {
                playbackModel.playNext(selectionModel.consume())
                requireContext().showToast(R.string.lng_queue_added)
            }
            R.id.action_queue_add -> {
                playbackModel.addToQueue(selectionModel.consume())
                requireContext().showToast(R.string.lng_queue_added)
            }
        }
    }

    protected fun handleClick(item: Item) {
        check(item is Music) { "Unexpected datatype: ${item::class.simpleName}" }
        if (selectionModel.selected.value.isNotEmpty()) {
            selectionModel.select(item)
        } else {
            onClick(item)
        }
    }

    protected fun handleSelect(item: Item) {
        check(item is Music) { "Unexpected datatype: ${item::class.simpleName}" }
        selectionModel.select(item)
    }
}
