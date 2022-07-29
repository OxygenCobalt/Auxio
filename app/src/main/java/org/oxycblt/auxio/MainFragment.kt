/*
 * Copyright (c) 2021 Auxio Project
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
 
package org.oxycblt.auxio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isInvisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.NeoBottomSheetBehavior
import com.google.android.material.transition.MaterialFadeThrough
import kotlin.math.max
import kotlin.math.min
import org.oxycblt.auxio.databinding.FragmentMainBinding
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackSheetBehavior
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.MainNavigationAction
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.ui.fragment.ViewBindingFragment
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.collect
import org.oxycblt.auxio.util.collectImmediately
import org.oxycblt.auxio.util.coordinatorLayoutBehavior

/**
 * A wrapper around the home fragment that shows the playback fragment and controls the more
 * high-level navigation features.
 * @author OxygenCobalt
 */
class MainFragment : ViewBindingFragment<FragmentMainBinding>() {
    private val playbackModel: PlaybackViewModel by androidActivityViewModels()
    private val navModel: NavigationViewModel by activityViewModels()
    private var callback: DynamicBackPressedCallback? = null
    private var lastInsets: WindowInsets? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentMainBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentMainBinding, savedInstanceState: Bundle?) {
        // --- UI SETUP ---
        requireActivity()
            .onBackPressedDispatcher.addCallback(
                viewLifecycleOwner, DynamicBackPressedCallback().also { callback = it })

        binding.root.setOnApplyWindowInsetsListener { v, insets ->
            lastInsets = insets
            insets
        }

        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior

        playbackSheetBehavior.addBottomSheetCallback(
            object : NeoBottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    handleSheetTransitions()
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {}
            })

        binding.root.post { handleSheetTransitions() }

        // --- VIEWMODEL SETUP ---

        collect(navModel.mainNavigationAction, ::handleMainNavigation)
        collect(navModel.exploreNavigationItem, ::handleExploreNavigation)
        collectImmediately(playbackModel.song, ::updateSong)
    }

    override fun onResume() {
        super.onResume()
        callback?.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        callback?.isEnabled = false
    }

    private fun handleSheetTransitions() {
        val binding = requireBinding()
        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior

        val playbackRatio = max(playbackSheetBehavior.calculateSlideOffset(), 0f)
        val queueRatio = 0f

        val outRatio = 1 - playbackRatio
        val halfOutRatio = min(playbackRatio * 2, 1f)
        val halfInPlaybackRatio = max(playbackRatio - 0.5f, 0f) * 2
        val halfOutQueueRatio = min(queueRatio * 2, 1f)
        val halfInQueueRatio = max(queueRatio - 0.5f, 0f) * 2

        playbackSheetBehavior.sheetBackgroundDrawable.alpha = (outRatio * 255).toInt()
        binding.playbackSheet.translationZ = 3f * outRatio
        binding.playbackPanelFragment.alpha = min(halfInPlaybackRatio, 1 - halfOutQueueRatio)
        // binding.queueRecycler.alpha = max(queueOffset, 0f)

        binding.exploreNavHost.apply {
            alpha = outRatio
            isInvisible = alpha == 0f
        }

        binding.playbackBarFragment.apply {
            alpha = max(1 - halfOutRatio, halfInQueueRatio)
            lastInsets?.let { translationY = it.systemWindowInsetTop * halfOutRatio }
        }
    }

    private fun handleMainNavigation(action: MainNavigationAction?) {
        if (action == null) return

        val binding = requireBinding()
        when (action) {
            is MainNavigationAction.Expand -> {
                val playbackSheetBehavior =
                    binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior
                playbackSheetBehavior.state = NeoBottomSheetBehavior.STATE_EXPANDED
            }
            is MainNavigationAction.Collapse -> {
                val playbackSheetBehavior =
                    binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior
                playbackSheetBehavior.state = NeoBottomSheetBehavior.STATE_COLLAPSED
            }
            is MainNavigationAction.Settings ->
                findNavController().navigate(MainFragmentDirections.actionShowSettings())
            is MainNavigationAction.About ->
                findNavController().navigate(MainFragmentDirections.actionShowAbout())
            is MainNavigationAction.SongDetails ->
                findNavController()
                    .navigate(MainFragmentDirections.actionShowDetails(action.song.id))
        }

        navModel.finishMainNavigation()
    }

    private fun handleExploreNavigation(item: Music?) {
        if (item != null) {
            val binding = requireBinding()
            val playbackSheetBehavior =
                binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior

            if (playbackSheetBehavior.state == NeoBottomSheetBehavior.STATE_EXPANDED) {
                playbackSheetBehavior.state = NeoBottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun updateSong(song: Song?) {
        val binding = requireBinding()
        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior
        if (song != null) {
            playbackSheetBehavior.unsideSafe()
        } else {
            playbackSheetBehavior.hideSafe()
        }
    }

    /**
     * A back press callback that handles how to respond to backwards navigation in the detail
     * fragments and the playback panel.
     *
     * TODO: Migrate to new predictive API
     */
    inner class DynamicBackPressedCallback : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            val binding = requireBinding()
            val playbackSheetBehavior =
                binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior
            if (playbackSheetBehavior.state == NeoBottomSheetBehavior.STATE_EXPANDED) {
                playbackSheetBehavior.state = NeoBottomSheetBehavior.STATE_COLLAPSED
            } else {
                val navController = binding.exploreNavHost.findNavController()

                if (navController.currentDestination?.id ==
                    navController.graph.startDestinationId) {
                    isEnabled = false
                    requireActivity().onBackPressed()
                    isEnabled = true
                } else {
                    navController.navigateUp()
                }
            }
        }
    }
}
