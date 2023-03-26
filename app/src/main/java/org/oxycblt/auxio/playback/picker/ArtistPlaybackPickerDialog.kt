/*
 * Copyright (c) 2022 Auxio Project
 * ArtistPlaybackPickerDialog.kt is part of Auxio.
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
 
package org.oxycblt.auxio.playback.picker

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.StateFlow
import org.oxycblt.auxio.R
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.picker.PickerChoices
import org.oxycblt.auxio.picker.PickerDialogFragment
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * An [ArtistPickerDialog] intended for when [Artist] playback is ambiguous.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class ArtistPlaybackPickerDialog : PickerDialogFragment<Artist>() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val pickerModel: PlaybackPickerViewModel by viewModels()
    // Information about what Song to show choices for is initially within the navigation arguments
    // as UIDs, as that is the only safe way to parcel a Song.
    private val args: ArtistPlaybackPickerDialogArgs by navArgs()

    override val titleRes: Int
        get() = R.string.lbl_artists

    override val pickerChoices: StateFlow<PickerChoices<Artist>?>
        get() = pickerModel.currentArtistChoices

    override fun initChoices() {
        pickerModel.setArtistChoiceUid(args.artistUid)
    }

    override fun onClick(item: Artist, viewHolder: RecyclerView.ViewHolder) {
        super.onClick(item, viewHolder)
        // User made a choice, play the given song from that artist.
        val song = unlikelyToBeNull(pickerModel.currentArtistChoices.value).song
        playbackModel.playFromArtist(song, item)
    }
}
