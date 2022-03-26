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
 
package org.oxycblt.auxio.home.list

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import org.oxycblt.auxio.databinding.FragmentHomeListBinding
import org.oxycblt.auxio.home.HomeViewModel
import org.oxycblt.auxio.home.fastscroll.FastScrollRecyclerView
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.BindingViewHolder
import org.oxycblt.auxio.ui.Item
import org.oxycblt.auxio.ui.MenuItemListener
import org.oxycblt.auxio.ui.MonoAdapter
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.util.applySpans

/**
 * A Base [Fragment] implementing the base features shared across all list fragments in the home UI.
 * @author OxygenCobalt
 */
abstract class HomeListFragment<T : Item> :
    ViewBindingFragment<FragmentHomeListBinding>(),
    MenuItemListener,
    FastScrollRecyclerView.PopupProvider,
    FastScrollRecyclerView.OnFastScrollListener {
    /** The popup provider to use for the fast scroller view. */
    abstract val recyclerId: Int
    abstract val homeAdapter:
        MonoAdapter<T, MenuItemListener, out BindingViewHolder<T, MenuItemListener>>
    abstract val homeData: LiveData<List<T>>

    protected val homeModel: HomeViewModel by activityViewModels()
    protected val playbackModel: PlaybackViewModel by activityViewModels()

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentHomeListBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentHomeListBinding, savedInstanceState: Bundle?) {
        binding.homeRecycler.apply {
            id = recyclerId
            adapter = homeAdapter
            applySpans()
        }

        binding.homeRecycler.popupProvider = this
        binding.homeRecycler.onDragListener = this

        homeData.observe(viewLifecycleOwner) { list ->
            homeAdapter.submitListHard(list.toMutableList())
        }
    }

    override fun onDestroyBinding(binding: FragmentHomeListBinding) {
        homeModel.updateFastScrolling(false)
        binding.homeRecycler.apply {
            adapter = null
            popupProvider = null
            onDragListener = null
        }
    }

    override fun onFastScrollStart() {
        homeModel.updateFastScrolling(true)
    }

    override fun onFastScrollStop() {
        homeModel.updateFastScrolling(false)
    }
}
