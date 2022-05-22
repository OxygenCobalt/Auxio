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

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import org.oxycblt.auxio.databinding.FragmentMainBinding
import org.oxycblt.auxio.music.Music
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.Song
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.ui.MainNavigationAction
import org.oxycblt.auxio.ui.NavigationViewModel
import org.oxycblt.auxio.ui.ViewBindingFragment
import org.oxycblt.auxio.util.logD

/**
 * A wrapper around the home fragment that shows the playback fragment and controls the more
 * high-level navigation features.
 * @author OxygenCobalt
 *
 * TODO: Add a new view with a stack trace whenever the music loading process fails.
 */
class MainFragment : ViewBindingFragment<FragmentMainBinding>() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val navModel: NavigationViewModel by activityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()
    private var callback: DynamicBackPressedCallback? = null

    override fun onCreateBinding(inflater: LayoutInflater) = FragmentMainBinding.inflate(inflater)

    override fun onBindingCreated(binding: FragmentMainBinding, savedInstanceState: Bundle?) {
        // --- UI SETUP ---
        // Build the permission launcher here as you can only do it in onCreateView/onCreate
        val permLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                musicModel.reloadMusic(requireContext())
            }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, DynamicBackPressedCallback().also { callback = it })

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

        // Initialize music loading. Do it here so that it shows on every fragment that this
        // one contains.
        // TODO: Move this to a service [automatic rescanning]
        musicModel.loadMusic(requireContext())

        // Handle the music loader response.
        musicModel.loaderResponse.observe(viewLifecycleOwner) { response ->
            handleLoaderResponse(response, permLauncher)
        }

        navModel.mainNavigationAction.observe(viewLifecycleOwner, ::handleMainNavigation)
        navModel.exploreNavigationItem.observe(viewLifecycleOwner, ::handleExploreNavigation)

        playbackModel.song.observe(viewLifecycleOwner, ::updateSong)
    }

    override fun onResume() {
        super.onResume()
        callback?.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        callback?.isEnabled = false
    }

    private fun handleLoaderResponse(
        response: MusicStore.Response?,
        permLauncher: ActivityResultLauncher<String>
    ) {
        val binding = requireBinding()

        // Handle the loader response.
        when (response) {
            // Ok, start restoring playback now
            is MusicStore.Response.Ok -> playbackModel.setupPlayback(requireContext())

            // Error, show the error to the user
            is MusicStore.Response.Err -> {
                logD("Received Response.Err")
                Snackbar.make(binding.root, R.string.err_load_failed, Snackbar.LENGTH_INDEFINITE)
                    .apply {
                        setAction(R.string.lbl_retry) { musicModel.reloadMusic(context) }
                        show()
                    }
            }
            is MusicStore.Response.NoMusic -> {
                logD("Received Response.NoMusic")
                Snackbar.make(binding.root, R.string.err_no_music, Snackbar.LENGTH_INDEFINITE)
                    .apply {
                        setAction(R.string.lbl_retry) { musicModel.reloadMusic(context) }
                        show()
                    }
            }
            is MusicStore.Response.NoPerms -> {
                logD("Received Response.NoPerms")
                Snackbar.make(binding.root, R.string.err_no_perms, Snackbar.LENGTH_INDEFINITE)
                    .apply {
                        setAction(R.string.lbl_grant) {
                            permLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }
                        show()
                    }
            }
            null -> {}
        }
    }

    private fun handleMainNavigation(action: MainNavigationAction?) {
        if (action == null) return

        val binding = requireBinding()
        when (action) {
            MainNavigationAction.EXPAND -> binding.bottomSheetLayout.expand()
            MainNavigationAction.COLLAPSE -> binding.bottomSheetLayout.collapse()
            MainNavigationAction.SETTINGS ->
                findNavController().navigate(MainFragmentDirections.actionShowSettings())
            MainNavigationAction.ABOUT ->
                findNavController().navigate(MainFragmentDirections.actionShowAbout())
            MainNavigationAction.QUEUE ->
                findNavController().navigate(MainFragmentDirections.actionShowQueue())
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
            binding.bottomSheetLayout.show()
        } else {
            binding.bottomSheetLayout.hide()
        }
    }

    /**
     * A back press callback that handles how to respond to backwards navigation in the detail
     * fragments and the playback panel. TODO: Migrate to new predictive API
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
