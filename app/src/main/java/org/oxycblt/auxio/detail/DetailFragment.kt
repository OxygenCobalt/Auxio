/*
 * Copyright (c) 2024 Auxio Project
 * DetailFragment.kt is part of Auxio.
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
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.transition.MaterialSharedAxis
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import org.oxycblt.auxio.BuildConfig
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentDetailBinding
import org.oxycblt.auxio.detail.list.DetailListAdapter
import org.oxycblt.auxio.list.ListFragment
import org.oxycblt.auxio.list.ListViewModel
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.getDimenPixels
import org.oxycblt.auxio.util.systemBarInsetsCompat
import org.oxycblt.musikr.Music
import org.oxycblt.musikr.MusicParent

abstract class DetailFragment<P : MusicParent, C : Music> :
    ListFragment<C, FragmentDetailBinding>(),
    DetailListAdapter.Listener<C>,
    AppBarLayout.OnOffsetChangedListener {
    protected val detailModel: DetailViewModel by activityViewModels()
    override val listModel: ListViewModel by activityViewModels()
    override val musicModel: MusicViewModel by activityViewModels()
    override val playbackModel: PlaybackViewModel by activityViewModels()

    private var spacingSmall = 0
    private var lastHadDualPane = false
    private var lastWasScrolled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lastHadDualPane = savedInstanceState?.getBoolean(KEY_DUAL_PANE) ?: false
        lastWasScrolled = savedInstanceState?.getBoolean(KEY_SCROLLED) ?: false
        // Detail transitions are always on the X axis. Shared element transitions are more
        // semantically correct, but are also too buggy to be sensible.
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentDetailBinding.inflate(inflater)

    abstract fun getDetailListAdapter(): DetailListAdapter

    abstract fun getToolbarParent(): P?

    override fun getSelectionToolbar(binding: FragmentDetailBinding) =
        binding.detailSelectionToolbar

    override fun onBindingCreated(binding: FragmentDetailBinding, savedInstanceState: Bundle?) {
        super.onBindingCreated(binding, savedInstanceState)
        // The straightforward "lets save the instance state from the view state"
        // method won't work due to android lifecycle restrictions. Populate in-memory
        // state as a backup for the saved instance state bundle.
        saveStateInMemory(binding)

        // --- UI SETUP ---
        binding.detailAppbar?.addOnOffsetChangedListener(this)

        val pane = binding.detailPane
        if (pane != null) {
            // 2-pane layout, re-adjust toolbar and pane to insets but not recycler,
            // it applies its own insets
            binding.detailToolbar.setOnApplyWindowInsetsListener { view, insets ->
                view.updatePadding(top = insets.systemBarInsetsCompat.top)
                insets
            }
            // prevents duplicate titles
            // not needed for buttons since they are not in the dual-pane layout
            binding.detailNormalToolbar.titleContainer.alpha = 0f
            pane.setOnApplyWindowInsetsListener { view, insets ->
                // todo: rtl awareness
                view.updatePadding(bottom = insets.systemBarInsetsCompat.bottom)
                insets
            }
        }
        val appbar = binding.detailAppbar
        if (appbar != null && savedInstanceState != null) {
            // single-pane, we need to make sure restore the lost collapsing toolbar state
            val panes = savedInstanceState.getBoolean(KEY_DUAL_PANE)
            val scrolled = savedInstanceState.getBoolean(KEY_SCROLLED)
            if (panes && scrolled) {
                appbar.setExpanded(false)
            }
        }

        binding.detailNormalToolbar.apply {
            setNavigationOnClickListener { findNavController().navigateUp() }
            setOnMenuItemClickListener(this@DetailFragment)
            setOnOverflowMenuClick { onOpenParentMenu() }
        }

        binding.detailRecycler.apply { adapter = getDetailListAdapter() }

        spacingSmall = requireContext().getDimenPixels(R.dimen.spacing_small)
    }

    override fun onDestroyBinding(binding: FragmentDetailBinding) {
        // see onBindingCreated
        saveStateInMemory(binding)
        super.onDestroyBinding(binding)
        binding.detailAppbar?.removeOnOffsetChangedListener(this)
        binding.detailNormalToolbar.setOnMenuItemClickListener(null)
        binding.detailRecycler.adapter = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Update in-memory saved state if we can (binding may be null) and then
        // save the instance state from that
        binding?.let(::saveStateInMemory)
        outState.putBoolean(KEY_DUAL_PANE, lastHadDualPane)
        outState.putBoolean(KEY_SCROLLED, lastWasScrolled)
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
        val binding = requireBinding()
        val range = appBarLayout.totalScrollRange
        val ratio = abs(verticalOffset.toFloat()) / range.toFloat()

        val outRatio = min(ratio * 2, 1f)
        val detailHeader = binding.detailHeader
        if (detailHeader != null) {
            detailHeader.scaleX = 1 - 0.2f * outRatio / (5f / 3f)
            detailHeader.scaleY = 1 - 0.2f * outRatio / (5f / 3f)
            detailHeader.alpha = 1 - outRatio
        }

        val inRatio = max(ratio - 0.5f, 0f) * 2
        animateToolbarView(binding.detailNormalToolbar.titleContainer, inRatio)
        animateToolbarActionButton(
            binding.detailNormalToolbar.getMenuButton(R.id.action_play),
            inRatio,
        )
        animateToolbarActionButton(
            binding.detailNormalToolbar.getMenuButton(R.id.action_shuffle),
            inRatio,
        )

        // Enable fast scrolling once fully collapsed
        binding.detailRecycler.fastScrollingEnabled = ratio == 1f
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        if (super.onMenuItemClick(item)) {
            return true
        }

        val parent = getToolbarParent() ?: return false
        return when (item.itemId) {
            R.id.action_play -> {
                onPlayParent(parent)
                true
            }
            R.id.action_shuffle -> {
                onShuffleParent(parent)
                true
            }
            else -> false
        }
    }

    protected fun setToolbarPlaybackButtonsEnabled(
        playEnabled: Boolean,
        shuffleEnabled: Boolean = playEnabled,
    ) {
        binding?.detailNormalToolbar?.apply {
            setMenuItemEnabled(R.id.action_play, playEnabled)
            setMenuItemEnabled(R.id.action_shuffle, shuffleEnabled)
        }
    }

    private fun animateToolbarView(view: View?, ratio: Float) {
        if (view == null) {
            return
        }
        view.alpha = ratio
        view.translationY = spacingSmall * (1 - ratio)
    }

    private fun animateToolbarActionButton(view: View?, ratio: Float) {
        if (view == null) {
            return
        }

        if (ratio <= 0f) {
            view.alpha = 0f
            view.translationY = spacingSmall.toFloat()
            view.visibility = View.GONE
            return
        }

        if (view.visibility != View.VISIBLE) {
            view.visibility = View.VISIBLE
        }
        view.alpha = ratio
        view.translationY = spacingSmall * (1 - ratio)
    }

    private fun saveStateInMemory(binding: FragmentDetailBinding) {
        lastHadDualPane = binding.detailPane != null
        lastWasScrolled = binding.detailRecycler.computeVerticalScrollOffset() > 0f
    }

    protected abstract fun onPlayParent(parent: P)

    protected abstract fun onShuffleParent(parent: P)

    abstract fun onOpenParentMenu()

    private companion object {
        const val KEY_DUAL_PANE = BuildConfig.APPLICATION_ID + ".detail.DUAL_PANE"
        const val KEY_SCROLLED = BuildConfig.APPLICATION_ID + ".detail.SCROLLED"
    }
}
