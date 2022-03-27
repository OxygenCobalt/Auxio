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

import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.R
import org.oxycblt.auxio.home.HomeFragmentDirections
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.ui.GenreViewHolder
import org.oxycblt.auxio.ui.Item
import org.oxycblt.auxio.ui.MenuItemListener
import org.oxycblt.auxio.ui.MonoAdapter
import org.oxycblt.auxio.ui.PrimitiveBackingData
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.ui.sliceArticle
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A [HomeListFragment] for showing a list of [Genre]s.
 * @author
 */
class GenreListFragment : HomeListFragment<Genre>() {
    private val homeAdapter = GenreAdapter(this)

    override fun setupRecycler(recycler: RecyclerView) {
        recycler.apply {
            id = R.id.home_genre_list
            adapter = homeAdapter
        }

        homeModel.genres.observe(viewLifecycleOwner) { list -> homeAdapter.data.submitList(list) }
    }

    override fun getPopup(pos: Int) =
        unlikelyToBeNull(homeModel.genres.value)[pos]
            .resolvedName
            .sliceArticle()
            .first()
            .uppercase()

    override fun onItemClick(item: Item) {
        check(item is Genre)
        findNavController().navigate(HomeFragmentDirections.actionShowGenre(item.id))
    }

    override fun onOpenMenu(item: Item, anchor: View) {
        newMenu(anchor, item)
    }

    class GenreAdapter(listener: MenuItemListener) :
        MonoAdapter<Genre, MenuItemListener, GenreViewHolder>(listener) {
        override val data = PrimitiveBackingData<Genre>(this)
        override val creator = GenreViewHolder.CREATOR
    }
}
