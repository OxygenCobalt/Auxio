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
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.ui.fragment.ViewBindingDialogFragment
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.collectImmediately

/**
 * A dialog displayed when "View properties" is selected on a song, showing more information about
 * the properties of the audio file itself.
 * @author OxygenCobalt
 */
class SongDetailDialog : ViewBindingDialogFragment<DialogSongDetailBinding>() {
    private val detailModel: DetailViewModel by androidActivityViewModels()
    private val args: SongDetailDialogArgs by navArgs()

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogSongDetailBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        super.onConfigDialog(builder)
        builder.setTitle(R.string.lbl_props).setPositiveButton(R.string.lbl_ok, null)
    }

    override fun onBindingCreated(binding: DialogSongDetailBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)
        detailModel.setSongUid(args.songUid)
        collectImmediately(detailModel.currentSong, ::updateSong)
    }

    override fun onDestroy() {
        super.onDestroy()
        detailModel.clearSong()
    }

    private fun updateSong(song: DetailViewModel.DetailSong?) {
        val binding = requireBinding()

        if (song != null) {
            if (song.info != null) {
                binding.detailLoading.isInvisible = true
                binding.detailContainer.isInvisible = false

                val context = requireContext()
                binding.detailFileName.setText(song.song.path.name)
                binding.detailRelativeDir.setText(song.song.path.parent.resolveName(context))
                binding.detailFormat.setText(song.info.resolvedMimeType.resolveName(context))
                binding.detailSize.setText(Formatter.formatFileSize(context, song.song.size))
                binding.detailDuration.setText(song.song.durationMs.formatDurationMs(true))

                if (song.info.bitrateKbps != null) {
                    binding.detailBitrate.setText(
                        getString(R.string.fmt_bitrate, song.info.bitrateKbps))
                } else {
                    binding.detailBitrate.setText(R.string.def_bitrate)
                }

                if (song.info.sampleRate != null) {
                    binding.detailSampleRate.setText(
                        getString(R.string.fmt_sample_rate, song.info.sampleRate))
                } else {
                    binding.detailSampleRate.setText(R.string.def_sample_rate)
                }
            } else {
                binding.detailLoading.isInvisible = false
                binding.detailContainer.isInvisible = true
            }
        } else {
            findNavController().navigateUp()
        }
    }
}
