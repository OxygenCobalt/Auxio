package org.oxycblt.auxio.loading

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import org.oxycblt.auxio.R
import org.oxycblt.auxio.databinding.FragmentLoadingBinding
import org.oxycblt.auxio.music.MusicLoadResponse

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
        binding = DataBindingUtil.inflate<FragmentLoadingBinding>(
            inflater, R.layout.fragment_loading, container, false
        )

        binding.lifecycleOwner = this
        binding.loadingModel = loadingModel

        loadingModel.musicRepoResponse.observe(
            viewLifecycleOwner,
            Observer { response ->
                onMusicLoadResponse(response)
            }
        )

        loadingModel.doRetry.observe(
            viewLifecycleOwner,
            Observer { retry ->
                onRetry(retry)
            }
        )

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    private fun onMusicLoadResponse(repoResponse: MusicLoadResponse?) {

        // Don't run this if the value is null, Which is what the value changes to after
        // this is run.
        repoResponse?.let { response ->
            if (response == MusicLoadResponse.DONE) {
                this.findNavController().navigate(
                    LoadingFragmentDirections.actionToLibrary()
                )
            } else {
                // If the response wasn't a success, then show the specific error message
                // depending on which error response was given, along with a retry button

                binding.loadingBar.visibility = View.GONE
                binding.statusText.visibility = View.VISIBLE
                binding.resetButton.visibility = View.VISIBLE

                if (response == MusicLoadResponse.NO_MUSIC) {
                    binding.statusText.text = getString(R.string.error_no_music)
                } else {
                    binding.statusText.text = getString(R.string.error_music_load_failed)
                }
            }

            loadingModel.doneWithResponse()
        }
    }

    private fun onRetry(retry: Boolean) {
        if (retry) {
            binding.loadingBar.visibility = View.VISIBLE
            binding.statusText.visibility = View.GONE
            binding.resetButton.visibility = View.GONE

            loadingModel.doneWithRetry()
        }
    }
}
