package org.oxycblt.auxio.library

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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLoadingBinding
import org.oxycblt.auxio.music.MusicViewModel
import org.oxycblt.auxio.music.processing.MusicLoaderResponse

class LoadingFragment : Fragment(R.layout.fragment_loading) {

    private val musicModel: MusicViewModel by activityViewModels {
        MusicViewModel.Factory(requireActivity().application)
    }

    private lateinit var binding: FragmentLoadingBinding
    private lateinit var permLauncher: ActivityResultLauncher<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_loading, container, false
        )

        binding.lifecycleOwner = this
        binding.musicModel = musicModel

        musicModel.response.observe(
            viewLifecycleOwner,
            { response ->
                onMusicLoadResponse(response)
            }
        )

        musicModel.doReload.observe(
            viewLifecycleOwner,
            { retry ->
                onRetry(retry)
            }
        )

        musicModel.doGrant.observe(
            viewLifecycleOwner,
            { grant ->
                onGrant(grant)
            }
        )

        // Set up the permission launcher, as its disallowed outside of onCreate.
        permLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted: Boolean ->
                // If its actually granted, restart the loading process again.
                if (granted) {
                    wipeViews()

                    musicModel.reload()
                }
            }

        // Force an error screen if the permissions are denied or the prompt needs to be shown.
        // This should be in MusicRepository, but the response comes faster than the view creation
        // itself and therefore causes the error screen to not appear.
        if (checkPerms()) {
            onNoPerms()
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

    private fun onMusicLoadResponse(repoResponse: MusicLoaderResponse?) {
        // Don't run this if the value is null, Which is what the value changes to after
        // this is run.
        repoResponse?.let { response ->
            binding.loadingBar.visibility = View.GONE

            if (response != MusicLoaderResponse.DONE) {
                binding.errorText.text =
                    if (response == MusicLoaderResponse.NO_MUSIC)
                        getString(R.string.error_no_music)
                    else
                        getString(R.string.error_music_load_failed)

                // If the response wasn't a success, then show the specific error message
                // depending on which error response was given, along with a retry button
                binding.errorText.visibility = View.VISIBLE
                binding.statusIcon.visibility = View.VISIBLE
                binding.retryButton.visibility = View.VISIBLE
            }

            musicModel.doneWithResponse()
        }
    }

    private fun onNoPerms() {
        // If there are no perms, switch out the view elements as if an error screen was being
        // shown, but show the label that Auxio needs to read external storage to function,
        // along with a GRANT button

        binding.loadingBar.visibility = View.GONE
        binding.errorText.visibility = View.VISIBLE
        binding.statusIcon.visibility = View.VISIBLE
        binding.grantButton.visibility = View.VISIBLE

        binding.errorText.text = getString(R.string.error_no_perms)
    }

    private fun onRetry(retry: Boolean) {
        if (retry) {
            wipeViews()

            musicModel.doneWithReload()
        }
    }

    private fun onGrant(grant: Boolean) {
        if (grant) {
            permLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            musicModel.doneWithGrant()
        }
    }

    // Wipe views and switch back to the plain LoadingBar
    private fun wipeViews() {
        binding.loadingBar.visibility = View.VISIBLE
        binding.errorText.visibility = View.GONE
        binding.statusIcon.visibility = View.GONE
        binding.retryButton.visibility = View.GONE
        binding.grantButton.visibility = View.GONE
    }
}
