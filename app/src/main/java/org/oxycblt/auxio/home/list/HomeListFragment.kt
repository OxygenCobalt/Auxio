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
import org.oxycblt.auxio.databinding.FragmentHomeListBinding
import org.oxycblt.auxio.home.HomeViewModel
import org.oxycblt.auxio.home.fastscroll.FastScrollRecyclerView
import org.oxycblt.auxio.ui.fragment.MenuFragment
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.ui.recycler.MenuItemListener
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.applySpans

/**
 * A Base [Fragment] implementing the base features shared across all list fragments in the home UI.
 * @author OxygenCobalt
 */
abstract class HomeListFragment<T : Item> :
    MenuFragment<FragmentHomeListBinding>(),
    MenuItemListener,
    FastScrollRecyclerView.PopupProvider,
    FastScrollRecyclerView.OnFastScrollListener {
    protected val homeModel: HomeViewModel by androidActivityViewModels()

    override fun onCreateBinding(inflater: LayoutInflater) =
        FragmentHomeListBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentHomeListBinding, savedInstanceState: Bundle?) {
        binding.homeRecycler.popupProvider = this
        binding.homeRecycler.listener = this
        binding.homeRecycler.applySpans()
    }

    override fun onDestroyBinding(binding: FragmentHomeListBinding) {
        homeModel.updateFastScrolling(false)
        binding.homeRecycler.apply {
            adapter = null
            popupProvider = null
            listener = null
        }
    }

    override fun onFastScrollStart() {
        homeModel.updateFastScrolling(true)
    }

    override fun onFastScrollStop() {
        homeModel.updateFastScrolling(false)
    }
}
