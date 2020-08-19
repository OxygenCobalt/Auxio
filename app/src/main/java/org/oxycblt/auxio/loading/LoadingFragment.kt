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
        ViewModelProvider(this, LoadingViewModel.Factory(
            requireActivity().application)
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

        loadingModel.musicRepoResponse.observe(viewLifecycleOwner, Observer { response ->
            onMusicLoadResponse(response)
        })

        Log.d(this::class.simpleName, "Fragment created.")

        return binding.root
    }

    private fun onMusicLoadResponse(response: MusicLoadResponse) {
        if (response == MusicLoadResponse.DONE) {
            this.findNavController().navigate(
                LoadingFragmentDirections.actionToLibrary()
            )
        }
    }
}