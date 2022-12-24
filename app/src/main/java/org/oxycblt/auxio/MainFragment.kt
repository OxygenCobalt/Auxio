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
import org.oxycblt.auxio.music.Artist
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackBottomSheetBehavior
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.playback.queue.QueueBottomSheetBehavior
import org.oxycblt.auxio.shared.MainNavigationAction
import org.oxycblt.auxio.shared.NavigationViewModel
import org.oxycblt.auxio.shared.ViewBindingFragment
import org.oxycblt.auxio.util.*

/**
 * A wrapper around the home fragment that shows the playback fragment and controls the more
 * high-level navigation features.
 * @author Alexander Capehart (OxygenCobalt)
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
        super.onBindingCreated(binding, savedInstanceState)

        // --- UI SETUP ---
        val context = requireActivity()
        // Override the back pressed callback so we can map back navigation to collapsing
        // navigation, navigation out of detail views, etc.
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

        val queueSheetBehavior =
            binding.queueSheet.coordinatorLayoutBehavior as QueueBottomSheetBehavior?
        if (queueSheetBehavior != null) {
            // Bottom sheet mode, set up click listeners.
            val playbackSheetBehavior =
                binding.playbackSheet.coordinatorLayoutBehavior as PlaybackBottomSheetBehavior
            unlikelyToBeNull(binding.handleWrapper).setOnClickListener {
                if (playbackSheetBehavior.state == NeoBottomSheetBehavior.STATE_EXPANDED &&
                    queueSheetBehavior.state == NeoBottomSheetBehavior.STATE_COLLAPSED) {
                    // Playback sheet is expanded and queue sheet is collapsed, we can expand it.
                    queueSheetBehavior.state = NeoBottomSheetBehavior.STATE_EXPANDED
                }
            }
        } else {
            // Dual-pane mode, manually style the static queue sheet.
            binding.queueSheet.apply {
                // Emulate the elevated bottom sheet style.
                background =
                    MaterialShapeDrawable.createWithElevationOverlay(context).apply {
                        fillColor = context.getAttrColorCompat(R.attr.colorSurface)
                        elevation = context.getDimen(R.dimen.elevation_normal)
                    }
                // Apply bar insets for the queue's RecyclerView to usee.
                setOnApplyWindowInsetsListener { v, insets ->
                    v.updatePadding(top = insets.systemBarInsetsCompat.top)
                    insets
                }
            }
        }

        // --- VIEWMODEL SETUP ---
        collect(navModel.mainNavigationAction, ::handleMainNavigation)
        collect(navModel.exploreNavigationItem, ::handleExploreNavigation)
        collect(navModel.exploreNavigationArtists, ::handleExplorePicker)
        collectImmediately(playbackModel.song, ::updateSong)
        collect(playbackModel.artistPlaybackPickerSong, ::handlePlaybackArtistPicker)
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
        // We overload CoordinatorLayout far too much to rely on any of it's typical
        // callback functionality. Just update all transitions before every draw. Should
        // probably be cheap enough.
        val binding = requireBinding()
        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackBottomSheetBehavior
        val queueSheetBehavior =
            binding.queueSheet.coordinatorLayoutBehavior as QueueBottomSheetBehavior?

        val playbackRatio = max(playbackSheetBehavior.calculateSlideOffset(), 0f)
        val outPlaybackRatio = 1 - playbackRatio
        val halfOutRatio = min(playbackRatio * 2, 1f)
        val halfInPlaybackRatio = max(playbackRatio - 0.5f, 0f) * 2

        if (queueSheetBehavior != null) {
            // Queue sheet available, the normal transition applies, but it now much be combined
            // with another transition where the playback panel disappears and the playback bar
            // appears as the queue sheet expands.
            val queueRatio = max(queueSheetBehavior.calculateSlideOffset(), 0f)
            val halfOutQueueRatio = min(queueRatio * 2, 1f)
            val halfInQueueRatio = max(queueRatio - 0.5f, 0f) * 2

            binding.playbackBarFragment.alpha = max(1 - halfOutRatio, halfInQueueRatio)
            binding.playbackPanelFragment.alpha = min(halfInPlaybackRatio, 1 - halfOutQueueRatio)
            binding.queueFragment.alpha = queueRatio

            if (playbackModel.song.value != null) {
                // Playback sheet intercepts queue sheet touch events, prevent that from
                // occurring by disabling dragging whenever the queue sheet is expanded.
                playbackSheetBehavior.isDraggable =
                    queueSheetBehavior.state == NeoBottomSheetBehavior.STATE_COLLAPSED
            }
        } else {
            // No queue sheet, fade normally based on the playback sheet
            binding.playbackBarFragment.alpha = 1 - halfOutRatio
            binding.playbackPanelFragment.alpha = halfInPlaybackRatio
        }

        // Fade out the content as the playback panel expands.
        // TODO: Replace with shadow?
        binding.exploreNavHost.apply {
            alpha = outPlaybackRatio
            // Prevent interactions when the content fully fades out.
            isInvisible = alpha == 0f
        }

        // Reduce playback sheet elevation as it expands. This involves both updating the
        // shadow elevation for older versions, and fading out the background drawable
        // containing the elevation overlay.
        binding.playbackSheet.translationZ = elevationNormal * outPlaybackRatio
        playbackSheetBehavior.sheetBackgroundDrawable.alpha = (outPlaybackRatio * 255).toInt()

        // Fade out the playback bar as the panel expands.
        binding.playbackBarFragment.apply {
            // Prevent interactions when the playback bar fully fades out.
            isInvisible = alpha == 0f
            // As the playback bar expands, we also want to subtly translate the bar to
            // align with the top inset. This results in both a smooth transition from the bar
            // to the playback panel's toolbar, but also a correctly positioned playback bar
            // for when the queue sheet expands.
            lastInsets?.let { translationY = it.systemBarInsetsCompat.top * halfOutRatio }
        }

        // Prevent interactions when the playback panell fully fades out.
        binding.playbackPanelFragment.isInvisible = binding.playbackPanelFragment.alpha == 0f

        binding.queueSheet.apply {
            // Queue sheet (not queue content) should fade out with the playback panel.
            alpha = halfInPlaybackRatio
            // Prevent interactions when the queue sheet fully fades out.
            binding.queueSheet.isInvisible = alpha == 0f
        }

        // Prevent interactions when the queue content fully fades out.
        binding.queueFragment.isInvisible = binding.queueFragment.alpha == 0f

        if (playbackModel.song.value == null) {
            // Sometimes lingering drags can un-hide the playback sheet even when we intend to
            // hide it, make sure we keep it hidden.
            tryHideAllSheets()
        }

        // Since the callback is also reliant on the bottom sheets, we must also update it
        // every frame.
        callback.invalidateEnabled()

        return true
    }

    private fun handleMainNavigation(action: MainNavigationAction?) {
        if (action == null) {
            // Nothing to do.
            return
        }

        when (action) {
            is MainNavigationAction.Expand -> tryExpandSheets()
            is MainNavigationAction.Collapse -> tryCollapseSheets()
            // TODO: Figure out how to clear out the selections as one moves between screens.
            is MainNavigationAction.Directions -> findNavController().navigate(action.directions)
        }

        navModel.finishMainNavigation()
    }

    private fun handleExploreNavigation(item: Music?) {
        if (item != null) {
            tryCollapseSheets()
        }
    }

    private fun handleExplorePicker(items: List<Artist>?) {
        if (items != null) {
            // Navigate to the analogous artist picker dialog.
            navModel.mainNavigateTo(
                MainNavigationAction.Directions(
                    MainFragmentDirections.actionPickNavigationArtist(
                        items.map { it.uid }.toTypedArray())))
            navModel.finishExploreNavigation()
        }
    }

    private fun updateSong(song: Song?) {
        if (song != null) {
            tryShowSheets()
        } else {
            tryHideAllSheets()
        }
    }

    private fun handlePlaybackArtistPicker(song: Song?) {
        if (song != null) {
            // Navigate to the analogous artist picker dialog.
            navModel.mainNavigateTo(
                MainNavigationAction.Directions(
                    MainFragmentDirections.actionPickPlaybackArtist(song.uid)))
            playbackModel.finishPlaybackArtistPicker()
        }
    }

    private fun tryExpandSheets() {
        val binding = requireBinding()
        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackBottomSheetBehavior

        if (playbackSheetBehavior.state == NeoBottomSheetBehavior.STATE_COLLAPSED) {
            // Playback sheet is not expanded and not hidden, we can expand it.
            playbackSheetBehavior.state = NeoBottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun tryCollapseSheets() {
        val binding = requireBinding()
        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackBottomSheetBehavior

        if (playbackSheetBehavior.state == NeoBottomSheetBehavior.STATE_EXPANDED) {
            // Make sure the queue is also collapsed here.
            val queueSheetBehavior =
                binding.queueSheet.coordinatorLayoutBehavior as QueueBottomSheetBehavior?
            playbackSheetBehavior.state = NeoBottomSheetBehavior.STATE_COLLAPSED
            queueSheetBehavior?.state = NeoBottomSheetBehavior.STATE_COLLAPSED
        }
    }

    private fun tryShowSheets() {
        val binding = requireBinding()
        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackBottomSheetBehavior

        if (playbackSheetBehavior.state == NeoBottomSheetBehavior.STATE_HIDDEN) {
            val queueSheetBehavior =
                binding.queueSheet.coordinatorLayoutBehavior as QueueBottomSheetBehavior?

            // Queue sheet behavior is either collapsed or expanded, no hiding needed
            queueSheetBehavior?.isDraggable = true

            playbackSheetBehavior.apply {
                // Make sure the view is draggable, at least until the draw checks kick in.
                isDraggable = true
                state = NeoBottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun tryHideAllSheets() {
        val binding = requireBinding()
        val playbackSheetBehavior =
            binding.playbackSheet.coordinatorLayoutBehavior as PlaybackBottomSheetBehavior

        if (playbackSheetBehavior.state != NeoBottomSheetBehavior.STATE_HIDDEN) {
            val queueSheetBehavior =
                binding.queueSheet.coordinatorLayoutBehavior as QueueBottomSheetBehavior?

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
     * A [OnBackPressedCallback] that overrides the back button to first navigate out of
     * internal app components, such as the Bottom Sheets or Explore Navigation.
     */
    private inner class DynamicBackPressedCallback : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            val binding = requireBinding()
            val playbackSheetBehavior =
                binding.playbackSheet.coordinatorLayoutBehavior as PlaybackBottomSheetBehavior
            val queueSheetBehavior =
                binding.queueSheet.coordinatorLayoutBehavior as QueueBottomSheetBehavior?

            // If expanded, collapse the queue sheet first.
            if (queueSheetBehavior != null &&
                queueSheetBehavior.state != NeoBottomSheetBehavior.STATE_COLLAPSED &&
                playbackSheetBehavior.state == NeoBottomSheetBehavior.STATE_EXPANDED) {
                queueSheetBehavior.state = NeoBottomSheetBehavior.STATE_COLLAPSED
                return
            }

            // If expanded, collapse the playback sheet next.
            if (playbackSheetBehavior.state != NeoBottomSheetBehavior.STATE_COLLAPSED &&
                playbackSheetBehavior.state != NeoBottomSheetBehavior.STATE_HIDDEN) {
                playbackSheetBehavior.state = NeoBottomSheetBehavior.STATE_COLLAPSED
                return
            }

            // Then try to navigate out of the explore navigation fragments (i.e Detail Views)
            binding.exploreNavHost.findNavController().navigateUp()
        }

        /**
         * Force this instance to update whether it's enabled or not. If there are no app
         * components that the back button should close first, the instance is disabled and
         * back navigation is delegated to the system.
         *
         * Normally, this callback would have just called the [MainActivity.onBackPressed]
         * if there were no components to close, but that prevents adaptive back navigation
         * from working on Android 14+, so we must do it this way.
         */
        fun invalidateEnabled() {
            val binding = requireBinding()
            val playbackSheetBehavior =
                binding.playbackSheet.coordinatorLayoutBehavior as PlaybackBottomSheetBehavior
            val queueSheetBehavior =
                binding.queueSheet.coordinatorLayoutBehavior as QueueBottomSheetBehavior?
            val exploreNavController = binding.exploreNavHost.findNavController()

            isEnabled =
                queueSheetBehavior?.state == NeoBottomSheetBehavior.STATE_EXPANDED ||
                playbackSheetBehavior.state == NeoBottomSheetBehavior.STATE_EXPANDED ||
                    exploreNavController.currentDestination?.id !=
                        exploreNavController.graph.startDestinationId
        }
    }
}
