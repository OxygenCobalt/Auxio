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

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialFadeThrough
import org.oxycblt.auxio.databinding.FragmentMainBinding
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.MainNavigationAction
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.ui.fragment.ViewBindingFragment
import org.oxycblt.auxio.util.androidActivityViewModels
import org.oxycblt.auxio.util.collect
import org.oxycblt.auxio.util.collectImmediately

/**
 * A wrapper around the home fragment that shows the playback fragment and controls the more
 * high-level navigation features.
 * @author OxygenCobalt
 */
class MainFragment : ViewBindingFragment<FragmentMainBinding>() {
    private val playbackModel: PlaybackViewModel by androidActivityViewModels()
    private val navModel: NavigationViewModel by activityViewModels()
    private var callback: DynamicBackPressedCallback? = null

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Auxio's layout completely breaks down when it's window is resized too small,
            // but for some insane reason google decided to cripple the window APIs one could use
            // to limit it's size. So, we just have our own special layout that is shown whenever
            // the screen is too small because of course we have to.
            if (requireActivity().isInMultiWindowMode) {
                val config = resources.configuration
                if (config.screenHeightDp < 250 || config.screenWidthDp < 250) {
                    binding.layoutTooSmall.visibility = View.VISIBLE
                }
            }
        }

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

    private fun handleMainNavigation(action: MainNavigationAction?) {
        if (action == null) return

        val binding = requireBinding()
        when (action) {
            is MainNavigationAction.Expand -> binding.bottomSheetLayout.expand()
            is MainNavigationAction.Collapse -> binding.bottomSheetLayout.collapse()
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
            requireBinding().bottomSheetLayout.collapse()
        }
    }

    private fun updateSong(song: Song?) {
        val binding = requireBinding()
        if (song != null) {
            binding.bottomSheetLayout.isDraggable = true
            binding.bottomSheetLayout.show()
        } else {
            binding.bottomSheetLayout.isDraggable = false
            binding.bottomSheetLayout.hide()
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
            if (!binding.bottomSheetLayout.collapse()) {
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
