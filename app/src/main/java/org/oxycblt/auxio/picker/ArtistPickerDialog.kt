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
 
package org.oxycblt.auxio.picker

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogMusicPickerBinding
import org.oxycblt.auxio.list.ClickableListListener
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.ui.ViewBindingDialogFragment
import org.oxycblt.auxio.util.collectImmediately

/**
 * The base class for dialogs that implements common behavior across all [Artist] pickers. These are
 * shown whenever what to do with an item's [Artist] is ambiguous, as there are multiple [Artist]'s
 * to choose from.
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
abstract class ArtistPickerDialog :
    ViewBindingDialogFragment<DialogMusicPickerBinding>(), ClickableListListener<Artist> {
    protected val pickerModel: PickerViewModel by viewModels()
    // Okay to leak this since the Listener will not be called until after initialization.
    private val artistAdapter = ArtistChoiceAdapter(@Suppress("LeakingThis") this)

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogMusicPickerBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder.setTitle(R.string.lbl_artists).setNegativeButton(R.string.lbl_cancel, null)
    }

    override fun onBindingCreated(binding: DialogMusicPickerBinding, savedInstanceState: Bundle?) {
        binding.pickerRecycler.adapter = artistAdapter

        collectImmediately(pickerModel.artistChoices) { artists ->
            if (artists.isNotEmpty()) {
                // Make sure the artist choices align with any changes in the music library.
                artistAdapter.submitList(artists)
            } else {
                // Not showing any choices, navigate up.
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyBinding(binding: DialogMusicPickerBinding) {
        binding.pickerRecycler.adapter = null
    }

    override fun onClick(item: Artist, viewHolder: RecyclerView.ViewHolder) {
        findNavController().navigateUp()
    }
}
