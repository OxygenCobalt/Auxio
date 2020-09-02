package org.oxycblt.auxio.loading

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
                // depending on which error response was given, along with a retry button

                binding.loadingBar.visibility = View.GONE
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

    private fun onRetry(retry: Boolean) {
        if (retry) {
            binding.loadingBar.visibility = View.VISIBLE
            binding.errorText.visibility = View.GONE
            binding.statusIcon.visibility = View.GONE
            binding.retryButton.visibility = View.GONE

            loadingModel.doneWithRetry()
        }
    }
}
