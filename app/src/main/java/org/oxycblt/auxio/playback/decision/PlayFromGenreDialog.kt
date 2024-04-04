/*
 * Copyright (c) 2022 Auxio Project
 * PlayFromGenreDialog.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.decision

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
import org.oxycblt.auxio.music.Genre
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.ViewBindingMaterialDialogFragment
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * A picker [ViewBindingMaterialDialogFragment] intended for when [Genre] playback is ambiguous.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class PlayFromGenreDialog :
    ViewBindingMaterialDialogFragment<DialogMusicChoicesBinding>(), ClickableListListener<Genre> {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val pickerModel: PlaybackPickerViewModel by viewModels()
    // Information about what Song to show choices for is initially within the navigation arguments
    // as UIDs, as that is the only safe way to parcel a Song.
    private val args: PlayFromGenreDialogArgs by navArgs()
    private val choiceAdapter = GenrePlaybackChoiceAdapter(this)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        builder.setTitle(R.string.lbl_genres).setNegativeButton(R.string.lbl_cancel, null)
    }

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogMusicChoicesBinding.inflate(inflater)

    override fun onBindingCreated(binding: DialogMusicChoicesBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)

        binding.choiceRecycler.apply {
            itemAnimator = null
            adapter = choiceAdapter
        }

        playbackModel.playbackDecision.consume()
        pickerModel.setPickerSongUid(args.songUid)
        collectImmediately(pickerModel.currentPickerSong, ::updateSong)
    }

    override fun onDestroyBinding(binding: DialogMusicChoicesBinding) {
        super.onDestroyBinding(binding)
        binding.choiceRecycler.adapter = null
    }

    override fun onClick(item: Genre, viewHolder: RecyclerView.ViewHolder) {
        // User made a choice, play the given song from that genre.
        val song = unlikelyToBeNull(pickerModel.currentPickerSong.value)
        playbackModel.playFromGenre(song, item)
        findNavController().navigateUp()
    }

    private fun updateSong(song: Song?) {
        if (song == null) {
            logD("No song to show choices for, navigating away")
            findNavController().navigateUp()
            return
        }
        choiceAdapter.update(song.genres, UpdateInstructions.Replace(0))
    }
}
