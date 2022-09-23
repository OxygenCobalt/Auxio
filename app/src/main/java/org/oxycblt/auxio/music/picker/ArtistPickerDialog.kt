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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogMusicPickerBinding
import org.oxycblt.auxio.music.Album
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.ui.fragment.ViewBindingDialogFragment
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.ui.recycler.ItemClickListener
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.collectImmediately

/**
 * A dialog that shows several artist options if the result of an artist-reliant operation is
 * ambiguous.
 * @author OxygenCobalt
 *
 * TODO: Clean up the picker flow to reduce the amount of duplication I had to do.
 */
class ArtistPickerDialog : ViewBindingDialogFragment<DialogMusicPickerBinding>(), ItemClickListener {
    private val pickerModel: PickerViewModel by viewModels()
    private val playbackModel: PlaybackViewModel by androidActivityViewModels()
    private val navModel: NavigationViewModel by activityViewModels()

    private val args: ArtistPickerDialogArgs by navArgs()
    private val adapter = ArtistChoiceAdapter(this)

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogMusicPickerBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder
            .setTitle(R.string.lbl_artists)
            .setNegativeButton(R.string.lbl_cancel, null)
    }

    override fun onBindingCreated(binding: DialogMusicPickerBinding, savedInstanceState: Bundle?) {
        pickerModel.setSongUid(args.uid)

        binding.pickerRecycler.adapter = adapter

        collectImmediately(pickerModel.currentItem) { item ->
            when (item) {
                is Song -> adapter.submitList(item.artists)
                is Album -> adapter.submitList(item.artists)
                null -> findNavController().navigateUp()
                else -> error("Invalid datatype: ${item::class.java}")
            }
        }
    }

    override fun onDestroyBinding(binding: DialogMusicPickerBinding) {
        binding.pickerRecycler.adapter = null
    }

    override fun onItemClick(item: Item) {
        check(item is Artist) { "Unexpected datatype: ${item::class.simpleName}" }
        findNavController().navigateUp()
        when (args.pickerMode) {
            PickerMode.SHOW -> navModel.exploreNavigateTo(item)
            PickerMode.PLAY -> {
                val currentItem = pickerModel.currentItem.value
                check(currentItem is Song) { "PickerMode.PLAY is only allowed with Songs" }
                playbackModel.playFromArtist(currentItem, item)
            }
        }
    }
}
