package org.oxycblt.auxio.loading

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLoadingBinding
import org.oxycblt.auxio.music.processing.MusicLoaderResponse

class LoadingFragment : Fragment() {

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
        permLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission())
            { granted: Boolean ->

                // If its actually granted, restart the loading process again.
                if (granted) {
                    binding.loadingBar.visibility = View.VISIBLE
                    binding.errorText.visibility = View.GONE
                    binding.statusIcon.visibility = View.GONE
                    binding.retryButton.visibility = View.GONE
                    binding.grantButton.visibility = View.GONE

                    loadingModel.retry()
                }
            }

        // This never seems to return true but Im apparently supposed to use it so
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            onMusicLoadResponse(MusicLoaderResponse.NO_PERMS)

        } else {
            loadingModel.go()
        }

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    private fun onMusicLoadResponse(repoResponse: MusicLoaderResponse?) {
        // Don't run this if the value is null, Which is what the value changes to after
        // this is run.
        repoResponse?.let { response ->
            if (response == MusicLoaderResponse.DONE) {
                this.findNavController().navigate(
                    LoadingFragmentDirections.actionToMain()
                )

            } else {
                // If the response wasn't a success, then show the specific error message
                // depending on which error response was given, along with a retry or grant button
                binding.loadingBar.visibility = View.GONE
                binding.errorText.visibility = View.VISIBLE
                binding.statusIcon.visibility = View.VISIBLE

                when (response) {
                    MusicLoaderResponse.NO_PERMS -> {
                        binding.grantButton.visibility = View.VISIBLE
                        binding.errorText.text = getString(R.string.error_no_perms)
                    }

                    MusicLoaderResponse.NO_MUSIC -> {
                        binding.retryButton.visibility = View.VISIBLE
                        binding.errorText.text = getString(R.string.error_no_music)
                    }

                    else -> {
                        binding.retryButton.visibility = View.VISIBLE
                        binding.errorText.text = getString(R.string.error_music_load_failed)
                    }
                }
            }

            loadingModel.doneWithResponse()
        }
    }

    private fun onRetry(retry: Boolean) {
        if (retry) {
            binding.loadingBar.visibility = View.VISIBLE
            binding.errorText.visibility = View.GONE
            binding.statusIcon.visibility = View.GONE
            binding.retryButton.visibility = View.GONE
            binding.grantButton.visibility = View.GONE

            loadingModel.doneWithRetry()
        }
    }

    private fun onGrant(grant: Boolean) {
        if (grant) {
            permLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

            loadingModel.doneWithGrant()
        }
    }
}
