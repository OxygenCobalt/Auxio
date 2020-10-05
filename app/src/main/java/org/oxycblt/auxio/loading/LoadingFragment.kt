package org.oxycblt.auxio.loading

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLoadingBinding
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.processing.MusicLoaderResponse

class LoadingFragment : Fragment(R.layout.fragment_loading) {

    private val musicModel: MusicViewModel by activityViewModels {
        MusicViewModel.Factory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLoadingBinding.inflate(inflater)

        // Set up the permission launcher, as its disallowed outside of onCreate.
        val permLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted: Boolean ->
                // If its actually granted, restart the loading process again.
                if (granted) {
                    wipeViews(binding)

                    musicModel.reload()
                }
            }

        // --- UI SETUP ---

        binding.lifecycleOwner = this
        binding.musicModel = musicModel

        // --- VIEWMODEL SETUP ---

        musicModel.response.observe(viewLifecycleOwner) { onMusicLoadResponse(it, binding) }
        musicModel.doReload.observe(viewLifecycleOwner) { onRetry(it, binding) }
        musicModel.doGrant.observe(viewLifecycleOwner) { onGrant(it, permLauncher) }

        // Force an error screen if the permissions are denied or the prompt needs to be shown.
        if (checkPerms()) {
            onNoPerms(binding)
        } else {
            musicModel.go()
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    // Check for two things:
    // - If Auxio needs to show the rationale for getting the READ_EXTERNAL_STORAGE permission.
    // - If Auxio straight up doesn't have the READ_EXTERNAL_STORAGE permission.
    private fun checkPerms(): Boolean {
        return shouldShowRequestPermissionRationale(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) || ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_DENIED
    }

    private fun onMusicLoadResponse(
        response: MusicLoaderResponse?,
        binding: FragmentLoadingBinding
    ) {
        if (response == MusicLoaderResponse.DONE) {
            findNavController().navigate(
                LoadingFragmentDirections.actionToMain()
            )
        } else {
            binding.loadingErrorText.text =
                if (response == MusicLoaderResponse.NO_MUSIC)
                    getString(R.string.error_no_music)
                else
                    getString(R.string.error_music_load_failed)

            // If the response wasn't a success, then show the specific error message
            // depending on which error response was given, along with a retry button
            binding.loadingBar.visibility = View.GONE
            binding.loadingErrorText.visibility = View.VISIBLE
            binding.loadingErrorIcon.visibility = View.VISIBLE
            binding.loadingRetryButton.visibility = View.VISIBLE
        }
    }

    private fun onNoPerms(binding: FragmentLoadingBinding) {
        // If there are no perms, switch out the view elements as if an error screen was being
        // shown, but show the label that Auxio needs to read external storage to function,
        // along with a GRANT button

        binding.loadingBar.visibility = View.GONE
        binding.loadingErrorIcon.visibility = View.VISIBLE
        binding.loadingGrantButton.visibility = View.VISIBLE
        binding.loadingErrorText.visibility = View.VISIBLE

        binding.loadingErrorText.text = getString(R.string.error_no_perms)
    }

    private fun onRetry(retry: Boolean, binding: FragmentLoadingBinding) {
        if (retry) {
            wipeViews(binding)

            musicModel.doneWithReload()
        }
    }

    private fun onGrant(grant: Boolean, permLauncher: ActivityResultLauncher<String>) {
        if (grant) {
            permLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            musicModel.doneWithGrant()
        }
    }

    // Wipe views and switch back to the plain ProgressBar
    private fun wipeViews(binding: FragmentLoadingBinding) {
        binding.loadingBar.visibility = View.VISIBLE
        binding.loadingErrorText.visibility = View.GONE
        binding.loadingErrorIcon.visibility = View.GONE
        binding.loadingRetryButton.visibility = View.GONE
        binding.loadingGrantButton.visibility = View.GONE
    }
}
