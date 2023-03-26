/*
 * Copyright (c) 2022 Auxio Project
 * SongDetailDialog.kt is part of Auxio.
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

import android.content.Context
import android.os.Bundle
import android.text.format.Formatter
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.DialogSongDetailBinding
import org.oxycblt.auxio.detail.list.SongProperty
import org.oxycblt.auxio.detail.list.SongPropertyAdapter
import org.oxycblt.auxio.list.adapter.UpdateInstructions
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.music.metadata.AudioInfo
import org.oxycblt.auxio.music.resolveNames
import org.oxycblt.auxio.playback.formatDurationMs
import org.oxycblt.auxio.ui.ViewBindingDialogFragment
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.concatLocalized

/**
 * A [ViewBindingDialogFragment] that shows information about a Song.
 *
 * @author Alexander Capehart (OxygenCobalt)
 */
@AndroidEntryPoint
class SongDetailDialog : ViewBindingDialogFragment<DialogSongDetailBinding>() {
    private val detailModel: DetailViewModel by activityViewModels()
    // Information about what song to display is initially within the navigation arguments
    // as a UID, as that is the only safe way to parcel an song.
    private val args: SongDetailDialogArgs by navArgs()
    private val detailAdapter = SongPropertyAdapter()

    override fun onCreateBinding(inflater: LayoutInflater) =
        DialogSongDetailBinding.inflate(inflater)

    override fun onConfigDialog(builder: AlertDialog.Builder) {
        super.onConfigDialog(builder)
        builder.setTitle(R.string.lbl_props).setPositiveButton(R.string.lbl_ok, null)
    }

    override fun onBindingCreated(binding: DialogSongDetailBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)
        binding.detailProperties.adapter = detailAdapter
        // DetailViewModel handles most initialization from the navigation argument.
        detailModel.setSongUid(args.songUid)
        collectImmediately(detailModel.currentSong, detailModel.songAudioInfo, ::updateSong)
    }

    private fun updateSong(song: Song?, info: AudioInfo?) {
        if (song == null) {
            // Song we were showing no longer exists.
            findNavController().navigateUp()
            return
        }

        if (info != null) {
            val context = requireContext()
            detailAdapter.update(
                buildList {
                    add(SongProperty(R.string.lbl_name, song.zipName(context)))
                    add(SongProperty(R.string.lbl_album, song.album.zipName(context)))
                    add(SongProperty(R.string.lbl_artists, song.artists.zipNames(context)))
                    add(SongProperty(R.string.lbl_genres, song.genres.resolveNames(context)))
                    song.date?.let { add(SongProperty(R.string.lbl_date, it.resolveDate(context))) }
                    song.track?.let {
                        add(SongProperty(R.string.lbl_track, getString(R.string.fmt_number, it)))
                    }
                    song.disc?.let {
                        val formattedNumber = getString(R.string.fmt_number, it.number)
                        val zipped =
                            if (it.name != null) {
                                getString(R.string.fmt_zipped_names, formattedNumber, it.name)
                            } else {
                                formattedNumber
                            }
                        add(SongProperty(R.string.lbl_disc, zipped))
                    }
                    add(SongProperty(R.string.lbl_file_name, song.path.name))
                    add(
                        SongProperty(
                            R.string.lbl_relative_path, song.path.parent.resolveName(context)))
                    info.resolvedMimeType.resolveName(context)?.let {
                        add(SongProperty(R.string.lbl_format, it))
                    }
                    add(
                        SongProperty(
                            R.string.lbl_size, Formatter.formatFileSize(context, song.size)))
                    add(SongProperty(R.string.lbl_duration, song.durationMs.formatDurationMs(true)))
                    info.bitrateKbps?.let {
                        add(SongProperty(R.string.lbl_bitrate, getString(R.string.fmt_bitrate, it)))
                    }
                    info.sampleRateHz?.let {
                        add(
                            SongProperty(
                                R.string.lbl_sample_rate, getString(R.string.fmt_sample_rate, it)))
                    }
                },
                UpdateInstructions.Replace(0))
        }
    }

    private fun <T : Music> T.zipName(context: Context) =
        if (rawSortName != null) {
            getString(R.string.fmt_zipped_names, resolveName(context), rawSortName)
        } else {
            resolveName(context)
        }

    private fun <T : Music> List<T>.zipNames(context: Context) =
        concatLocalized(context) { it.zipName(context) }
}
