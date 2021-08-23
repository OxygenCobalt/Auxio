/*
 * Copyright (c) 2021 Auxio Project
 * LoadingFragment.kt is part of Auxio.
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

package org.oxycblt.auxio.loading

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLoadingBinding
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.util.logD

/**
 * Fragment that handles what to display during the loading process.
 * TODO: Figure out how to phase out the loading screen since
 *  Android 12 is annoyingly stubborn about having one splash
 *  screen and one splash screen only.
 * @author OxygenCobalt
 */
class LoadingFragment : Fragment() {
    private val loadingModel: LoadingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentLoadingBinding.inflate(inflater)

        // Build the permission launcher here as you can only do it in onCreateView/onCreate
        val permLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission(), ::onPermResult
        )

        // --- UI SETUP ---

        binding.lifecycleOwner = viewLifecycleOwner
        binding.loadingModel = loadingModel

        // --- VIEWMODEL SETUP ---

        loadingModel.doGrant.observe(viewLifecycleOwner) { doGrant ->
            if (doGrant) {
                permLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                loadingModel.doneWithGrant()
            }
        }

        loadingModel.response.observe(viewLifecycleOwner) { response ->
            when (response) {
                // Success should lead to navigation to the main fragment
                MusicStore.Response.SUCCESS -> findNavController().navigate(
                    LoadingFragmentDirections.actionToMain()
                )

                // Null means that the loading process is going on
                null -> showLoading(binding)

                // Anything else is an error
                else -> showError(binding, response)
            }
        }

        if (hasNoPermissions()) {
            // MusicStore.Response.NO_PERMS isnt actually returned by MusicStore, its just
            // a way to keep the current permission state across device changes
            loadingModel.notifyNoPermissions()
        }

        if (loadingModel.response.value == null) {
            loadingModel.load(requireContext())
        }

        logD("Fragment created")

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        // Navigate away if the music has already been loaded.
        // This causes a memory leak, but there's nothing I can do about it.
        if (loadingModel.loaded) {
            findNavController().navigate(
                LoadingFragmentDirections.actionToMain()
            )
        }
    }

    // --- PERMISSIONS ---

    /**
     * Check if Auxio has the permissions to load music
     */
    private fun hasNoPermissions(): Boolean {
        val needRationale = shouldShowRequestPermissionRationale(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val notGranted = ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_DENIED

        return needRationale || notGranted
    }

    private fun onPermResult(granted: Boolean) {
        if (granted) {
            // If granted, its now safe to load, which will clear the NO_PERMS response
            // we applied earlier.
            loadingModel.load(requireContext())
        }
    }

    // --- UI DISPLAY ---

    /**
     * Hide all error elements and return to the loading view
     */
    private fun showLoading(binding: FragmentLoadingBinding) {
        binding.apply {
            loadingErrorText.visibility = View.INVISIBLE
            loadingActionButton.visibility = View.INVISIBLE
            loadingCircle.visibility = View.VISIBLE
        }
    }

    /**
     * Show an error prompt.
     * @param error The [MusicStore.Response] that this error corresponds to. Ignores
     * [MusicStore.Response.SUCCESS]
     */
    private fun showError(binding: FragmentLoadingBinding, error: MusicStore.Response) {
        binding.loadingCircle.visibility = View.GONE
        binding.loadingErrorText.visibility = View.VISIBLE
        binding.loadingActionButton.visibility = View.VISIBLE

        when (error) {
            MusicStore.Response.NO_MUSIC -> {
                binding.loadingErrorText.text = getString(R.string.err_no_music)
                binding.loadingActionButton.apply {
                    setText(R.string.lbl_retry)
                    setOnClickListener {
                        loadingModel.load(context)
                    }
                }
            }

            MusicStore.Response.FAILED -> {
                binding.loadingErrorText.text = getString(R.string.err_load_failed)
                binding.loadingActionButton.apply {
                    setText(R.string.lbl_retry)
                    setOnClickListener {
                        loadingModel.load(context)
                    }
                }
            }

            MusicStore.Response.NO_PERMS -> {
                binding.loadingErrorText.text = getString(R.string.err_no_perms)
                binding.loadingActionButton.apply {
                    setText(R.string.lbl_grant)
                    setOnClickListener {
                        loadingModel.grant()
                    }
                }
            }

            else -> {}
        }
    }
}
