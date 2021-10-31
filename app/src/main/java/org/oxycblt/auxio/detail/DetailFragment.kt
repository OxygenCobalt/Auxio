/*
 * Copyright (c) 2021 Auxio Project
 * DetailFragment.kt is part of Auxio.
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

package org.oxycblt.auxio.detail

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.SortMode
import org.oxycblt.auxio.ui.memberBinding
import org.oxycblt.auxio.util.isLandscape

/**
 * A Base [Fragment] implementing the base features shared across all detail fragments.
 * @author OxygenCobalt
 */
abstract class DetailFragment : Fragment() {
    protected val detailModel: DetailViewModel by activityViewModels()
    protected val playbackModel: PlaybackViewModel by activityViewModels()
    protected val binding by memberBinding(FragmentDetailBinding::inflate)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    override fun onResume() {
        super.onResume()

        callback.isEnabled = true
        detailModel.setNavigating(false)
    }

    override fun onPause() {
        super.onPause()
        callback.isEnabled = false
    }

    override fun onStop() {
        super.onStop()

        // Cancel all pending menus when this fragment stops to prevent bugs/crashes
        detailModel.finishShowMenu(null)
    }

    /**
     * Shortcut method for doing setup of the detail toolbar.
     * @param menu Menu resource to use
     * @param onMenuClick (Optional) a click listener for that menu
     */
    protected fun setupToolbar(
        @MenuRes menu: Int = -1,
        onMenuClick: ((itemId: Int) -> Boolean)? = null
    ) {
        binding.detailToolbar.apply {
            if (menu != -1) {
                inflateMenu(menu)
            }

            setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            onMenuClick?.let { onClick ->
                setOnMenuItemClickListener { item ->
                    onClick(item.itemId)
                }
            }
        }
    }

    /**
     * Shortcut method for recyclerview setup
     */
    protected fun setupRecycler(
        detailAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        gridLookup: (Int) -> Boolean
    ) {
        binding.detailRecycler.apply {
            adapter = detailAdapter
            setHasFixedSize(true)

            // Set up a grid if the mode is landscape
            if (requireContext().isLandscape()) {
                layoutManager = GridLayoutManager(requireContext(), 2).also {
                    it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return if (gridLookup(position)) 2 else 1
                        }
                    }
                }
            }
        }
    }

    /**
     * Shortcut method for spinning up the sorting [PopupMenu]
     * @param config The initial configuration to apply to the menu. This is provided by [DetailViewModel.showMenu].
     * @param showItem Which menu items to keep
     */
    protected fun showMenu(config: DetailViewModel.MenuConfig, showItem: ((Int) -> Boolean)? = null) {
        PopupMenu(config.anchor.context, config.anchor).apply {
            inflate(R.menu.menu_detail_sort)

            setOnMenuItemClickListener { item ->
                item.isChecked = true
                detailModel.finishShowMenu(SortMode.fromId(item.itemId)!!)
                true
            }

            setOnDismissListener {
                detailModel.finishShowMenu(null)
            }

            if (showItem != null) {
                menu.forEach { item ->
                    item.isVisible = showItem(item.itemId)
                }
            }

            menu.findItem(config.sortMode.itemId).isChecked = true

            show()
        }
    }

    // Override the back button so that going back will only exit the detail fragments instead of
    // the entire app.
    private val callback = object : OnBackPressedCallback(false) {

        override fun handleOnBackPressed() {
            val navController = findNavController()
            // Check if it's the root of nested fragments in this NavHost
            if (navController.currentDestination?.id == navController.graph.startDestination) {
                isEnabled = false
                requireActivity().onBackPressed()
                isEnabled = true
            } else {
                navController.navigateUp()
            }
        }
    }
}
