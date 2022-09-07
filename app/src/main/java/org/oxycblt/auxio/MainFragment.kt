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
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.NeoBottomSheetBehavior
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
    private val callback = DynamicBackPressedCallback()
    private var lastInsets: WindowInsets? = null

    private val elevationNormal: Float by lifecycleObject { binding ->
        binding.context.getDimen(R.dimen.elevation_normal)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentMainBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentMainBinding, savedInstanceState: Bundle?) {
        // --- UI SETUP ---
        val context = requireActivity()

        context.onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.root.setOnApplyWindowInsetsListener { _, insets ->
            lastInsets = insets
            insets
        }

        // Send meaningful accessibility events for bottom sheets
        ViewCompat.setAccessibilityPaneTitle(
            binding.playbackSheet, context.getString(R.string.lbl_playback))
        ViewCompat.setAccessibilityPaneTitle(
            binding.queueSheet, context.getString(R.string.lbl_queue))

        val queueSheetBehavior = binding.queueSheet.coordinatorLayoutBehavior as QueueSheetBehavior?
        if (queueSheetBehavior != null) {
            val playbackSheetBehavior =
                binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior

            unlikelyToBeNull(binding.handleWrapper).setOnClickListener {
                if (playbackSheetBehavior.state == NeoBottomSheetBehavior.STATE_EXPANDED &&
                    queueSheetBehavior.state == NeoBottomSheetBehavior.STATE_COLLAPSED) {
                    queueSheetBehavior.state = NeoBottomSheetBehavior.STATE_EXPANDED
                }
            }
        } else {
            // Dual-pane mode, color/pad the queue sheet manually.
            binding.queueSheet.apply {
                background =
                    MaterialShapeDrawable.createWithElevationOverlay(context).apply {
                        fillColor = context.getAttrColorCompat(R.attr.colorSurface)
                        elevation = context.getDimen(R.dimen.elevation_normal)
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

    override fun onStop() {
        super.onStop()
        requireBinding().playbackSheet.viewTreeObserver.removeOnPreDrawListener(this)
    }

    override fun onPreDraw(): Boolean {
        // CoordinatorLayout is insane and thus makes bottom sheet callbacks insane. Do our
        // checks before every draw, which is not ideal in the slightest but also has minimal
        // performance impact since we are only mutating attributes used during drawing.
        val binding = requireBinding()

        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior

        val playbackRatio = max(playbackSheetBehavior.calculateSlideOffset(), 0f)

        val outPlaybackRatio = 1 - playbackRatio
        val halfOutRatio = min(playbackRatio * 2, 1f)
        val halfInPlaybackRatio = max(playbackRatio - 0.5f, 0f) * 2

        val queueSheetBehavior = binding.queueSheet.coordinatorLayoutBehavior as QueueSheetBehavior?

        if (queueSheetBehavior != null) {
            // Queue sheet, take queue into account so the playback bar is shown and the playback
            // panel is hidden when the queue sheet is expanded.
            val queueRatio = max(queueSheetBehavior.calculateSlideOffset(), 0f)
            val halfOutQueueRatio = min(queueRatio * 2, 1f)
            val halfInQueueRatio = max(queueRatio - 0.5f, 0f) * 2
            binding.playbackBarFragment.alpha = max(1 - halfOutRatio, halfInQueueRatio)
            binding.playbackPanelFragment.alpha = min(halfInPlaybackRatio, 1 - halfOutQueueRatio)
            binding.queueFragment.alpha = queueRatio

            if (playbackModel.song.value != null) {
                // Hack around the playback sheet intercepting swipe events on the queue bar
                playbackSheetBehavior.isDraggable =
                    queueSheetBehavior.state == NeoBottomSheetBehavior.STATE_COLLAPSED
            }
        } else {
            // No queue sheet, fade normally based on the playback sheet
            binding.playbackBarFragment.alpha = 1 - halfOutRatio
            binding.playbackPanelFragment.alpha = halfInPlaybackRatio
        }

        binding.exploreNavHost.apply {
            alpha = outPlaybackRatio
            isInvisible = alpha == 0f
        }

        binding.playbackSheet.translationZ = elevationNormal * outPlaybackRatio
        playbackSheetBehavior.sheetBackgroundDrawable.alpha = (outPlaybackRatio * 255).toInt()

        binding.playbackBarFragment.apply {
            isInvisible = alpha == 0f
            lastInsets?.let { translationY = it.systemBarInsetsCompat.top * halfOutRatio }
        }

        binding.playbackPanelFragment.isInvisible = binding.playbackPanelFragment.alpha == 0f

        binding.queueSheet.apply {
            alpha = halfInPlaybackRatio
            binding.queueSheet.isInvisible = alpha == 0f
        }

        binding.queueFragment.isInvisible = binding.queueFragment.alpha == 0f

        if (playbackModel.song.value == null) {
            // Sometimes lingering drags can un-hide the playback sheet even when we intend to
            // hide it, make sure we keep it hidden.
            tryHideAll()
        }

        // Since the callback is also reliant on the bottom sheets, we must also update it
        // every frame.
        callback.updateEnabledState()

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
                    .navigate(MainFragmentDirections.actionShowDetails(action.song.uid))
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

        if (playbackSheetBehavior.state == NeoBottomSheetBehavior.STATE_COLLAPSED) {
            // State is collapsed and non-hidden, expand
            playbackSheetBehavior.state = NeoBottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun tryCollapseAll() {
        val binding = requireBinding()
        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior

        if (playbackSheetBehavior.state == NeoBottomSheetBehavior.STATE_EXPANDED) {
            // Make sure the queue is also collapsed here.
            val queueSheetBehavior =
                binding.queueSheet.coordinatorLayoutBehavior as QueueSheetBehavior?

            playbackSheetBehavior.state = NeoBottomSheetBehavior.STATE_COLLAPSED
            queueSheetBehavior?.state = NeoBottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun tryUnhideAll() {
        val binding = requireBinding()
        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior

        if (playbackSheetBehavior.state == NeoBottomSheetBehavior.STATE_HIDDEN) {
            val queueSheetBehavior =
                binding.queueSheet.coordinatorLayoutBehavior as QueueSheetBehavior?

            // Queue sheet behavior is either collapsed or expanded, no hiding needed
            queueSheetBehavior?.isDraggable = true

            playbackSheetBehavior.apply {
                // Make sure the view is draggable, at least until the draw checks kick in.
                isDraggable = true
                state = NeoBottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun tryHideAll() {
        val binding = requireBinding()
        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior

        if (playbackSheetBehavior.state != NeoBottomSheetBehavior.STATE_HIDDEN) {
            val queueSheetBehavior =
                binding.queueSheet.coordinatorLayoutBehavior as QueueSheetBehavior?

            // Make these views non-draggable so the user can't halt the hiding event.

            queueSheetBehavior?.apply {
                isDraggable = false
                state = NeoBottomSheetBehavior.STATE_COLLAPSED
            }

            playbackSheetBehavior.apply {
                isDraggable = false
                state = NeoBottomSheetBehavior.STATE_HIDDEN
            }
        }
    }

    /**
     * A back press callback that handles how to respond to backwards navigation in the detail
     * fragments and the playback panel.
     */
    private inner class DynamicBackPressedCallback : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            val binding = requireBinding()
            val playbackSheetBehavior =
                binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior
            val queueSheetBehavior =
                binding.queueSheet.coordinatorLayoutBehavior as QueueSheetBehavior?

            if (queueSheetBehavior != null &&
                queueSheetBehavior.state != NeoBottomSheetBehavior.STATE_COLLAPSED &&
                playbackSheetBehavior.state == NeoBottomSheetBehavior.STATE_EXPANDED) {
                // Collapse the queue first if it is expanded.
                queueSheetBehavior.state = NeoBottomSheetBehavior.STATE_COLLAPSED
                return
            }

            if (playbackSheetBehavior.state != NeoBottomSheetBehavior.STATE_COLLAPSED &&
                playbackSheetBehavior.state != NeoBottomSheetBehavior.STATE_HIDDEN) {
                // Then collapse the playback sheet.
                playbackSheetBehavior.state = NeoBottomSheetBehavior.STATE_COLLAPSED
                return
            }

            binding.exploreNavHost.findNavController().navigateUp()
        }

        fun updateEnabledState() {
            val binding = requireBinding()
            val playbackSheetBehavior =
                binding.playbackSheet.coordinatorLayoutBehavior as PlaybackSheetBehavior
            val queueSheetBehavior =
                binding.queueSheet.coordinatorLayoutBehavior as QueueSheetBehavior?

            val exploreNavController = binding.exploreNavHost.findNavController()

            isEnabled =
                playbackSheetBehavior.state == NeoBottomSheetBehavior.STATE_EXPANDED ||
                    queueSheetBehavior?.state == NeoBottomSheetBehavior.STATE_EXPANDED ||
                    exploreNavController.currentDestination?.id !=
                        exploreNavController.graph.startDestinationId
        }
    }
}
