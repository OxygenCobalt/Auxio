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
 
package org.oxycblt.auxio.playback

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.color.MaterialColors
import org.oxycblt.auxio.R
import org.oxycblt.auxio.coil.bindAlbumCover
import org.oxycblt.auxio.databinding.FragmentPlaybackBarBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.bindSongInfo
import org.oxycblt.auxio.ui.BottomSheetLayout
import org.oxycblt.auxio.util.getAttrColorSafe
import org.oxycblt.auxio.util.systemBarInsetsCompat

class PlaybackBarFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPlaybackBarBinding.inflate(inflater)

        // -- UI SETUP ---

        binding.root.apply {
            setOnClickListener {
                // This is a dumb and fragile hack but this fragment isn't part of the navigation
                // stack so we can't really do much
                (requireView().parent.parent.parent as BottomSheetLayout).expand()
            }

            setOnLongClickListener {
                playbackModel.song.value?.let { song -> detailModel.navToItem(song) }
                true
            }

            setOnApplyWindowInsetsListener { view, insets ->
                // Since we swipe up this view, we need to make sure it does not collide with
                // any gesture events. So, apply the system gesture insets if present and then
                // only default to the system bar insets when there are no other options.
                val gesturePadding =
                    when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                            insets.getInsets(WindowInsets.Type.systemGestures()).bottom
                        }
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                            @Suppress("DEPRECATION") insets.systemGestureInsets.bottom
                        }
                        else -> 0
                    }

                view.updatePadding(
                    bottom =
                        if (gesturePadding != 0) gesturePadding
                        else insets.systemBarInsetsCompat.bottom)

                insets
            }
        }

        binding.playbackSkipPrev?.setOnClickListener { playbackModel.skipPrev() }

        binding.playbackPlayPause.setOnClickListener { playbackModel.invertPlayingStatus() }

        binding.playbackSkipNext?.setOnClickListener { playbackModel.skipNext() }

        // Deliberately override the progress bar color [in a Lollipop-friendly way] so that
        // we use colorSecondary instead of colorSurfaceVariant. This is because
        // colorSurfaceVariant is used with the assumption that the view that is using it is
        // not elevated and is therefore not colored. This view is elevated.
        binding.playbackProgressBar.trackColor =
            MaterialColors.compositeARGBWithAlpha(
                requireContext().getAttrColorSafe(R.attr.colorSecondary), (255 * 0.2).toInt())

        // -- VIEWMODEL SETUP ---

        playbackModel.song.observe(viewLifecycleOwner) { song ->
            if (song != null) {
                binding.playbackCover.bindAlbumCover(song)
                binding.playbackSong.text = song.resolvedName
                binding.playbackInfo.bindSongInfo(song)
                binding.playbackProgressBar.max = song.seconds.toInt()
            }
        }

        binding.playbackPlayPause.isActivated = playbackModel.isPlaying.value!!
        playbackModel.isPlaying.observe(viewLifecycleOwner) { isPlaying ->
            binding.playbackPlayPause.isActivated = isPlaying
        }

        binding.playbackProgressBar.progress = playbackModel.seconds.value!!.toInt()
        playbackModel.seconds.observe(viewLifecycleOwner) { position ->
            binding.playbackProgressBar.progress = position.toInt()
        }

        return binding.root
    }
}
