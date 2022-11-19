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
 
package org.oxycblt.auxio.music.picker

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.oxycblt.auxio.databinding.DialogMusicPickerBinding
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.util.collectImmediately

/**
 * The [ArtistPickerDialog] for ambiguous artist navigation operations.
 * @author OxygenCobalt
 */
class ArtistNavigationPickerDialog : ArtistPickerDialog() {
    private val navModel: NavigationViewModel by activityViewModels()
    private val args: ArtistNavigationPickerDialogArgs by navArgs()

    override fun onBindingCreated(binding: DialogMusicPickerBinding, savedInstanceState: Bundle?) {
        pickerModel.setArtistUids(args.artistUids)
        super.onBindingCreated(binding, savedInstanceState)
    }

    override fun onItemClick(item: Item) {
        super.onItemClick(item)
        check(item is Music) { "Unexpected datatype: ${item::class.simpleName}" }
        navModel.exploreNavigateTo(item)
    }
}
