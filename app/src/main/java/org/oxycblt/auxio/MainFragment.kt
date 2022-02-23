/*
 * Copyright (c) 2021 Auxio Project
 * MainFragment.kt is part of Auxio.
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
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import org.oxycblt.auxio.databinding.FragmentMainBinding
import org.oxycblt.auxio.detail.DetailViewModel
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.playback.PlaybackViewModel
import org.oxycblt.auxio.util.logD
import org.oxycblt.auxio.util.logW

/**
 * A wrapper around the home fragment that shows the playback fragment and controls
 * the more high-level navigation features.
 * @author OxygenCobalt
 */
class MainFragment : Fragment() {
    private val playbackModel: PlaybackViewModel by activityViewModels()
    private val detailModel: DetailViewModel by activityViewModels()
    private val musicModel: MusicViewModel by activityViewModels()
    private var callback: Callback? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentMainBinding.inflate(inflater)

        // Build the permission launcher here as you can only do it in onCreateView/onCreate
        val permLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            musicModel.reloadMusic(requireContext())
        }

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            Callback(binding).also {
                callback = it
            }
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Auxio's layout completely breaks down when it's window is resized too small,
            // but for some insane reason google decided to cripple the window APIs one could use
            // to limit it's size. So, we just have our own special layout that is shown whenever
            // the screen is too small because of course we have to.
            // Another fun fact: smallestScreenWidthDp is completely bugged and uses the total
            // screen size, even when the window is smaller. This basically borks split screen
            // even more than it already does. Fun!

            if (requireActivity().isInMultiWindowMode) {
                val config = resources.configuration

                if (config.screenHeightDp < 250 || config.screenWidthDp < 250) {
                    binding.layoutTooSmall.visibility = View.VISIBLE
                }
            }
        }

        // --- VIEWMODEL SETUP ---

        // We have to control the bar view from here since using a Fragment in PlaybackLayout
        // would result in annoying UI issues.
        binding.playbackLayout.setup(playbackModel, detailModel, viewLifecycleOwner)

        // Initialize music loading. Do it here so that it shows on every fragment that this
        // one contains.
        musicModel.loadMusic(requireContext())

        // Handle the music loader response.
        musicModel.loaderResponse.observe(viewLifecycleOwner) { response ->
            // Handle the loader response.
            when (response) {
                // Ok, start restoring playback now
                is MusicStore.Response.Ok -> playbackModel.setupPlayback(requireContext())

                // Error, show the error to the user
                is MusicStore.Response.Err -> {
                    logW("Received Error")

                    val errorRes = when (response.kind) {
                        MusicStore.ErrorKind.NO_MUSIC -> R.string.err_no_music
                        MusicStore.ErrorKind.NO_PERMS -> R.string.err_no_perms
                        MusicStore.ErrorKind.FAILED -> R.string.err_load_failed
                    }

                    val snackbar = Snackbar.make(
                        binding.root, getString(errorRes), Snackbar.LENGTH_INDEFINITE
                    )

                    when (response.kind) {
                        MusicStore.ErrorKind.FAILED, MusicStore.ErrorKind.NO_MUSIC -> {
                            snackbar.setAction(R.string.lbl_retry) {
                                musicModel.reloadMusic(requireContext())
                            }
                        }

                        MusicStore.ErrorKind.NO_PERMS -> {
                            snackbar.setAction(R.string.lbl_grant) {
                                permLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            }
                        }
                    }

                    snackbar.show()
                }
                else -> {}
            }
        }

        logD("Fragment Created")

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        callback?.isEnabled = true
    }

    override fun onPause() {
        super.onPause()
        callback?.isEnabled = false
    }

    /**
     * A back press callback that handles how to respond to backwards navigation in the detail
     * fragments and the playback panel.
     */
    inner class Callback(private val binding: FragmentMainBinding) : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (!binding.playbackLayout.collapse()) {
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
}
