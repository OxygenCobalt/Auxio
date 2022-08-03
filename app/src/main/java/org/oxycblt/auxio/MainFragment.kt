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
import android.view.ViewTreeObserver
import android.view.WindowInsets
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isInvisible
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.transition.MaterialFadeThrough
import kotlin.math.max
import kotlin.math.min
import org.oxycblt.auxio.databinding.FragmentMainBinding
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackSheetBehavior
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.queue.QueueSheetBehavior
import org.oxycblt.auxio.ui.MainNavigationAction
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.ui.fragment.ViewBindingFragment
import org.oxycblt.auxio.util.*

/**
 * A wrapper around the home fragment that shows the playback fragment and controls the more
 * high-level navigation features.
 * @author OxygenCobalt
 */
class MainFragment :
    ViewBindingFragment<FragmentMainBinding>(), ViewTreeObserver.OnPreDrawListener {
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

        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            lastInsets = insets
            insets
        }

        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior

        val queueSheetBehavior = binding.queueSheet.coordinatorLayoutBehavior as QueueSheetBehavior?

        if (queueSheetBehavior != null) {
            unlikelyToBeNull(binding.handleWrapper).setOnClickListener {
                if (playbackSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED &&
                    queueSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    queueSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
        } else {
            binding.queueSheet.apply {
                background =
                    MaterialShapeDrawable.createWithElevationOverlay(context).apply {
                        fillColor = context.getAttrColorSafe(R.attr.colorSurface).stateList
                        elevation = context.getDimenSafe(R.dimen.elevation_normal)
                    }

                setOnApplyWindowInsetsListener { v, insets ->
                    v.updatePadding(top = insets.systemBarInsetsCompat.top)
                    insets
                }
            }
        }

        // --- VIEWMODEL SETUP ---

        collect(navModel.mainNavigationAction, ::handleMainNavigation)
        collect(navModel.exploreNavigationItem, ::handleExploreNavigation)
        collectImmediately(playbackModel.song, ::updateSong)
    }

    override fun onStart() {
        super.onStart()

        // Callback could still reasonably fire even if we clear the binding, attach/detach
        // our pre-draw listener our listener in onStart/onStop respectively.
        requireBinding().playbackSheet.viewTreeObserver.addOnPreDrawListener(this)
    }

    override fun onResume() {
        super.onResume()
        callback?.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        callback?.isEnabled = false
    }

    override fun onStop() {
        super.onStop()
        requireBinding().playbackSheet.viewTreeObserver.removeOnPreDrawListener(this)
    }

    override fun onPreDraw(): Boolean {
        // CoordinatorLayout is insane and thus makes bottom sheet callbacks insane. Do our
        // checks before every draw.
        val binding = requireBinding()
        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior

        val playbackRatio = max(playbackSheetBehavior.calculateSlideOffset(), 0f)

        val outPlaybackRatio = 1 - playbackRatio
        val halfOutRatio = min(playbackRatio * 2, 1f)
        val halfInPlaybackRatio = max(playbackRatio - 0.5f, 0f) * 2

        binding.exploreNavHost.apply {
            alpha = outPlaybackRatio
            isInvisible = alpha == 0f
        }

        binding.playbackSheet.translationZ =
            requireContext().getDimenSafe(R.dimen.elevation_normal) * outPlaybackRatio
        playbackSheetBehavior.sheetBackgroundDrawable.alpha = (outPlaybackRatio * 255).toInt()

        val queueSheetBehavior = binding.queueSheet.coordinatorLayoutBehavior as QueueSheetBehavior?

        if (queueSheetBehavior != null) {
            val queueRatio = max(queueSheetBehavior.calculateSlideOffset(), 0f)
            val halfOutQueueRatio = min(queueRatio * 2, 1f)
            val halfInQueueRatio = max(queueRatio - 0.5f, 0f) * 2

            binding.playbackBarFragment.apply {
                alpha = max(1 - halfOutRatio, halfInQueueRatio)
                isInvisible = alpha == 0f
                lastInsets?.let { translationY = it.systemBarInsetsCompat.top * halfOutRatio }
            }

            binding.playbackPanelFragment.apply {
                alpha = min(halfInPlaybackRatio, 1 - halfOutQueueRatio)
                isInvisible = alpha == 0f
            }

            binding.queueFragment.apply {
                alpha = queueRatio
                isInvisible = alpha == 0f
            }

            if (playbackModel.song.value != null) {
                // Hack around the playback sheet intercepting swipe events on the queue bar
                playbackSheetBehavior.isDraggable =
                    queueSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED
            } else {
                // Sometimes lingering drags can un-hide the playback sheet even when we intend to
                // hide it, make sure we keep it hidden.
                tryHideAll()
            }
        } else {

            binding.playbackBarFragment.apply {
                alpha = 1 - halfOutRatio
                isInvisible = alpha == 0f
                lastInsets?.let { translationY = it.systemBarInsetsCompat.top * halfOutRatio }
            }

            binding.playbackPanelFragment.apply {
                alpha = halfInPlaybackRatio
                isInvisible = alpha == 0f
            }

            binding.queueSheet.apply {
                alpha = halfInPlaybackRatio
                isInvisible = alpha == 0f
            }

            if (playbackModel.song.value == null) {
                // Sometimes lingering drags can un-hide the playback sheet even when we intend to
                // hide it, make sure we keep it hidden.
                tryHideAll()
            }
        }

        return true
    }

    private fun updateSong(song: Song?) {
        if (song != null) {
            tryUnhideAll()
        } else {
            tryHideAll()
        }
    }

    private fun handleMainNavigation(action: MainNavigationAction?) {
        if (action == null) return

        when (action) {
            is MainNavigationAction.Expand -> tryExpandAll()
            is MainNavigationAction.Collapse -> tryCollapseAll()
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
            tryCollapseAll()
        }
    }

    private fun tryExpandAll() {
        val binding = requireBinding()
        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior

        if (playbackSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN &&
            playbackSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            playbackSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun tryCollapseAll() {
        val binding = requireBinding()
        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior

        if (playbackSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN &&
            playbackSheetBehavior.state != BottomSheetBehavior.STATE_COLLAPSED) {
            val queueSheetBehavior =
                binding.queueSheet.coordinatorLayoutBehavior as QueueSheetBehavior?

            playbackSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            queueSheetBehavior?.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun tryUnhideAll() {
        val binding = requireBinding()
        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior

        if (playbackSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            playbackSheetBehavior.isDraggable = true
            playbackSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            val queueSheetBehavior =
                binding.queueSheet.coordinatorLayoutBehavior as QueueSheetBehavior?

            queueSheetBehavior?.isDraggable = true
        }
    }

    private fun tryHideAll() {
        val binding = requireBinding()
        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior

        if (playbackSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            val queueSheetBehavior =
                binding.queueSheet.coordinatorLayoutBehavior as QueueSheetBehavior?

            queueSheetBehavior?.apply {
                isDraggable = false
                state = BottomSheetBehavior.STATE_COLLAPSED
            }

            playbackSheetBehavior.apply {
                isDraggable = false
                state = BottomSheetBehavior.STATE_HIDDEN
            }
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

            val queueSheetBehavior =
                binding.queueSheet.coordinatorLayoutBehavior as QueueSheetBehavior?

            if (queueSheetBehavior != null &&
                queueSheetBehavior.state != BottomSheetBehavior.STATE_COLLAPSED) {
                queueSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                return
            }

            val playbackSheetBehavior =
                binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior

            if (playbackSheetBehavior.state != BottomSheetBehavior.STATE_COLLAPSED &&
                playbackSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
                playbackSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                return
            }

            val navController = binding.exploreNavHost.findNavController()

            if (navController.currentDestination?.id == navController.graph.startDestinationId) {
                isEnabled = false
                requireActivity().onBackPressed()
                isEnabled = true
            } else {
                navController.navigateUp()
            }
        }
    }
}
