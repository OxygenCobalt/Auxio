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
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogSongDetailBinding
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.ui.ViewBindingDialogFragment
import org.oxycblt.auxio.util.formatDuration
import org.oxycblt.auxio.util.launch

class SongDetailDialog : ViewBindingDialogFragment<DialogSongDetailBinding>() {
    private val detailModel: DetailViewModel by activityViewModels()

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogSongDetailBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        super.onConfigDialog(builder)
        builder.setTitle(R.string.lbl_props).setPositiveButton(R.string.lbl_ok, null)
    }

    override fun onBindingCreated(binding: DialogSongDetailBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)
        detailModel.setSongId(requireNotNull(arguments).getLong(ARG_ID))
        launch { detailModel.currentSong.collect(::updateSong) }
    }

    override fun onDestroy() {
        super.onDestroy()
        detailModel.clearSong()
    }

    private fun updateSong(song: DetailViewModel.DetailSong?) {
        val binding = requireBinding()

        if (song != null) {
            binding.detailContainer.isGone = false
            binding.detailFileName.setText(song.song.path.name)
            binding.detailRelativeDir.setText(song.song.path.parent.resolveName(requireContext()))
            binding.detailFormat.setText(song.resolvedMimeType.resolveName(requireContext()))
            binding.detailSize.setText(Formatter.formatFileSize(requireContext(), song.song.size))
            binding.detailDuration.setText(song.song.durationSecs.formatDuration(true))

            if (song.bitrateKbps != null) {
                binding.detailBitrate.setText(getString(R.string.fmt_bitrate, song.bitrateKbps))
            } else {
                binding.detailBitrate.setText(R.string.def_bitrate)
            }

            if (song.sampleRate != null) {
                binding.detailSampleRate.setText(
                    getString(R.string.fmt_sample_rate, song.sampleRate))
            } else {
                binding.detailSampleRate.setText(R.string.def_sample_rate)
            }
        } else {
            binding.detailContainer.isGone = true
        }
    }

    companion object {
        fun from(song: Song): SongDetailDialog {
            val instance = SongDetailDialog()
            instance.arguments = Bundle().apply { putLong(ARG_ID, song.id) }
            return instance
        }

        const val TAG = BuildConfig.APPLICATION_ID + ".tag.SONG_DETAILS"
        private const val ARG_ID = BuildConfig.APPLICATION_ID + ".arg.SONG_ID"
    }
}
