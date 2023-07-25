/*
 * Copyright (c) 2023 Auxio Project
 * SongSortDialog.kt is part of Auxio.
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
 
package org.oxycblt.auxio.home.sort

import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.home.HomeViewModel
import org.oxycblt.auxio.list.Sort
import org.oxycblt.auxio.list.sort.SortDialog

/**
 * A [SortDialog] that controls the [Sort] of [HomeViewModel.songList].
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class SongSortDialog : SortDialog() {
    private val homeModel: HomeViewModel by activityViewModels()

    override fun getInitialSort() = homeModel.songSort

    override fun applyChosenSort(sort: Sort) {
        homeModel.applySongSort(sort)
    }

    override fun getModeChoices() =
        listOf(
            Sort.Mode.ByName,
            Sort.Mode.ByArtist,
            Sort.Mode.ByAlbum,
            Sort.Mode.ByDate,
            Sort.Mode.ByDuration,
            Sort.Mode.ByDateAdded)
}
