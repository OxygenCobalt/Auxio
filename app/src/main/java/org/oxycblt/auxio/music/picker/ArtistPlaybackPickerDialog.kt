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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import org.oxycblt.auxio.databinding.DialogMusicPickerBinding
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.requireIs
import org.oxycblt.auxio.util.unlikelyToBeNull

/**
 * An [ArtistPickerDialog] intended for when [Artist] playback is ambiguous.
 * @author Alexander Capehart (OxygenCobalt)
 */
class ArtistPlaybackPickerDialog : ArtistPickerDialog() {
    private val playbackModel: PlaybackViewModel by androidActivityViewModels()
    // Information about what Song to show choices for is initially within the navigation arguments
    // as UIDs, as that is the only safe way to parcel a Song.
    private val args: ArtistPlaybackPickerDialogArgs by navArgs()

    override fun onBindingCreated(binding: DialogMusicPickerBinding, savedInstanceState: Bundle?) {
        pickerModel.setItemUid(args.itemUid)
        super.onBindingCreated(binding, savedInstanceState)
    }

    override fun onClick(item: Artist, viewHolder: RecyclerView.ViewHolder) {
        super.onClick(item, viewHolder)
        // User made a choice, play the given song from that artist.
        val song = requireIs<Song>(unlikelyToBeNull(pickerModel.currentItem.value))
        playbackModel.playFromArtist(song, item)
    }
}
