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
 
package org.oxycblt.auxio.detail

import android.os.Bundle
import android.text.format.Formatter
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isInvisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogSongDetailBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.ui.ViewBindingDialogFragment
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.collectImmediately

/**
 * A [ViewBindingDialogFragment] that shows information about a Song.
 * @author Alexander Capehart (OxygenCobalt)
 */
class SongDetailDialog : ViewBindingDialogFragment<DialogSongDetailBinding>() {
    private val detailModel: DetailViewModel by androidActivityViewModels()
    // Information about what song to display is initially within the navigation arguments
    // as a UID, as that is the only safe way to parcel an song.
    private val args: SongDetailDialogArgs by navArgs()

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogSongDetailBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        super.onConfigDialog(builder)
        builder.setTitle(R.string.lbl_props).setPositiveButton(R.string.lbl_ok, null)
    }

    override fun onBindingCreated(binding: DialogSongDetailBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)
        // DetailViewModel handles most initialization from the navigation argument.
        detailModel.setSongUid(args.itemUid)
        collectImmediately(detailModel.currentSong, detailModel.songProperties, ::updateSong)
    }

    private fun updateSong(song: Song?, properties: SongProperties?) {
        if (song == null) {
            // Song we were showing no longer exists.
            findNavController().navigateUp()
            return
        }

        val binding = requireBinding()
        if (properties != null) {
            // Finished loading Song properties, populate and show the list of Song information.
            binding.detailLoading.isInvisible = true
            binding.detailContainer.isInvisible = false

            val context = requireContext()
            binding.detailFileName.setText(song.path.name)
            binding.detailRelativeDir.setText(song.path.parent.resolveName(context))
            binding.detailFormat.setText(properties.resolvedMimeType.resolveName(context))
            binding.detailSize.setText(Formatter.formatFileSize(context, song.size))
            binding.detailDuration.setText(song.durationMs.formatDurationMs(true))

            if (properties.bitrateKbps != null) {
                binding.detailBitrate.setText(
                    getString(R.string.fmt_bitrate, properties.bitrateKbps))
            } else {
                binding.detailBitrate.setText(R.string.def_bitrate)
            }

            if (properties.sampleRateHz != null) {
                binding.detailSampleRate.setText(
                    getString(R.string.fmt_sample_rate, properties.sampleRateHz))
            } else {
                binding.detailSampleRate.setText(R.string.def_sample_rate)
            }
        } else {
            // Loading is still on-going, don't show anything yet.
            binding.detailLoading.isInvisible = false
            binding.detailContainer.isInvisible = true
        }
    }
}
