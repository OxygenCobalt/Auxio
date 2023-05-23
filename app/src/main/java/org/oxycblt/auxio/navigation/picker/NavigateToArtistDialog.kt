/*
 * Copyright (c) 2022 Auxio Project
 * NavigateToArtistDialog.kt is part of Auxio.
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
 
package org.oxycblt.auxio.navigation.picker

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogMusicChoicesBinding
import org.oxycblt.auxio.list.ClickableListListener
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.navigation.NavigationViewModel
import org.oxycblt.auxio.ui.ViewBindingDialogFragment
import org.oxycblt.auxio.util.collectImmediately

/**
 * A picker [ViewBindingDialogFragment] intended for when [Artist] navigation is ambiguous.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class NavigateToArtistDialog :
    ViewBindingDialogFragment<DialogMusicChoicesBinding>(), ClickableListListener<Artist> {
    private val navigationModel: NavigationViewModel by activityViewModels()
    private val pickerModel: NavigationPickerViewModel by viewModels()
    // Information about what artists to show choices for is initially within the navigation
    // arguments as UIDs, as that is the only safe way to parcel an artist.
    private val args: NavigateToArtistDialogArgs by navArgs()
    private val choiceAdapter = ArtistNavigationChoiceAdapter(this)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder.setTitle(R.string.lbl_artists).setNegativeButton(R.string.lbl_cancel, null)
    }

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogMusicChoicesBinding.inflate(inflater)

    override fun onBindingCreated(binding: DialogMusicChoicesBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.choiceRecycler.apply {
            itemAnimator = null
            adapter = choiceAdapter
        }

        pickerModel.setArtistChoiceUid(args.itemUid)
        collectImmediately(pickerModel.artistChoices) {
            if (it != null) {
                choiceAdapter.update(it.choices, UpdateInstructions.Replace(0))
            } else {
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyBinding(binding: DialogMusicChoicesBinding) {
        super.onDestroyBinding(binding)
        choiceAdapter
    }

    override fun onClick(item: Artist, viewHolder: RecyclerView.ViewHolder) {
        // User made a choice, navigate to the artist.
        navigationModel.exploreNavigateTo(item)
        findNavController().navigateUp()
    }
}
