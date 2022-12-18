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
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogMusicPickerBinding
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.list.ItemClickCallback
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.shared.ViewBindingDialogFragment
import org.oxycblt.auxio.util.collectImmediately

abstract class ArtistPickerDialog : ViewBindingDialogFragment<DialogMusicPickerBinding>() {
    protected val pickerModel: MusicPickerViewModel by viewModels()
    private val artistAdapter = ArtistChoiceAdapter(ItemClickCallback(::onChoiceConfirmed))

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogMusicPickerBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder.setTitle(R.string.lbl_artists).setNegativeButton(R.string.lbl_cancel, null)
    }

    override fun onBindingCreated(binding: DialogMusicPickerBinding, savedInstanceState: Bundle?) {
        binding.pickerRecycler.adapter = artistAdapter

        collectImmediately(pickerModel.currentArtists) { artists ->
            if (!artists.isNullOrEmpty()) {
                artistAdapter.submitList(artists)
            } else {
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyBinding(binding: DialogMusicPickerBinding) {
        binding.pickerRecycler.adapter = null
    }

    open fun onChoiceConfirmed(item: Item) {
        check(item is Artist) { "Unexpected datatype: ${item::class.java}" }
        findNavController().navigateUp()
    }
}
