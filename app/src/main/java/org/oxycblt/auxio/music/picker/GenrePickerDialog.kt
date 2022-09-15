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
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.ui.fragment.ViewBindingDialogFragment
import org.oxycblt.auxio.ui.recycler.Item
import org.oxycblt.auxio.ui.recycler.ItemClickListener
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A dialog that shows several genre options if the result of an genre-reliant operation is
 * ambiguous.
 * @author OxygenCobalt
 */
class GenrePickerDialog : ViewBindingDialogFragment<DialogMusicPickerBinding>(), ItemClickListener {
    private val pickerModel: PickerViewModel by viewModels()
    private val playbackModel: PlaybackViewModel by androidActivityViewModels()
    private val navModel: NavigationViewModel by activityViewModels()

    private val args: GenrePickerDialogArgs by navArgs()
    private val adapter = GenreChoiceAdapter(this)

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogMusicPickerBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder
            .setTitle(
                when (args.pickerMode) {
                    PickerMode.GO -> R.string.lbl_go_genre
                    PickerMode.PLAY -> R.string.lbl_play_genre
                }
            )
            .setNegativeButton(R.string.lbl_cancel, null)
    }

    override fun onBindingCreated(binding: DialogMusicPickerBinding, savedInstanceState: Bundle?) {
        pickerModel.setSongUid(args.songUid)

        binding.pickerRecycler.adapter = adapter

        collectImmediately(pickerModel.currentSong) { song ->
            if (song != null) {
                adapter.submitList(song.genres)
            } else {
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyBinding(binding: DialogMusicPickerBinding) {
        binding.pickerRecycler.adapter = null
    }

    override fun onItemClick(item: Item) {
        check(item is Genre) { "Unexpected datatype: ${item::class.simpleName}" }
        findNavController().navigateUp()
        when (args.pickerMode) {
            PickerMode.GO -> navModel.exploreNavigateTo(item)
            PickerMode.PLAY -> {
                val song = unlikelyToBeNull(pickerModel.currentSong.value)
                playbackModel.playFromGenre(song, item)
            }
        }
    }
}
