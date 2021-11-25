/*
 * Copyright (c) 2021 Auxio Project
 * AlbumListFragment.kt is part of Auxio.
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
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.R
import org.oxycblt.auxio.home.HomeFragmentDirections
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.ui.AlbumViewHolder
import org.oxycblt.auxio.ui.DisplayMode
import org.oxycblt.auxio.ui.Sort
import org.oxycblt.auxio.ui.newMenu
import org.oxycblt.auxio.ui.sliceArticle

/**
 * A [HomeListFragment] for showing a list of [Album]s.
 * @author
 */
class AlbumListFragment : HomeListFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter = AlbumAdapter(
            doOnClick = { album ->
                findNavController().navigate(
                    HomeFragmentDirections.actionShowAlbum(album.id)
                )
            },
            ::newMenu
        )

        setupRecycler(R.id.home_album_list, adapter, homeModel.albums)

        return binding.root
    }

    override val listPopupProvider: (Int) -> String
        get() = { idx ->
            val album = homeModel.albums.value!![idx]

            // Change how we display the popup depending on the mode.
            when (homeModel.getSortForDisplay(DisplayMode.SHOW_ALBUMS)) {
                // By Name -> Use Name
                is Sort.ByName -> album.name.sliceArticle()
                    .first().uppercase()

                // By Artist -> Use Artist Name
                is Sort.ByArtist -> album.artist.resolvedName.sliceArticle()
                    .first().uppercase()

                // Year -> Use Full Year
                is Sort.ByYear -> album.year.toString()

                // Unsupported sort, error gracefully
                else -> ""
            }
        }

    class AlbumAdapter(
        private val doOnClick: (data: Album) -> Unit,
        private val doOnLongClick: (view: View, data: Album) -> Unit,
    ) : HomeAdapter<Album, AlbumViewHolder>() {
        override fun getItemCount(): Int = data.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
            return AlbumViewHolder.from(parent.context, doOnClick, doOnLongClick)
        }

        override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
            holder.bind(data[position])
        }
    }
}
