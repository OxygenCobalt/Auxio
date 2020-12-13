package org.oxycblt.auxio.loading

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLoadingBinding
import org.oxycblt.auxio.music.MusicStore
import org.oxycblt.auxio.music.processing.MusicLoaderResponse

/**
 * An intermediary [Fragment] that asks for the READ_EXTERNAL_STORAGE permission and runs
 * the music loading process in the background.
 * @author OxygenCobalt
 */
class LoadingFragment : Fragment(R.layout.fragment_loading) {
    private val loadingModel: LoadingViewModel by activityViewModels {
        LoadingViewModel.Factory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // If the music was already loaded, then don't do it again.
        if (MusicStore.getInstance().loaded) {
            findNavController().navigate(
                LoadingFragmentDirections.actionToMain()
            )

            return null
        }

        val binding = FragmentLoadingBinding.inflate(inflater)

        // Set up the permission launcher, as its disallowed outside of onCreate.
        val permLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted: Boolean ->
                // If its actually granted, restart the loading process again.
                if (granted) {
                    returnToLoading(binding)

                    loadingModel.reload()
                }
            }

        // --- UI SETUP ---

        binding.lifecycleOwner = this
        binding.loadingModel = loadingModel

        // --- VIEWMODEL SETUP ---

        loadingModel.response.observe(viewLifecycleOwner) {
            if (it == MusicLoaderResponse.DONE) {
                findNavController().navigate(
                    LoadingFragmentDirections.actionToMain()
                )
            } else {
                // If the response wasn't a success, then show the specific error message
                // depending on which error response was given, along with a retry button
                binding.loadingErrorText.text =
                    if (it == MusicLoaderResponse.NO_MUSIC)
                        getString(R.string.error_no_music)
                    else
                        getString(R.string.error_music_load_failed)

                showError(binding)
                binding.loadingRetryButton.visibility = View.VISIBLE
            }
        }

        loadingModel.doReload.observe(viewLifecycleOwner) {
            if (it) {
                returnToLoading(binding)
                loadingModel.doneWithReload()
            }
        }

        loadingModel.doGrant.observe(viewLifecycleOwner) {
            if (it) {
                permLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                returnToLoading(binding)
                loadingModel.doneWithGrant()
            }
        }

        // Force an error screen if the permissions are denied or the prompt needs to be shown.
        if (checkPerms()) {
            showError(binding)

            binding.loadingGrantButton.visibility = View.VISIBLE
            binding.loadingErrorText.text = getString(R.string.error_no_perms)
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

    // Remove the loading indicator and show the error groups
    private fun showError(binding: FragmentLoadingBinding) {
        binding.loadingBar.visibility = View.GONE
        binding.loadingErrorIcon.visibility = View.VISIBLE
        binding.loadingErrorText.visibility = View.VISIBLE
    }

    // Wipe views and switch back to the plain ProgressBar
    private fun returnToLoading(binding: FragmentLoadingBinding) {
        binding.loadingBar.visibility = View.VISIBLE
        binding.loadingErrorText.visibility = View.GONE
        binding.loadingErrorIcon.visibility = View.GONE
        binding.loadingRetryButton.visibility = View.GONE
        binding.loadingGrantButton.visibility = View.GONE
    }
}
