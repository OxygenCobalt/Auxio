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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogMusicPickerBinding
import org.oxycblt.auxio.list.ClickableListListener
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.ViewBindingDialogFragment
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.requireIs
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A picker [ViewBindingDialogFragment] intended for when [Genre] playback is ambiguous.
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class GenrePlaybackPickerDialog :
    ViewBindingDialogFragment<DialogMusicPickerBinding>(), ClickableListListener<Genre> {
    private val pickerModel: PickerViewModel by viewModels()
    private val playbackModel: PlaybackViewModel by activityViewModels()
    // Information about what Song to show choices for is initially within the navigation arguments
    // as UIDs, as that is the only safe way to parcel a Song.
    private val args: GenrePlaybackPickerDialogArgs by navArgs()
    // Okay to leak this since the Listener will not be called until after initialization.
    private val genreAdapter = GenreChoiceAdapter(@Suppress("LeakingThis") this)

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogMusicPickerBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder.setTitle(R.string.lbl_genres).setNegativeButton(R.string.lbl_cancel, null)
    }

    override fun onBindingCreated(binding: DialogMusicPickerBinding, savedInstanceState: Bundle?) {
        binding.pickerRecycler.adapter = genreAdapter

        pickerModel.setItemUid(args.itemUid)
        collectImmediately(pickerModel.genreChoices) { genres ->
            if (genres.isNotEmpty()) {
                // Make sure the genre choices align with any changes in the music library.
                genreAdapter.submitList(genres)
            } else {
                // Not showing any choices, navigate up.
                findNavController().navigateUp()
            }
        }
    }

    override fun onDestroyBinding(binding: DialogMusicPickerBinding) {
        binding.pickerRecycler.adapter = null
    }

    override fun onClick(item: Genre, viewHolder: RecyclerView.ViewHolder) {
        // User made a choice, play the given song from that genre.
        val song = requireIs<Song>(unlikelyToBeNull(pickerModel.currentItem.value))
        playbackModel.playFromGenre(song, item)
    }
}
