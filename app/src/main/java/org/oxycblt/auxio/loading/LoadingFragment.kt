package org.oxycblt.auxio.loading

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLoadingBinding
import org.oxycblt.auxio.music.processing.MusicLoaderResponse

class LoadingFragment : Fragment(R.layout.fragment_loading) {

    private val loadingModel: LoadingViewModel by lazy {
        ViewModelProvider(
            this,
            LoadingViewModel.Factory(
                requireActivity().application
            )
        ).get(LoadingViewModel::class.java)
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
        binding.loadingModel = loadingModel

        loadingModel.musicRepoResponse.observe(
            viewLifecycleOwner,
            { response ->
                onMusicLoadResponse(response)
            }
        )

        loadingModel.doRetry.observe(
            viewLifecycleOwner,
            { retry ->
                onRetry(retry)
            }
        )

        loadingModel.doGrant.observe(
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

                    loadingModel.retry()
                }
            }

        // Force an error screen if the permissions are denied or the prompt needs to be shown.
        // This should be in MusicRepository, but the response comes faster than the view creation
        // itself and therefore causes the error screen to not appear.
        if (checkPerms()) {
            onNoPerms()
        } else {
            loadingModel.go()
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

            if (response == MusicLoaderResponse.DONE) {
                val inflater = TransitionInflater.from(requireContext())
                exitTransition = inflater.inflateTransition(R.transition.transition_to_main)

                this.findNavController().navigate(
                    LoadingFragmentDirections.actionToMain()
                )
            } else {
                // If the response wasn't a success, then show the specific error message
                // depending on which error response was given, along with a retry button
                binding.errorText.visibility = View.VISIBLE
                binding.statusIcon.visibility = View.VISIBLE
                binding.retryButton.visibility = View.VISIBLE

                binding.errorText.text =
                    if (response == MusicLoaderResponse.NO_MUSIC)
                        getString(R.string.error_no_music)
                    else
                        getString(R.string.error_music_load_failed)
            }

            loadingModel.doneWithResponse()
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

            loadingModel.doneWithRetry()
        }
    }

    private fun onGrant(grant: Boolean) {
        if (grant) {
            permLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            loadingModel.doneWithGrant()
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
