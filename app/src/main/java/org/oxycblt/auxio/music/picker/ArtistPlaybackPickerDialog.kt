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
import org.oxycblt.auxio.databinding.DialogMusicPickerBinding
import org.oxycblt.auxio.list.Item
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.androidActivityViewModels

/**
 * The [ArtistPickerDialog] for ambiguous artist playback operations.
 * @author Alexander Capehart (OxygenCobalt)
 */
class ArtistPlaybackPickerDialog : ArtistPickerDialog() {
    private val playbackModel: PlaybackViewModel by androidActivityViewModels()

    private val args: ArtistPlaybackPickerDialogArgs by navArgs()

    override fun onBindingCreated(binding: DialogMusicPickerBinding, savedInstanceState: Bundle?) {
        pickerModel.setSongUid(args.songUid)
        super.onBindingCreated(binding, savedInstanceState)
    }

    override fun onClick(item: Item) {
        super.onClick(item)
        check(item is Artist) { "Unexpected datatype: ${item::class.simpleName}" }
        pickerModel.currentSong.value?.let { song -> playbackModel.playFromArtist(song, item) }
    }
}
